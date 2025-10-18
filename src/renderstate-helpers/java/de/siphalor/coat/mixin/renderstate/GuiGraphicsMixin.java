package de.siphalor.coat.mixin.renderstate;

import de.siphalor.coat.util.renderstate.CoatGuiGraphics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin implements CoatGuiGraphics {
	@Shadow
	@Final
	private GuiGraphics.ScissorStack scissorStack;

	@Shadow
	public abstract Matrix3x2fStack pose();

	@Shadow
	@Final
	private GuiRenderState guiRenderState;

	@Override
	public void coat_submitGuiRenderState(GuiElementRenderState renderState) {
		guiRenderState.submitGuiElement(renderState);
	}

	@Override
	public ScreenRectangle coat_currentScissorArea() {
		return scissorStack.peek();
	}
}
