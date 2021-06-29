package de.siphalor.coat.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
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
		RenderSystem.depthFunc(GL11.GL_ALWAYS);
		client.currentScreen.renderTooltip(
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
}
