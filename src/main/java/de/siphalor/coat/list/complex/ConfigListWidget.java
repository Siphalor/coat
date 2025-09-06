package de.siphalor.coat.list.complex;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.entry.ConfigListEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//- import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigListWidget<V> extends DynamicEntryListWidget<ConfigListEntry<V>> implements ConfigContentWidget {
	private static final String APPEND_BUTTON_TEXT_KEY = Coat.MOD_ID + ".list.append";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component APPEND_BUTTON_TEXT = Component.translatable(APPEND_BUTTON_TEXT_KEY);
	//# else
	//- private static final Component APPEND_BUTTON_TEXT = new TranslatableComponent(APPEND_BUTTON_TEXT_KEY);
	//# end
	private final MutableComponent name;
	private final ConfigEntryHandler<List<V>> entryHandler;
	private final ConfigListEntryFactory<V> entryFactory;
	//# if MC_VERSION_NUMBER >= 11903
	private final Button appendButton = Button.builder(APPEND_BUTTON_TEXT, button -> createEntry(getEntryCount()))
			.size(100, 20)
			.build();
	//# else
	//- private final Button appendButton = new Button(0, 0, 100, 20, APPEND_BUTTON_TEXT, button -> createEntry(getEntryCount()));
	//# end
	private ConfigListEntry<V> dragEntry;

	public ConfigListWidget(Minecraft client, int width, int height, int top, int rowWidth, ConfigContentWidget parent, MutableComponent name, ConfigEntryHandler<List<V>> entryHandler, ConfigListEntryFactory<V> entryFactory) {
		super(client, width, height, top, rowWidth);
		this.name = name;
		this.entryHandler = entryHandler;
		this.entryFactory = entryFactory;
	}

	public ConfigListWidget(Minecraft client, Collection<ConfigListEntry<V>> entries, ResourceLocation background, ConfigContentWidget parent, MutableComponent name, ConfigEntryHandler<List<V>> entryHandler, ConfigListEntryFactory<V> entryFactory) {
		super(client, entries, background);
		this.name = name;
		this.entryHandler = entryHandler;
		this.entryFactory = entryFactory;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean result = super.mouseClicked(mouseX, mouseY, button);
		if (mouseX < left + 20) {
			dragEntry = getEntryAtPosition(mouseX, mouseY);
			if (dragEntry != null) {
				setDragging(true);
				dragEntry.setDragFollow(true);
				return true;
			}
		}
		if (!result) {
			return appendButton.mouseClicked(mouseX, mouseY, button);
		}
		return result;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		boolean result = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		if (dragEntry != null && dragEntry.isDragFollow()) {
			int oldPos = entries().indexOf(dragEntry);
			if (oldPos < 0) {
				return result;
			}

			int newPos = getEntryAtY((int) mouseY);
			if (oldPos != newPos) {
				removeEntry(oldPos);
				addEntry(Math.min(newPos, getEntryCount()), dragEntry);
				return true;
			}
		}
		return result;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		setDragging(false);
		if (dragEntry != null) {
			dragEntry.setDragFollow(false);
		}
		dragEntry = null;
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public ConfigTreeEntry getTreeEntry() {
		return new ConfigTreeEntry(name.withStyle(style -> style.withUnderlined(true)), this, true);
	}

	@Override
	public Component getName() {
		return name;
	}

	public List<V> getValue() {
		return entries().stream().map(ConfigListEntry::getValue).collect(Collectors.toList());
	}

	public Collection<Message> getMessages() {
		List<Message> messages = entries().stream().flatMap(entry -> entry.getMessages().stream()).collect(Collectors.toList());
		messages.addAll(entryHandler.getMessages(getValue()));
		return messages;
	}

	@Override
	public void save() {
		entryHandler.save(getValue());
	}

	public void createEntry(int pos) {
		addEntry(pos, entryFactory.create());
	}

	@Override
	public ConfigListEntry<V> removeEntry(ConfigListEntry<V> entry) {
		return super.removeEntry(entry);
	}

	@Override
	protected int getMaxPosition() {
		return super.getMaxPosition() + CoatUtil.DOUBLE_MARGIN + appendButton.getHeight();
	}

	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void renderWidget(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# end
		super.renderWidget(graphics, mouseX, mouseY, delta);
		CoatUtil.setButtonPosition(
				appendButton,
				left + (width - appendButton.getWidth()) / 2,
				super.getEntryAreaTop() + super.getMaxPosition()
		);
		appendButton.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void setFocused(@Nullable GuiEventListener focused) {
		super.setFocused(focused);
		if (focused == appendButton) {
			setScrollAmount(getMaxPosition());
		}
	}

	@Override
	public List<? extends GuiEventListener> children() {
		ArrayList<GuiEventListener> children = new ArrayList<>(entries());
		children.add(appendButton);
		return children;
	}
}
