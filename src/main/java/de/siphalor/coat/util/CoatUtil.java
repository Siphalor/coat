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
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class CoatUtil {
	public static final int TEXT_COLOR = 0xffdddddd;
	public static final int SECONDARY_TEXT_COLOR = 0xffaaaaaa;
	public static final int MARGIN = 2;
	public static final int DOUBLE_MARGIN = MARGIN * 2;
	public static final int HALF_MARGIN = MARGIN / 2;
	public static final String ELLIPSIS = "...";

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

	public static List<OrderedText> wrapTooltip(TextRenderer textRenderer, MinecraftClient minecraftClient, Text text) {
		return textRenderer.wrapLines(text, minecraftClient.currentScreen.width / 2);
	}

	public static void renderTooltip(MatrixStack matrices, int x, int y, Text text) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.currentScreen.renderOrderedTooltip(
				matrices,
				wrapTooltip(client.textRenderer, client, text),
				x, y
		);
	}

	public static void drawStrokeRect(int x1, int y1, int x2, int y2, int stroke, int color) {
		int alpha = color >> 24 & 255;
		int red   = color >> 16 & 255;
		int green = color >> 8 & 255;
		int blue  = color & 255;
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

	private static void addRect(BufferBuilder buffer, int x1, int y1, int x2, int y2, int red, int green, int blue, int alpha) {
		buffer.vertex(x1, y2, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x2, y2, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x2, y1, 0).color(red, green, blue, alpha).next();
		buffer.vertex(x1, y1, 0).color(red, green, blue, alpha).next();
	}
}
