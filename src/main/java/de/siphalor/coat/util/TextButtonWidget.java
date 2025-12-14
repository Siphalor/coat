package de.siphalor.coat.util;

//- import com.mojang.blaze3d.vertex.PoseStack;
//- import lombok.Getter;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

/**
 * A button widget that only renders as text.
 */
public class TextButtonWidget extends Button {
	/**
	 *  The original, untrimmed button text
	 */
	//# if MC_VERSION_NUMBER < 12108
	//- @Getter
	//- private Component originalMessage;
	//# end
	@Setter
	private boolean hoverEffect = true;

	//#if MC_VERSION_NUMBER >= 12110
	@Setter
	private CursorType actionCursorType = CursorTypes.POINTING_HAND;
	//# end

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
	public TextButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(
				x,
				y,
				width,
				height,
				message,
				onPress
				/*# if MC_VERSION_NUMBER >= 11903 */, DEFAULT_NARRATION/*# end */
		);
		setMessage(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if MC_VERSION_NUMBER >= 12111
	public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	//- public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "POSE_STACK" && MC_VERSION_NUMBER >= 11904
	//- public void renderWidget(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void renderButton(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# end
		//# if MC_VERSION_NUMBER >= 11903
		int x = getX();
		int y = getY();
		//# end
		final CoatColor color = CoatUtil.TEXT_COLOR.withAlpha((int) (alpha * 255F));
		int textY = y + (height - 7) / 2;
		Font font = Minecraft.getInstance().font;
		//# if RENDERING == "GUI_GRAPHICS"
		//# if MC_VERSION_NUMBER >= 12108
		CoatUtil.drawLeftAlignedText(
				graphics,
				font,
				getMessage(),
				new ScreenRectangle(x, textY, width, 11),
				color
		);
		//# else
		//- graphics.drawString(font, getMessage(), x, textY, color.getArgb());
		//# end
		if (isFocused()) {
			CoatUtil.drawOutline(graphics, x - 2, y - 2, x + width + 2, y + height + 2, color);
		}
		//# elif RENDERING == "POSE_STACK"
		//- font.draw(graphics, getMessage(), x, textY, color.getArgb());
		//- if (isFocused()) {
		//- 	CoatUtil.drawOutline(x - 2, y - 2, x + width + 2, y + height + 2, color);
		//- }
		//# end
		if (isHovered) {
			if (hoverEffect) {
				//# if RENDERING == "GUI_GRAPHICS"
				graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, CoatUtil.HOVER_BG_COLOR.getArgb());
				//# elif RENDERING == "POSE_STACK"
				//- fill(graphics, x - 1, y - 1, x + width + 1, y + height + 1, CoatUtil.HOVER_BG_COLOR.getArgb());
				//# end
			}
			//# if MC_VERSION_NUMBER == 12110
			//- if (active) {
			//- 	graphics.requestCursor(actionCursorType);
			//- }
			//# end
			//# if MC_VERSION_NUMBER < 12108
			//- if (originalMessage != getMessage()) {
				//- CoatUtil.renderTooltip(graphics, mouseX, mouseY, originalMessage);
			//- }
			//# end
		}
	}

	//# if MC_VERSION_NUMBER >= 12111
	@Override
	protected void handleCursor(GuiGraphics graphics) {
		if (isHovered && active) {
			graphics.requestCursor(actionCursorType);
		}
	}

	@Override
	public Component getMessage() {
		return message;
	}
	//# end

	//# if MC_VERSION_NUMBER >= 12108
	public Component getOriginalMessage() {
		return getMessage();
	}
	//# else
	//- /**
	//-  * Sets a new text for this button and trims it appropriately. #intellitrim
	//-  *
	//-  * @param text The new button text
	//-  */
	//- @Override
	//- public void setMessage(Component text) {
	//- 	originalMessage = text;
	//- 	super.setMessage(CoatUtil.intelliTrim(Minecraft.getInstance().font, originalMessage, width));
	//- }

	//- /**
	//-  * Updates the button's width and the trim work on the text.
	//-  *
	//-  * @param value The new width
	//-  */
	//- @Override
	//- public void setWidth(int value) {
	//- 	super.setWidth(value);
	//- 	super.setMessage(CoatUtil.intelliTrim(Minecraft.getInstance().font, originalMessage, width));
	//- }
	//# end
}
