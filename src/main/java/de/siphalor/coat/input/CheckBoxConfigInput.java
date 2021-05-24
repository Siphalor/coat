package de.siphalor.coat.input;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CheckBoxConfigInput extends CheckboxWidget implements ConfigInput<Boolean> {
	private InputChangeListener<Boolean> changeListener;

	public CheckBoxConfigInput(Text message, boolean checked, boolean showMessage) {
		super(0, 0, 20, 20, message, checked, showMessage);
	}

	@Override
	public Boolean getValue() {
		return isChecked();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	@Override
	public void tick() {
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x + width - getWidth();
		this.y = y;
		render(matrices, mouseX, mouseY, tickDelta);
	}

	@Override
	public void setValue(Boolean value) {
		if (isChecked() != value) {
			onPress();
		}
	}

	@Override
	public void onPress() {
		super.onPress();
		changeListener.inputChanged(isChecked());
	}

	@Override
	public void setChangeListener(InputChangeListener<Boolean> changeListener) {
		this.changeListener = changeListener;
	}
}
