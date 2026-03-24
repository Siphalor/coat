package de.siphalor.coat.util.renderstate;

import net.minecraft.client.gui.navigation.ScreenRectangle;
//# if MC_VERSION_NUMBER >= 260100
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
//# else
//- import net.minecraft.client.gui.render.state.GuiElementRenderState;
//# end

public interface CoatGuiGraphics {
	void coat_submitGuiRenderState(GuiElementRenderState renderState);
	ScreenRectangle coat_currentScissorArea();
}
