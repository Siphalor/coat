package de.siphalor.coat.util.renderstate;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public interface CoatGuiGraphics {
	void coat_submitGuiRenderState(GuiElementRenderState renderState);
	ScreenRectangle coat_currentScissorArea();
}
