package de.siphalor.coat.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

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
		super(MinecraftClient.getInstance().textRenderer, 0, 0, 10, 16, "");
		setValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return super.height + 4;
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
		setCursorToStart(); // Required because otherwise the text doesn't render sometimes
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
	public void render(int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x + 2;
		this.y = y + 2;
		this.width = width - 4;
		render(mouseX, mouseY, tickDelta);
	}
}
