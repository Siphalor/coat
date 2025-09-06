package de.siphalor.coat.input;

//- import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * A string input represented as a text field.
 */
public class TextConfigInput extends EditBox implements ConfigInput<String> {
	private static final int AUTO_PADDING = /*# if MC_VERSION_NUMBER >= 12002 */0/*# else *//*- 2 *//*# end */;

	/**
	 * Constructs a new text input.
	 *
	 * @param value The initial value of this text field
	 */
	public TextConfigInput(String value) {
		super(
				Minecraft.getInstance().font,
				0,
				0,
				10,
				20 - AUTO_PADDING * 2,
				/*# if MC_VERSION_NUMBER >= 11900 */Component.empty()/*# else *//*- null *//*# end */
		);
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
	public void setValue(String value) {
		super.setValue(value);
		// Required because otherwise the text doesn't render sometimes
		//# if MC_VERSION_NUMBER >= 12002
		moveCursorToStart(false);
		//# else
		//- moveCursorToStart();
		//# end
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChangeListener(InputChangeListener<String> changeListener) {
		super.setResponder(changeListener);
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
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		setPosition(x + AUTO_PADDING, y);
		this.width = width - AUTO_PADDING * 2;
		render(graphics, mouseX, mouseY, tickDelta);
	}

	//# if MC_VERSION_NUMBER < 11903
	//- private void setPosition(int x, int y) {
	//- 	this.x = x;
	//- 	this.y = y;
	//- }
	//# end
}
