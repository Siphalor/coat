package de.siphalor.coat.util;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * A button widget that only renders as text.
 */
public class TextButtonWidget extends ButtonWidget {
	/**
	 *  The original, untrimmed button text
	 */
	@Getter
	private Text originalMessage;
	private boolean hoverEffect = true;

	/**
	 * Constructs a new instance.
	 *
	 * @param x       The x position
	 * @param y       The y position
	 * @param width   The width of the widget
	 * @param height  The height of the widget
	 * @param message The text to render
	 * @param onPress An action to run when the widget gets triggered
	 */
	public TextButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		setMessage(message);
	}

	public void setHoverEffect(boolean hoverEffect) {
		this.hoverEffect = hoverEffect;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int x = getX();
		int y = getY();
		final CoatColor color = CoatUtil.TEXT_COLOR.withAlpha((int) (alpha * 255F));
		float textY = y + (height - 7) / 2F;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		textRenderer.drawWithShadow(matrices, getMessage(), x, textY, color.getArgb());
		if (isFocused()) {
			CoatUtil.drawStrokeRect(x - 2, y - 2, x + width + 2, y + height + 2, 1, color);
		}
		if (hovered) {
			if (hoverEffect) {
				fill(matrices, x - 1, y - 1, x + width + 1, y + height + 1, CoatUtil.HOVER_BG_COLOR.getArgb());
			}
			if (originalMessage != getMessage()) {
				CoatUtil.renderTooltip(matrices, mouseX, mouseY, originalMessage);
			}
		}
	}

	/**
	 * Sets a new text for this button and trims it appropriately. #intellitrim
	 *
	 * @param text The new button text
	 */
	@Override
	public void setMessage(Text text) {
		originalMessage = text;
		super.setMessage(CoatUtil.intelliTrim(MinecraftClient.getInstance().textRenderer, originalMessage, width));
	}

	/**
	 * Updates the button's width and the trim work on the text.
	 *
	 * @param value The new width
	 */
	@Override
	public void setWidth(int value) {
		super.setWidth(value);
		super.setMessage(CoatUtil.intelliTrim(MinecraftClient.getInstance().textRenderer, originalMessage, width));
	}
}
