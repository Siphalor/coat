package de.siphalor.coat.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//- import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
//- import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Utility class for Coat that collects everything that doesn't have a better place.
 * Mildly intended for internal usage.
 */
public class CoatUtil {
	/**
	 * The primary text color to use in Coat.
	 */
	public static final CoatColor TEXT_COLOR = CoatColor.rgb(0xdddddd);
	/**
	 * The secondary text color to use in Coat.
	 */
	public static final CoatColor SECONDARY_TEXT_COLOR = CoatColor.rgb(0xaaaaaa);
	/**
	 * A semi-transparent light color to use as background for hovered elements.
	 */
	public static final CoatColor HOVER_BG_COLOR = CoatColor.argb(0x2dffffff);
	/**
	 * A predefined margin that'll always™ be <code>2</code>.
	 * I really don't like having constant numeric values in my code.
	 */
	public static final int MARGIN = 2;
	/**
	 * Double the {@link CoatUtil#MARGIN}.
	 */
	public static final int DOUBLE_MARGIN = MARGIN * 2;
	/**
	 * Half the {@link CoatUtil#MARGIN}.
	 */
	public static final int HALF_MARGIN = MARGIN / 2;
	/**
	 * An ellipsis - what did you expect?
	 */
	public static final String ELLIPSIS = "...";

