package de.siphalor.coat.input;

//- import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

/**
 * A boolean {@link ConfigInput} which displays as a checkbox.
 */
public class CheckBoxConfigInput extends Checkbox implements ConfigInput<Boolean> {
	private InputChangeListener<Boolean> changeListener;

	/**
	 * Constructs a new checkbox input.
	 * @param message     A message to be shown behind the checkbox
	 * @param checked     Whether this checkbox should initially be checked
	 * @param showMessage Whether the message should be displayed
	 */
	public CheckBoxConfigInput(Component message, boolean checked, boolean showMessage) {
		super(
				0,
				0,
				//# if CHECKBOX_SIZE == "EXPLICIT"
				//- 20,
				//- 20,
				//# end
				showMessage ? message : Component.empty() /*# if CHECKBOX_SIZE == "FONT" */,
				Minecraft.getInstance().font/*# end */,
				checked /*# if CHECKBOX_CHANGE_HANDLER */,
				(checkbox, checked1) -> {}/*# end */
		);
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
		return selected();
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
	//# if RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		setPosition(x + width - getWidth(), y);
		render(graphics, mouseX, mouseY, tickDelta);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(Boolean value) {
		if (selected() != value) {
			onPress();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPress() {
		super.onPress();
		changeListener.inputChanged(selected());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChangeListener(InputChangeListener<Boolean> changeListener) {
		this.changeListener = changeListener;
	}
}
