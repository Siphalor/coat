package de.siphalor.coat.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
	public static final int TEXT_COLOR = 0xffdddddd;
	/**
	 * The secondary text color to use in Coat.
	 */
	public static final int SECONDARY_TEXT_COLOR = 0xffaaaaaa;
	/**
	 * A semi-transparent light color to use as background for hovered elements.
	 */
	public static final int HOVER_BG_COLOR = 0x2dffffff;
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
	 * @param textRenderer The text renderer to use for calculations
	 * @param baseText     The text to trim intelligently
	 * @param width        The width to trim the text to
	 * @return The trimmed text
	 */
	public static Text intelliTrim(TextRenderer textRenderer, Text baseText, int width) {
		int textWidth = textRenderer.getWidth(baseText);
		if (textWidth > width) {
			textWidth = textRenderer.getWidth(ELLIPSIS);
			String trimmed = textRenderer.trimToWidth(baseText.getString(), width - textWidth);
			return new LiteralText(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
		} else {
			return baseText;
		}
	}

	/**
	 * Wraps a text that's intended for tooltips at a certain length.
	 * @param textRenderer    The text renderer to use for calculations
	 * @param minecraftClient The {@link MinecraftClient} instance
	 * @param text            The text to wrap
	 * @return A list of {@link OrderedText}s representing the wrapped tooltip text
	 */
	public static List<OrderedText> wrapTooltip(TextRenderer textRenderer, MinecraftClient minecraftClient, Text text) {
		return textRenderer.wrapLines(text, minecraftClient.currentScreen.width / 2);
	}

	/**
	 * Wraps and renders the given text as a tooltip.
	 * @param matrices The matrix stack to use for rendering
	 * @param x        The x position to render to
	 * @param y        The y position to render to
	 * @param text     The tooltip text to wrap and render
	 */
	public static void renderTooltip(MatrixStack matrices, int x, int y, Text text) {
		MinecraftClient client = MinecraftClient.getInstance();
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		client.currentScreen.renderOrderedTooltip(
				matrices,
				wrapTooltip(client.textRenderer, client, text),
				x, y
		);
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
	public static void drawStrokeRect(int x1, int y1, int x2, int y2, int stroke, int color) {
		int alpha = color >> 24 & 255;
		int red = color >> 16 & 255;
		int green = color >> 8 & 255;
		int blue = color & 255;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		addRect(buffer, x1, y1, x2, y1 + stroke, red, green, blue, alpha);
		addRect(buffer, x1, y2 - stroke, x2, y2, red, green, blue, alpha);
		addRect(buffer, x1, y1 + stroke, x1 + stroke, y2 - stroke, red, green, blue, alpha);
		addRect(buffer, x2 - stroke, y1 + stroke, x2, y2 - stroke, red, green, blue, alpha);
		buffer.end();
		BufferRenderer.draw(buffer);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * Adds a rectangle to the given buffer builder.
	 * @param buffer The builder to append to
	 * @param x1     x1
	 * @param y1     y1
	 * @param x2     x2
	 * @param y2     y2, duh
	 * @param red    The red part of the rect color
	 * @param green  The green part of the rect color
	 * @param blue   The blue part of the rect color
	 * @param alpha  The opacity of the rectangle
	 */
	public static void addRect(BufferBuilder buffer, int x1, int y1, int x2, int y2, int red, int green, int blue, int alpha) {
		buffer.vertex(x1, y2, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x2, y2, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x2, y1, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x1, y1, 0).color(red, green, blue, alpha).next();
	}

	public static void drawHorizontalGradient(int left, int top, int right, int bottom, int leftColor, int rightColor) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		int leftRed = leftColor >> 24 & 255;
		int leftGreen = leftColor >> 16 & 255;
		int leftBlue = leftColor >> 8 & 255;
		int leftAlpha = leftColor & 255;
		int rightRed = rightColor >> 24 & 255;
		int rightGreen = rightColor >> 16 & 255;
		int rightBlue = rightColor >> 8 & 255;
		int rightAlpha = rightColor & 255;
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(left, bottom, 0D).color(leftRed, leftGreen, leftBlue, leftAlpha).next();
		bufferBuilder.vertex(right, bottom, 0D).color(rightRed, rightGreen, rightBlue, rightAlpha).next();
		bufferBuilder.vertex(right, top, 0D).color(rightRed, rightGreen, rightBlue, rightAlpha).next();
		bufferBuilder.vertex(left, top, 0D).color(leftRed, leftGreen, leftBlue, leftAlpha).next();
		tessellator.draw();

		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	public static void drawVerticalGradientTexture(int left, int top, int right, int bottom, Identifier texture, float textureScale, int topColor, int bottomColor) {
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		int topRed = topColor >> 24 & 255;
		int topGreen = topColor >> 16 & 255;
		int topBlue = topColor >> 8 & 255;
		int topAlpha = topColor & 255;
		int bottomRed = bottomColor >> 24 & 255;
		int bottomGreen = bottomColor >> 16 & 255;
		int bottomBlue = bottomColor >> 8 & 255;
		int bottomAlpha = bottomColor & 255;

		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(left, bottom, 0D).color(topRed, topGreen, topBlue, topAlpha).texture(left / textureScale, top / textureScale).next();
		bufferBuilder.vertex(right, bottom, 0D).color(topRed, topGreen, topBlue, topAlpha).texture(right / textureScale, top / textureScale).next();
		bufferBuilder.vertex(right, top, 0D).color(bottomRed, bottomGreen, bottomBlue, bottomAlpha).texture(right / textureScale, bottom / textureScale).next();
		bufferBuilder.vertex(left, top, 0D).color(bottomRed, bottomGreen, bottomBlue, bottomAlpha).texture(left / textureScale, bottom / textureScale).next();
		tessellator.draw();
	}

	public static void drawInsetGradientTexture(int left, int top, int right, int bottom, int z, Identifier texture, float textureScale, int outerColor, int innerColor) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		int width = right - left;
		int height = bottom - top;
		int middleOffset = height / 2;

		int outerRed = outerColor >> 24 & 255;
		int outerGreen = outerColor >> 16 & 255;
		int outerBlue = outerColor >> 8 & 255;
		int outerAlpha = outerColor & 255;
		int innerRed = innerColor >> 24 & 255;
		int innerGreen = innerColor >> 16 & 255;
		int innerBlue = innerColor >> 8 & 255;
		int innerAlpha = innerColor & 255;

		buffer.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR_TEXTURE);
		buffer.vertex(left, top, z).color(outerRed, outerGreen, outerBlue, outerAlpha).texture(0F, 0F).next();
		buffer.vertex(left + middleOffset, top + middleOffset, z).color(innerRed, innerGreen, innerBlue, innerAlpha).texture(middleOffset / textureScale, middleOffset / textureScale).next();
		buffer.vertex(right, top, z).color(outerRed, outerGreen, outerBlue, outerAlpha).texture(width / textureScale, 0F).next();
		buffer.vertex(right - middleOffset, top + middleOffset, z).color(innerRed, innerGreen, innerBlue, innerAlpha).texture((width - middleOffset) / textureScale, middleOffset / textureScale).next();
		buffer.vertex(right, bottom, z).color(outerRed, outerGreen, outerBlue, outerAlpha).texture(width / textureScale, height / textureScale).next();
		buffer.vertex(left + middleOffset, bottom - middleOffset, z).color(innerRed, innerGreen, innerBlue, innerAlpha).texture(middleOffset / textureScale, (height - middleOffset) / textureScale).next();
		buffer.vertex(left, bottom, z).color(outerRed, outerGreen, outerBlue, outerAlpha).texture(0F, height / textureScale).next();
		buffer.vertex(left, top, z).color(outerRed, outerGreen, outerBlue, outerAlpha).texture(0F, 0F).next();
		tessellator.draw();
	}

	public static void drawTintedTexture(int left, int top, int right, int bottom, int z, Identifier texture, float textureScale, int textureYOffset, int color) {
		RenderSystem.color4f(
				(color >> 24 & 255) / 255F,
				(color >> 16 & 255) / 255F,
				(color >> 8 & 255) / 255F,
				(color & 255) / 255F
		);
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(left, bottom, z).texture(left / textureScale, (bottom + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(right, bottom, z).texture(right / textureScale, (bottom + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(right, top, z).texture(right / textureScale, (top + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(left, top, z).texture(left / textureScale, (top + textureYOffset) / textureScale).next();
		tessellator.draw();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
	}

	public static void playClickSound() {
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
	}
}