	/**
	 * Intelligently trims the given text to the given width.
	 * At the end of the string an ellipsis will be placed.
	 *
	 * @param font     The text renderer to use for calculations
	 * @param baseText The text to trim intelligently
	 * @param width    The width to trim the text to
	 * @return The trimmed text
	 */
	public static Component intelliTrim(Font font, Component baseText, int width) {
		int textWidth = font.width(baseText);
		if (textWidth > width) {
			textWidth = font.width(ELLIPSIS);
			String trimmed = font.plainSubstrByWidth(baseText.getString(), width - textWidth);
			//# if MC_VERSION_NUMBER >= 11900
			return Component.literal(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
			//# else
			//- return new TextComponent(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
			//# end
		} else {
			return baseText;
		}
	}

	/**
	 * Wraps a text that's intended for tooltips at a certain length.
	 * @param font    The text renderer to use for calculations
	 * @param minecraft The {@link Minecraft} instance
	 * @param text            The text to wrap
	 * @return A list of {@link FormattedCharSequence}s representing the wrapped tooltip text
	 */
	public static List<FormattedCharSequence> wrapTooltip(Font font, Minecraft minecraft, Component text) {
		return font.split(text, minecraft.screen.width / 2);
	}

	/**
	 * Wraps and renders the given text as a tooltip.
	 *
	 * @param graphics The matrix stack to use for rendering
	 * @param x        The x position to render to
	 * @param y        The y position to render to
	 * @param text     The tooltip text to wrap and render
	 */
	//# if RENDERING == "GUI_GRAPHICS"
	public static void renderTooltip(GuiGraphics graphics, int x, int y, Component text) {
	//# elif RENDERING == "POSE_STACK"
	//- public static void renderTooltip(PoseStack graphics, int x, int y, Component text) {
	//# end
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		//# if RENDERING == "GUI_GRAPHICS"
		graphics.renderTooltip(
				minecraft.font,
				wrapTooltip(minecraft.font, minecraft, text),
				x, y
		);
		//# elif RENDERING == "POSE_STACK"
		//- Minecraft.getInstance().screen.renderTooltip(
		//- 		graphics,
		//- 		text,
		//- 		x, y
		//- );
		//# end
	}

	/**
	 * Draws the outline of a rectangle.
	 * @param x1     x1
	 * @param y1     y1
	 * @param x2     x2
	 * @param y2     y2, duh
	 * @param stroke The width of the outline
	 * @param color  The color to draw with
	 */
	public static void drawStrokeRect(int x1, int y1, int x2, int y2, int stroke, CoatColor color) {
		Tesselator tesselator = Tesselator.getInstance();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		//# if MC_VERSION_NUMBER >= 12100
		RenderSystem.setShader(CoreShaders.POSITION_COLOR);
		BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//# else
		//- RenderSystem.setShader(GameRenderer::getPositionColorShader);

		//- BufferBuilder buffer = tesselator.getBuilder();
		//- buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//# end
		addRect(buffer, x1, y1, x2, y1 + stroke, color);
		addRect(buffer, x1, y2 - stroke, x2, y2, color);
		addRect(buffer, x1, y1 + stroke, x1 + stroke, y2 - stroke, color);
		addRect(buffer, x2 - stroke, y1 + stroke, x2, y2 - stroke, color);
		//# if MC_VERSION_NUMBER >= 12100
		BufferUploader.drawWithShader(buffer.buildOrThrow());
		//# else
		//- tesselator.end();
		//# end
	}

	/**
	 * Adds a rectangle to the given buffer builder.
	 * @param buffer The builder to append to
	 * @param x1     x1
	 * @param y1     y1
	 * @param x2     x2
	 * @param y2     y2, duh
	 * @param color  the color of the rect
	 */
	public static void addRect(BufferBuilder buffer, int x1, int y1, int x2, int y2, CoatColor color) {
		//# if MC_VERSION_NUMBER >= 12100
		withColor(buffer.addVertex(x1, y2, 0), color);
		withColor(buffer.addVertex(x2, y2, 0), color);
		withColor(buffer.addVertex(x2, y1, 0), color);
		withColor(buffer.addVertex(x1, y1, 0), color);
		//# else
		//- withColor(buffer.vertex(x1, y2, 0), color).endVertex();
		//- withColor(buffer.vertex(x2, y2, 0), color).endVertex();
		//- withColor(buffer.vertex(x2, y1, 0), color).endVertex();
		//- withColor(buffer.vertex(x1, y1, 0), color).endVertex();
		//# end
	}

	public static void drawHorizontalGradient(int left, int top, int right, int bottom, CoatColor leftColor, CoatColor rightColor) {
		RenderSystem.enableBlend();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		Tesselator tesselator = Tesselator.getInstance();

		//# if MC_VERSION_NUMBER >= 12100
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		withColor(bufferBuilder.addVertex(left, bottom, 0), leftColor);
		withColor(bufferBuilder.addVertex(right, bottom, 0), rightColor);
		withColor(bufferBuilder.addVertex(right, top, 0), rightColor);
		withColor(bufferBuilder.addVertex(left, top, 0), leftColor);
		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
		//# else
		//- BufferBuilder bufferBuilder = tesselator.getBuilder();
		//- bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//- withColor(bufferBuilder.vertex(left, bottom, 0D), leftColor).endVertex();
		//- withColor(bufferBuilder.vertex(right, bottom, 0D), rightColor).endVertex();
		//- withColor(bufferBuilder.vertex(right, top, 0D), rightColor).endVertex();
		//- withColor(bufferBuilder.vertex(left, top, 0D), leftColor).endVertex();
		//- tesselator.end();
		//# end

		RenderSystem.disableBlend();
	}

	public static void drawInsetGradientTexture(int left, int top, int right, int bottom, int z, ResourceLocation texture, float textureScale, CoatColor outerColor, CoatColor innerColor) {
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		//# if MC_VERSION_NUMBER >= 12100
		RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
		//# else
		//- RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		//# end
		resetShaderColor();
		RenderSystem.setShaderTexture(0, texture);
		Tesselator tesselator = Tesselator.getInstance();

		int width = right - left;
		int height = bottom - top;
		int middleOffset = height / 2;


		//# if MC_VERSION_NUMBER >= 12100
		BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

		withColor(buffer.addVertex(left, top, z), outerColor).setUv(0F, 0F);
		withColor(buffer.addVertex(left + middleOffset, top + middleOffset, z), innerColor).setUv(middleOffset / textureScale, middleOffset / textureScale);
		withColor(buffer.addVertex(right, top, z), outerColor).setUv(width / textureScale, 0F);
		withColor(buffer.addVertex(right - middleOffset, top + middleOffset, z), innerColor).setUv((width - middleOffset) / textureScale, middleOffset / textureScale);
		withColor(buffer.addVertex(right, bottom, z), outerColor).setUv(width / textureScale, height / textureScale);
		withColor(buffer.addVertex(left + middleOffset, bottom - middleOffset, z), innerColor).setUv(middleOffset / textureScale, (height - middleOffset) / textureScale);
		withColor(buffer.addVertex(left, bottom, z), outerColor).setUv(0F, height / textureScale);
		withColor(buffer.addVertex(left, top, z), outerColor).setUv(0F, 0F);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
		//# else
		//- BufferBuilder buffer = tesselator.getBuilder();
		//- buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_TEX);

		//- withColor(buffer.vertex(left, top, z), outerColor).uv(0F, 0F).endVertex();
		//- withColor(buffer.vertex(left + middleOffset, top + middleOffset, z), innerColor).uv(middleOffset / textureScale, middleOffset / textureScale).endVertex();
		//- withColor(buffer.vertex(right, top, z), outerColor).uv(width / textureScale, 0F).endVertex();
		//- withColor(buffer.vertex(right - middleOffset, top + middleOffset, z), innerColor).uv((width - middleOffset) / textureScale, middleOffset / textureScale).endVertex();
		//- withColor(buffer.vertex(right, bottom, z), outerColor).uv(width / textureScale, height / textureScale).endVertex();
		//- withColor(buffer.vertex(left + middleOffset, bottom - middleOffset, z), innerColor).uv(middleOffset / textureScale, (height - middleOffset) / textureScale).endVertex();
		//- withColor(buffer.vertex(left, bottom, z), outerColor).uv(0F, height / textureScale).endVertex();
		//- withColor(buffer.vertex(left, top, z), outerColor).uv(0F, 0F).endVertex();
		//- tesselator.end();
		//# end
	}

	//# if MC_VERSION_NUMBER >= 12100
	public static void drawTintedTexture(GuiGraphics graphics, int left, int top, int right, int bottom, ResourceLocation texture, int textureScale, int textureYOffset, CoatColor color) {
		int width = right - left;
		int height = bottom - top;
		graphics.blit(RenderType::guiTextured, texture, left, top, right, bottom + textureYOffset, width, height, width, height, textureScale, textureScale, color.getArgb());
	}
	//# else
	//- public static void drawTintedTexture(int left, int top, int right, int bottom, int z, ResourceLocation texture, float textureScale, int textureYOffset, CoatColor color) {
	//- 	RenderSystem.setShader(GameRenderer::getPositionTexShader);
	//- 	RenderSystem.setShaderTexture(0, texture);
	//- 	Tesselator tesselator = Tesselator.getInstance();
	//- 	setShaderColor(color);
	//- 	BufferBuilder bufferBuilder = tesselator.getBuilder();
	//- 	bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
	//- 	bufferBuilder.vertex(left, bottom, z).uv(left / textureScale, (bottom + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(right, bottom, z).uv(right / textureScale, (bottom + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(right, top, z).uv(right / textureScale, (top + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(left, top, z).uv(left / textureScale, (top + textureYOffset) / textureScale).endVertex();
	//- 	tesselator.end();
	//- 	resetShaderColor();
	//- }
	//# end

	private static <V extends VertexConsumer> V withColor(V vertexConsumer, CoatColor color) {
		//# if MC_VERSION_NUMBER >= 12100
		vertexConsumer.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		//# else
		//- vertexConsumer.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		//# end
		return vertexConsumer;
	}

	public static void setShaderColor(CoatColor color) {
		RenderSystem.setShaderColor(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
	}

	public static void resetShaderColor() {
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	public static void playClickSound() {
		SoundManager soundManager = Minecraft.getInstance().getSoundManager();
		soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
	}

	public static void setButtonPosition(Button button, int x, int y) {
		setButtonX(button, x);
		setButtonY(button, y);
	}

	public static void setButtonX(Button button, int x) {
		//# if MC_VERSION_NUMBER >= 11903
		button.setX(x);
		//# else
		//- button.x = x;
		//# end
	}

	public static void setButtonY(Button button, int y) {
		//# if MC_VERSION_NUMBER >= 11903
		button.setY(y);
		//# else
		//- button.y = y;
		//# end
	}
}
