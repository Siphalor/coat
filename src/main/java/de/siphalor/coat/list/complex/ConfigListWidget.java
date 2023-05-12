package de.siphalor.coat.list.complex;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.entry.ConfigListEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigListWidget<V> extends DynamicEntryListWidget<ConfigListEntry<V>> implements ConfigContentWidget {
	private final MutableText name;
	private final ConfigEntryHandler<List<V>> entryHandler;
	private final ConfigListEntryFactory<V> entryFactory;
	private final ButtonWidget appendButton = ButtonWidget.builder(
			Text.translatable(Coat.MOD_ID + ".list.append"),
			button -> createEntry(getEntryCount())
	).size(100, 20).build();
	private ConfigListEntry<V> dragEntry;

	public ConfigListWidget(MinecraftClient client, int width, int height, int top, int rowWidth, ConfigContentWidget parent, MutableText name, ConfigEntryHandler<List<V>> entryHandler, ConfigListEntryFactory<V> entryFactory) {
		super(client, width, height, top, rowWidth);
		this.name = name;
		this.entryHandler = entryHandler;
		this.entryFactory = entryFactory;
	}

	public ConfigListWidget(MinecraftClient client, Collection<ConfigListEntry<V>> entries, Identifier background, ConfigContentWidget parent, MutableText name, ConfigEntryHandler<List<V>> entryHandler, ConfigListEntryFactory<V> entryFactory) {
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
		return new ConfigTreeEntry(name.styled(style -> style.withUnderline(true)), this, true);
	}

	@Override
	public Text getName() {
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
	public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		super.renderWidget(drawContext, mouseX, mouseY, delta);
		appendButton.setY(super.getEntryAreaTop() + super.getMaxPosition());
		appendButton.setX(left + (width - appendButton.getWidth()) / 2);
		appendButton.render(drawContext, mouseX, mouseY, delta);
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		super.setFocused(focused);
		if (focused == appendButton) {
			setScrollAmount(getMaxPosition());
		}
	}

	@Override
	public List<? extends Element> children() {
		ArrayList<Element> children = new ArrayList<>(entries());
		children.add(appendButton);
		return children;
	}
}
