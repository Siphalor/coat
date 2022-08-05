package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.list.complex.ConfigListWidget;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigListEntry<V> extends ConfigContainerCompoundEntry {
	private static final Identifier HANDLE_TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");

	private final ConfigInput<V> input;
	private final ButtonWidget deleteWidget;
	private boolean dragFollow;

	public ConfigListEntry(ConfigInput<V> input) {
		this.input = input;
		deleteWidget = new ButtonWidget(0, 0, 20, 20, new LiteralText("x"), button -> {
			if (parent instanceof ConfigListWidget) {
				//noinspection unchecked
				((ConfigListWidget<V>) parent).removeEntry(this);
			}
		});
	}

	public void setDragFollow(boolean dragFollow) {
		this.dragFollow = dragFollow;
	}

	public boolean isDragFollow() {
		return dragFollow;
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		if (isDragFollow()) {
			y = mouseY - entryHeight / 2;
		}

		MinecraftClient.getInstance().getTextureManager().bindTexture(HANDLE_TEXTURE);
		drawTexture(matrices, x, y + 2, isDragFollow() ? 244 : 232, 0, 12, 15);
		input.render(matrices, x + 12 + CoatUtil.MARGIN, y, entryWidth - 32 - CoatUtil.DOUBLE_MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		deleteWidget.x = x + entryWidth - 20;
		deleteWidget.y = y;
		deleteWidget.render(matrices, mouseX, mouseY, tickDelta);
	}

	@Override
	public int getHeight() {
		return input.getHeight() + CoatUtil.DOUBLE_MARGIN;
	}

	@Override
	public void tick() {
		input.tickConfigInput();
	}

	public V getValue() {
		return input.getValue();
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth();
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of(input, deleteWidget);
	}
}
