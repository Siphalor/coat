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
	/**
	 * Constructs a new text input.
	 *
	 * @param value The initial value of this text field
	 */
	public TextConfigInput(String value) {
		super(Minecraft.getInstance().font, 0, 0, 10, 20, Component.empty());
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
		//# if EDITBOX_CURSOR_TO_START_PARAM
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
	//# if RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		setPosition(x, y);
		this.width = width;
		render(graphics, mouseX, mouseY, tickDelta);
	}
}
