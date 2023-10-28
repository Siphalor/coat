package de.siphalor.coat.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

/**
 * A boolean {@link ConfigInput} which displays as a checkbox.
 */
public class CheckBoxConfigInput extends CheckboxWidget implements ConfigInput<Boolean> {
	private InputChangeListener<Boolean> changeListener;

	/**
	 * Constructs a new checkbox input.
	 * @param message     A message to be shown behind the checkbox
	 * @param checked     Whether this checkbox should initially be checked
	 * @param showMessage Whether the message should be displayed
	 */
	public CheckBoxConfigInput(Text message, boolean checked, boolean showMessage) {
		super(0, 0, showMessage ? message : Text.empty(), MinecraftClient.getInstance().textRenderer, checked, (checkbox, checked1) -> {});
	}

	@Override
	public int getPreferredWidth() {
		return getWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean getValue() {
		return isChecked();
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
		setPosition(x + width - getWidth(), y);
		render(drawContext, mouseX, mouseY, tickDelta);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Boolean value) {
		if (isChecked() != value) {
			onPress();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPress() {
		super.onPress();
		changeListener.inputChanged(isChecked());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChangeListener(InputChangeListener<Boolean> changeListener) {
		this.changeListener = changeListener;
	}
}
