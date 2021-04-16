package de.siphalor.coat.input;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TextConfigInput extends TextFieldWidget implements ConfigInput<String> {
	private final ConfigEntryHandler<String> entryHandler;

	public TextConfigInput(ConfigEntryHandler<String> entryHandler, Text placeholder) {
		super(MinecraftClient.getInstance().textRenderer, 0, 0, 10, 16, placeholder);
		this.entryHandler = entryHandler;
	}

	@Override
	public int getHeight() {
		return super.getHeight() + Coat.MARGIN + Coat.MARGIN;
	}

	@Override
	public String getValue() {
		return getText();
	}

	@Override
	public void setValue(String value) {
		setText(value);
	}

	@Override
	public ConfigEntryHandler<String> getEntryHandler() {
		return entryHandler;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;
		this.width = width;
		render(matrices, mouseX, mouseY, tickDelta);
	}
}
