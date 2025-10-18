package de.siphalor.coat.util.renderstate;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector4f;

public record IndividuallyColoredBlitRenderState(
		RenderPipeline pipeline,
		TextureSetup textureSetup,
		Matrix3x2f pose,
		ScreenRectangle rect,
		Vector4f uv,
		int topLeftColor,
		int topRightColor,
		int bottomRightColor,
		int bottomLeftColor,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

	public IndividuallyColoredBlitRenderState(
			RenderPipeline pipeline,
			TextureSetup textureSetup,
			Matrix3x2f pose,
			ScreenRectangle rect,
			Vector4f uv,
			int topLeftColor,
			int topRightColor,
			int bottomRightColor,
			int bottomLeftColor,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
				pipeline,
				textureSetup,
				pose,
				rect,
				uv,
				topLeftColor,
				topRightColor,
				bottomRightColor,
				bottomLeftColor,
				scissorArea,
				RenderStateHelper.computeRectBounds(rect, pose, scissorArea)
		);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float z) {
		vertexConsumer.addVertexWith2DPose(pose, rect.left(), rect.top(), z).setUv(uv.x, uv.y).setColor(topLeftColor);
		vertexConsumer.addVertexWith2DPose(pose, rect.left(), rect.bottom(), z).setUv(uv.x, uv.w).setColor(bottomLeftColor);
		vertexConsumer.addVertexWith2DPose(pose, rect.right(), rect.bottom(), z).setUv(uv.z, uv.w).setColor(bottomRightColor);
		vertexConsumer.addVertexWith2DPose(pose, rect.right(), rect.top(), z).setUv(uv.z, uv.y).setColor(topRightColor);
	}
}
