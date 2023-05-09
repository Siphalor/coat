package de.siphalor.coat.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
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
			return Text.literal(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
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
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		addRect(buffer, x1, y1, x2, y1 + stroke, red, green, blue, alpha);
		addRect(buffer, x1, y2 - stroke, x2, y2, red, green, blue, alpha);
		addRect(buffer, x1, y1 + stroke, x1 + stroke, y2 - stroke, red, green, blue, alpha);
		addRect(buffer, x2 - stroke, y1 + stroke, x2, y2 - stroke, red, green, blue, alpha);
		BufferRenderer.drawWithoutShader(buffer.end());
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

	public static void playClickSound() {
		SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
	}
}
