package de.siphalor.coat.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextButtonWidget extends ButtonWidget {
	private Text originalMessage;

	public TextButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress);
		setMessage(message);
	}

	public Text getOriginalMessage() {
		return originalMessage;
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		final int color = CoatUtil.TEXT_COLOR | MathHelper.ceil(alpha * 255F) << 24;
		float textY = y + (height - 7) / 2F;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		textRenderer.drawWithShadow(matrices, getMessage(), x, textY, color);
		if (isFocused()) {
			CoatUtil.drawStrokeRect(x - 2, y - 2, x + width + 2, y + height + 2, 1, color);
		}
		if (hovered) {
			fill(matrices, x - 1, y - 1, x + width + 1, y + height + 1, 0x33ffffff);
			if (originalMessage != getMessage()) {
				CoatUtil.renderTooltip(matrices, mouseX, mouseY, originalMessage);
			}
		}
	}

	@Override
	public void setMessage(Text text) {
		originalMessage = text;
		super.setMessage(CoatUtil.intelliTrim(MinecraftClient.getInstance().textRenderer, originalMessage, width));
	}

	@Override
	public void setWidth(int value) {
		super.setWidth(value);
		super.setMessage(CoatUtil.intelliTrim(MinecraftClient.getInstance().textRenderer, originalMessage, width));
	}
}
