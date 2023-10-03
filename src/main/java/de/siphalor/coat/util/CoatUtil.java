package de.siphalor.coat.util;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
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
	 * @param textRenderer The text renderer to use for calculations
	 * @param baseText     The text to trim intelligently
	 * @param width        The width to trim the text to
	 * @return The trimmed text
	 */
	public static String intelliTrim(TextRenderer textRenderer, String baseText, int width) {
		int textWidth = textRenderer.getStringWidth(baseText);
		if (textWidth > width) {
			textWidth = textRenderer.getStringWidth(ELLIPSIS);
			String trimmed = textRenderer.trimToWidth(baseText, width - textWidth);
			return trimmed.trim() + ELLIPSIS;
		} else {
			return baseText;
		}
	}

	/**
	 * Wraps a text that's intended for tooltips at a certain length.
	 * @param textRenderer    The text renderer to use for calculations
	 * @param minecraftClient The {@link MinecraftClient} instance
	 * @param text            The text to wrap
	 * @return A list of Strings representing the wrapped tooltip text
	 */
	public static List<String> wrapTooltip(TextRenderer textRenderer, MinecraftClient minecraftClient, String text) {
		return textRenderer.wrapStringToWidthAsList(text, minecraftClient.currentScreen.width / 2);
	}

	/**
	 * Wraps and renders the given text as a tooltip.
	 * @param x        The x position to render to
	 * @param y        The y position to render to
	 * @param text     The tooltip text to wrap and render
	 */
	public static void renderTooltip(int x, int y, String text) {
		MinecraftClient client = MinecraftClient.getInstance();
		GlStateManager.disableDepthTest();
		client.currentScreen.renderTooltip(
				wrapTooltip(client.textRenderer, client, text),
				x, y
		);
		GlStateManager.enableDepthTest();
		GlStateManager.disableLighting();
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
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		addRect(buffer, x1, y1, x2, y1 + stroke, color);
		addRect(buffer, x1, y2 - stroke, x2, y2, color);
		addRect(buffer, x1, y1 + stroke, x1 + stroke, y2 - stroke, color);
		addRect(buffer, x2 - stroke, y1 + stroke, x2, y2 - stroke, color);
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
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
		withColor(buffer.vertex(x1, y2, 0), color).next();
		withColor(buffer.vertex(x2, y2, 0), color).next();
		withColor(buffer.vertex(x2, y1, 0), color).next();
		withColor(buffer.vertex(x1, y1, 0), color).next();
	}

	public static void drawHorizontalGradient(int left, int top, int right, int bottom, CoatColor leftColor, CoatColor rightColor) {
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(7425);
		GlStateManager.disableTexture();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		withColor(bufferBuilder.vertex(left, bottom, 0D), leftColor).next();
		withColor(bufferBuilder.vertex(right, bottom, 0D), rightColor).next();
		withColor(bufferBuilder.vertex(right, top, 0D), rightColor).next();
		withColor(bufferBuilder.vertex(left, top, 0D), leftColor).next();
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
	}

	public static void drawInsetGradientTexture(int left, int top, int right, int bottom, int z, Identifier texture, float textureScale, CoatColor outerColor, CoatColor innerColor) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);

		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		int width = right - left;
		int height = bottom - top;
		int middleOffset = height / 2;

		buffer.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);
		withColor(buffer.vertex(left, top, z).texture(0F, 0F), outerColor).next();
		withColor(buffer.vertex(left + middleOffset, top + middleOffset, z).texture(middleOffset / textureScale, middleOffset / textureScale), innerColor).next();
		withColor(buffer.vertex(right, top, z).texture(width / textureScale, 0F), outerColor).next();
		withColor(buffer.vertex(right - middleOffset, top + middleOffset, z).texture((width - middleOffset) / textureScale, middleOffset / textureScale), innerColor).next();
		withColor(buffer.vertex(right, bottom, z).texture(width / textureScale, height / textureScale), outerColor).next();
		withColor(buffer.vertex(left + middleOffset, bottom - middleOffset, z).texture(middleOffset / textureScale, (height - middleOffset) / textureScale), innerColor).next();
		withColor(buffer.vertex(left, bottom, z).texture(0F, height / textureScale), outerColor).next();
		withColor(buffer.vertex(left, top, z).texture(0F, 0F), outerColor).next();
		tessellator.draw();
	}

	public static void drawTintedTexture(int left, int top, int right, int bottom, int z, Identifier texture, float textureScale, int textureYOffset, CoatColor color) {
		setShaderColor(color);
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(left, bottom, z).texture(left / textureScale, (bottom + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(right, bottom, z).texture(right / textureScale, (bottom + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(right, top, z).texture(right / textureScale, (top + textureYOffset) / textureScale).next();
		bufferBuilder.vertex(left, top, z).texture(left / textureScale, (top + textureYOffset) / textureScale).next();
		tessellator.draw();
		resetShaderColor();
	}

	private static <V extends BufferBuilder> V withColor(V vertexConsumer, CoatColor color) {
		vertexConsumer.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		return vertexConsumer;
	}

	public static void setShaderColor(CoatColor color) {
		GlStateManager.color4f(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
	}

	public static void resetShaderColor() {
		GlStateManager.color4f(1F, 1F, 1F, 1F);
	}

	public static void playClickSound() {
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
	}
}
