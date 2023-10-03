package de.siphalor.coat.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * A string input represented as a text field.
 */
public class TextConfigInput extends TextFieldWidget implements ConfigInput<String> {
	/**
	 * Constructs a new text input.
	 *
	 * @param value The initial value of this text field
	 */
	public TextConfigInput(String value) {
		super(MinecraftClient.getInstance().textRenderer, 0, 0, 10, 20, Text.empty());
		setMaxLength(Integer.MAX_VALUE);
		setValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return super.getHeight();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue() {
		return getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(String value) {
		setText(value);
		setCursorToStart(false); // Required because otherwise the text doesn't render sometimes
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChangeListener(InputChangeListener<String> changeListener) {
		setChangedListener(changeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(DrawContext drawContext, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		setPosition(x, y);
		this.width = width;
		render(drawContext, mouseX, mouseY, tickDelta);
	}
}
