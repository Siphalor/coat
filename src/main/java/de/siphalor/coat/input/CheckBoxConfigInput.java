package de.siphalor.coat.input;

//- import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
//- import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A boolean {@link ConfigInput} which displays as a checkbox.
 */
public class CheckBoxConfigInput extends Checkbox implements ConfigInput<Boolean> {
	private InputChangeListener<Boolean> changeListener;

	public CheckBoxConfigInput(boolean checked) {
		this(null, checked, false);
	}

	/**
	 * Constructs a new checkbox input.
	 * @param message     A message to be shown behind the checkbox
	 * @param checked     Whether this checkbox should initially be checked
	 */
	public CheckBoxConfigInput(boolean checked, @NotNull Component message) {
		this(message, checked, true);
	}

	/**
	 * Constructs a new checkbox input.
	 * @param message     A message to be shown behind the checkbox
	 * @param checked     Whether this checkbox should initially be checked
	 * @param showMessage Whether the message should be displayed
	 * @deprecated use {@link #CheckBoxConfigInput(boolean)} or {@link #CheckBoxConfigInput(boolean, Component)}
	 */
	@Deprecated
	public CheckBoxConfigInput(Component message, boolean checked, boolean showMessage) {
		super(
				0,
				0,
				//# if CHECKBOX_SIZE == "EXPLICIT"
				//- 20,
				//- 20,
				//# end
				//# if MC_VERSION_NUMBER >= 12100
				20,
				//# end
				//# if MC_VERSION_NUMBER >= 11900
				showMessage ? message : Component.empty()
				//# else
				//- message
				//# end
				/*# if CHECKBOX_SIZE == "FONT" */, Minecraft.getInstance().font/*# end */
				, checked
				/*# if CHECKBOX_CHANGE_HANDLER */, (checkbox, checked1) -> {}/*# end */
				/*# if MC_VERSION_NUMBER < 11900 *//*- , showMessage *//*# end */
		);
	}

	@Override
	public int getPreferredWidth() {
		return getWidth();
	}

	@Override
	public Boolean getValue() {
		return selected();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	@Override
	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
	public void render(GuiGraphicsExtractor graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	//- public void render(GuiGraphics graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		setPosition(x + width - getWidth(), y);

		//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
		extractContents(graphics, mouseX, mouseY, tickDelta);
		//# else
		//- render(graphics, mouseX, mouseY, tickDelta);
		//# end

		//# if MC_VERSION_NUMBER >= 12110
		if (visible && isHovered) {
			if (active) {
				graphics.requestCursor(CursorTypes.POINTING_HAND);
			} else {
				graphics.requestCursor(CursorTypes.NOT_ALLOWED);
			}
		}
		//# end
	}

	//# if MC_VERSION_NUMBER < 11903
	//- private void setPosition(int x, int y) {
	//- 	this.x = x;
	//- 	this.y = y;
	//- }
	//# end

	@Override
	public void setValue(Boolean value) {
		if (selected() != value) {
			onPress(/*# if MC_VERSION_NUMBER >= 12110 */null/*# end */);
		}
	}

	//# if MC_VERSION_NUMBER >= 12110
	@Override
	public void onPress(InputWithModifiers inputWithModifiers) {
		super.onPress(inputWithModifiers);
		changeListener.inputChanged(selected());
	}
	//# else
	//- @Override
	//- public void onPress() {
	//- 	super.onPress();
	//- 	changeListener.inputChanged(selected());
	//- }
	//# end

	@Override
	public void setChangeListener(InputChangeListener<Boolean> changeListener) {
		this.changeListener = changeListener;
	}
}
