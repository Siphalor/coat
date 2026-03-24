package de.siphalor.coat.mixin.renderstate;

import de.siphalor.coat.util.renderstate.CoatGuiGraphics;
//- import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//# if MC_VERSION_NUMBER >= 260100
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
//# else
//- import net.minecraft.client.gui.render.state.GuiElementRenderState;
//- import net.minecraft.client.gui.render.state.GuiRenderState;
//# end
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
@Mixin(GuiGraphicsExtractor.class)
//# else
//- @Mixin(GuiGraphics.class)
//# end
public abstract class GuiGraphicsMixin implements CoatGuiGraphics {
	@Shadow
	@Final
	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
	private GuiGraphicsExtractor.ScissorStack scissorStack;
	//# else
	//- private GuiGraphics.ScissorStack scissorStack;
	//# end

	@Shadow
	public abstract Matrix3x2fStack pose();

	@Shadow
	@Final
	private GuiRenderState guiRenderState;

	@Override
	public void coat_submitGuiRenderState(GuiElementRenderState renderState) {
		//# if MC_VERSION_NUMBER >= 260100
		guiRenderState.addGuiElement(renderState);
		//# else
		//- guiRenderState.submitGuiElement(renderState);
		//# end
	}

	@Override
	public ScreenRectangle coat_currentScissorArea() {
		return scissorStack.peek();
	}
}
