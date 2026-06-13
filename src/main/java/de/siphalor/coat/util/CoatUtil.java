package de.siphalor.coat.util;

//- import com.mojang.blaze3d.systems.RenderSystem;
//- import com.mojang.blaze3d.vertex.BufferBuilder;
//- import com.mojang.blaze3d.vertex.BufferUploader;
//- import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//- import com.mojang.blaze3d.vertex.PoseStack;
//- import com.mojang.blaze3d.vertex.Tesselator;
//- import com.mojang.blaze3d.vertex.VertexConsumer;
//- import com.mojang.blaze3d.vertex.VertexFormat;
//- import de.siphalor.coat.mixin.renderstate.GuiGraphicsMixin;
import de.siphalor.coat.util.renderstate.CoatGuiGraphics;
import de.siphalor.coat.util.renderstate.IndividuallyColoredBlitRenderState;
import de.siphalor.coat.util.renderstate.IndividuallyColoredRectangleRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//- import net.minecraft.client.gui.GuiGraphics;
//- import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
//- import net.minecraft.client.renderer.GameRenderer;
//- import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
//- import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.Identifier;
//- import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix3x2f;
import org.joml.Vector4f;
//- import net.minecraft.util.FormattedCharSequence;
//- import org.lwjgl.opengl.GL11;

//- import java.util.List;

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

	public static <T extends Screen> T getOpenScreen() {
		//# if MC_VERSION_NUMBER >= 260200
		return (T) Minecraft.getInstance().gui.screen();
		//# else
		//- return (T) Minecraft.getInstance().screen;
		//# end
	}

	public static void openScreen(Screen screen) {
		//# if MC_VERSION_NUMBER >= 260200
		Minecraft.getInstance().gui.setScreen(screen);
		//# else
		//- Minecraft.getInstance().setScreen(screen);
		//# end
	}

	//# if MC_VERSION_NUMBER >= 12108
	/**
	 * Renders a string either scrolling or left-aligned depending on the width.
	 */
	public static void drawLeftAlignedText(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			Font font,
			Component text,
			ScreenRectangle rect,
			CoatColor color
	) {
		int textWidth = font.width(text);
		if (textWidth <= rect.width()) {
			//# if MC_VERSION_NUMBER >= 260100
			graphics.text(font, text, rect.left(), rect.top(), color.getArgb());
			//# else
			//- graphics.drawString(font, text, rect.left(), rect.top(), color.getArgb());
			//# end
		} else {
			//# if MC_VERSION_NUMBER >= 12111
			//# if MC_VERSION_NUMBER >= 260100
			graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.NONE)
			//# else
			//- graphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE)
			//# end
					.acceptScrolling(
							// This is pretty hacky, but probably the simplest way to keep the method signature relatively stable
									text.plainCopy().withColor(color.getArgb()),
									rect.left(),
									rect.left(),
									rect.right(),
									// Scrolling rendering for some reason renders two pixels lower
									rect.top() - 2,
									rect.bottom() - 2
							);
			//# else
			//- AbstractWidget.renderScrollingString(
			//- 		graphics,
			//- 		font,
			//- 		text,
			//- 		rect.left(),
			//- 		rect.top(),
			//- 		rect.right(),
			//- 		rect.bottom(),
			//- 		color.getArgb()
			//- );
			//# end
		}
	}
	//# else
	//- /**
	//-  * An ellipsis - what did you expect?
	//-  */
	//- public static final String ELLIPSIS = "...";

	//- /**
	//-  * Intelligently trims the given text to the given width.
	//-  * At the end of the string an ellipsis will be placed.
	//-  *
	//-  * @param font     The text renderer to use for calculations
	//-  * @param baseText The text to trim intelligently
	//-  * @param width    The width to trim the text to
	//-  * @return The trimmed text
	//-  */
	//- public static Component intelliTrim(Font font, Component baseText, int width) {
	//- 	int textWidth = font.width(baseText);
	//- 	if (textWidth > width) {
	//- 		textWidth = font.width(ELLIPSIS);
	//- 		String trimmed = font.plainSubstrByWidth(baseText.getString(), width - textWidth);
	//- 		//# if MC_VERSION_NUMBER >= 11900
	//- 		return Component.literal(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
	//- 		//# else
	//- 		return new TextComponent(trimmed.trim() + ELLIPSIS).setStyle(baseText.getStyle());
	//- 		//# end
	//- 	} else {
	//- 		return baseText;
	//- 	}
	//- }

	//- /**
	//-  * Wraps a text that's intended for tooltips at a certain length.
	//-  * @param font    The text renderer to use for calculations
	//-  * @param minecraft The {@link Minecraft} instance
	//-  * @param text            The text to wrap
	//-  * @return A list of {@link FormattedCharSequence}s representing the wrapped tooltip text
	//-  */
	//- public static List<FormattedCharSequence> wrapTooltip(Font font, Minecraft minecraft, Component text) {
	//- 	return font.split(text, minecraft.screen.width / 2);
	//- }

	//- /**
	//-  * Wraps and renders the given text as a tooltip.
	//-  *
	//-  * @param graphics The matrix stack to use for rendering
	//-  * @param x        The x position to render to
	//-  * @param y        The y position to render to
	//-  * @param text     The tooltip text to wrap and render
	//-  */
	//- //# if RENDERING == "GUI_GRAPHICS"
	//- public static void renderTooltip(GuiGraphics graphics, int x, int y, Component text) {
	//- //# elif RENDERING == "POSE_STACK"
	//- public static void renderTooltip(PoseStack graphics, int x, int y, Component text) {
	//- //# end
	//- 	Minecraft minecraft = Minecraft.getInstance();
	//- 	RenderSystem.depthFunc(GL11.GL_ALWAYS);
	//- 	//# if RENDERING == "GUI_GRAPHICS"
	//- 	graphics.renderTooltip(
	//- 			minecraft.font,
	//- 			wrapTooltip(minecraft.font, minecraft, text),
	//- 			x, y
	//- 	);
	//- 	//# elif RENDERING == "POSE_STACK"
	//- 	Minecraft.getInstance().screen.renderTooltip(
	//- 			graphics,
	//- 			text,
	//- 			x, y
	//- 	);
	//- 	//# end
	//- }
	//# end

	/**
	 * Draws the outline of a rectangle.
	 * @param color  The color to draw with
	 */
	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
	public static void drawOutline(GuiGraphicsExtractor graphics, int x1, int y1, int x2, int y2, CoatColor color) {
		graphics.outline(x1, y1, x2 - x1, y2 - y1, color.getArgb());
	}
	//# elif RENDERING == "GUI_GRAPHICS"
	//- public static void drawOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, CoatColor color) {
	//- 	//# if MC_VERSION_NUMBER >= 12111
	//- 	graphics.renderOutline(x1, y1, x2 - x1, y2 - y1, color.getArgb());
	//- 	//# elif MC_VERSION_NUMBER >= 12110
	//- 	graphics.submitOutline(x1, y1, x2 - x1, y2 - y1, color.getArgb());
	//- 	//# else
	//- 	graphics.renderOutline(x1, y1, x2 - x1, y2 - y1, color.getArgb());
	//- 	//# end
	//- }
	//# else
	//- public static void drawOutline(int x1, int y1, int x2, int y2, CoatColor color) {
	//- 	final int stroke = 1;
	//- 	Tesselator tesselator = Tesselator.getInstance();
	//- 	RenderSystem.enableBlend();
	//- 	RenderSystem.defaultBlendFunc();
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShader(GameRenderer::getPositionColorShader);
	//- 	BufferBuilder buffer = tesselator.getBuilder();
	//- 	buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
	//- 	//# else
	//- 	RenderSystem.enableTexture();
	//- 	BufferBuilder buffer = tesselator.getBuilder();
	//- 	buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
	//- 	//# end
	//- 	addRect(buffer, x1, y1, x2, y1 + stroke, color);
	//- 	addRect(buffer, x1, y2 - stroke, x2, y2, color);
	//- 	addRect(buffer, x1, y1 + stroke, x1 + stroke, y2 - stroke, color);
	//- 	addRect(buffer, x2 - stroke, y1 + stroke, x2, y2 - stroke, color);
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	tesselator.end();
	//- 	//# else
	//- 	RenderSystem.disableTexture();
	//- 	tesselator.end();
	//- 	//# end
	//- }
	//# end

	//# if RENDERING == "POSE_STACK"
	//- /**
	//-  * Adds a rectangle to the given buffer builder.
	//-  * @param buffer The builder to append to
	//-  * @param color  the color of the rect
	//-  */
	//- public static void addRect(BufferBuilder buffer, int x1, int y1, int x2, int y2, CoatColor color) {
	//- 	withColor(buffer.vertex(x1, y2, 0), color).endVertex();
	//- 	withColor(buffer.vertex(x2, y2, 0), color).endVertex();
	//- 	withColor(buffer.vertex(x2, y1, 0), color).endVertex();
	//- 	withColor(buffer.vertex(x1, y1, 0), color).endVertex();
	//- }
	//# end

	public static void drawHorizontalGradient(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# elif RENDERING == "GUI_GRAPHICS"
			//- GuiGraphics graphics,
			//# end
			int left,
			int top,
			int right,
			int bottom,
			CoatColor leftColor,
			CoatColor rightColor
	) {
		//# if MC_VERSION_NUMBER >= 12108
		ScreenRectangle rect = new ScreenRectangle(left, top, right - left, bottom - top);
		CoatGuiGraphics coatGraphics = (CoatGuiGraphics) graphics;
		coatGraphics.coat_submitGuiRenderState(
				new IndividuallyColoredRectangleRenderState(
						RenderPipelines.GUI,
						TextureSetup.noTexture(),
						new Matrix3x2f(graphics.pose()),
						rect,
						leftColor.getArgb(),
						rightColor.getArgb(),
						rightColor.getArgb(),
						leftColor.getArgb(),
						coatGraphics.coat_currentScissorArea()
				)
		);
		//# else
		//- RenderSystem.enableBlend();
		//- RenderSystem.defaultBlendFunc();

		//- Tesselator tesselator = Tesselator.getInstance();

		//- //# if MC_VERSION_NUMBER >= 12100
		//- BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//- bufferBuilder.addVertex(left, bottom, 0).setColor(leftColor.getArgb());
		//- bufferBuilder.addVertex(right, bottom, 0).setColor(rightColor.getArgb());
		//- bufferBuilder.addVertex(right, top, 0).setColor(rightColor.getArgb());
		//- bufferBuilder.addVertex(left, top, 0).setColor(leftColor.getArgb());
		//- BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
		//- //# else
		//- BufferBuilder bufferBuilder = tesselator.getBuilder();
		//- //# if MC_VERSION_NUMBER >= 11700
		//- bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		//- //# else
		//- RenderSystem.shadeModel(GL11.GL_SMOOTH);
		//- bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_COLOR);
		//- //# end
		//- withColor(bufferBuilder.vertex(left, bottom, 0D), leftColor).endVertex();
		//- withColor(bufferBuilder.vertex(right, bottom, 0D), rightColor).endVertex();
		//- withColor(bufferBuilder.vertex(right, top, 0D), rightColor).endVertex();
		//- withColor(bufferBuilder.vertex(left, top, 0D), leftColor).endVertex();
		//- tesselator.end();
		//- //# end

		//- RenderSystem.disableBlend();
		//# end
	}

	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR" || RENDERING == "GUI_GRAPHICS"
	public static void drawTexture(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			//# if MC_VERSION_NUMBER >= 12111
			Identifier texture,
			//# else
			//- ResourceLocation texture,
			//# end
			int left,
			int top,
			int width,
			int height
	) {
		//# if MC_VERSION_NUMBER >= 12108
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, left, top, width, height);
		//# elif MC_VERSION_NUMBER >= 12103
		//- graphics.blitSprite(RenderType::guiTextured, texture, left, top, width, height);
		//# elif MC_VERSION_NUMBER >= 12002
		//- graphics.blitSprite(texture, left, top, width, height);
		//# else
		//- drawTexture(graphics, texture, left, top, 0, 0, width, height);
		//# end
	}

	//# if MC_VERSION_NUMBER < 12002
	//- public static void drawTexture(
	//- 		GuiGraphics graphics,
	//- 		ResourceLocation texture,
	//- 		int left,
	//- 		int top,
	//- 		int textureLeft,
	//- 		int textureTop,
	//- 		int width,
	//- 		int height
	//- ) {
	//- 	graphics.blit(texture, left, top, textureLeft, textureTop, width, height);
	//- }
	//# end

	public static void drawTintedTiledTexture(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			//# if MC_VERSION_NUMBER >= 12111
			Identifier texture,
			//# else
			//- ResourceLocation texture,
			//# end
			int left,
			int top,
			int right,
			int bottom,
			int textureScale,
			int textureYOffset,
			CoatColor color
	) {
		//# if MC_VERSION_NUMBER >= 12103
		int width = right - left;
		int height = bottom - top;
		graphics.blit(
				//# if MC_VERSION_NUMBER >= 12108
				RenderPipelines.GUI_TEXTURED,
				//# elif MC_VERSION_NUMBER >= 12103
				//- RenderType::guiTextured,
				//# end
				texture,
				left,
				top,
				right,
				bottom + textureYOffset,
				width,
				height,
				textureScale,
				textureScale,
				color.getArgb()
		);
		//# else
		//- setShaderColor(color);
		//- drawTiledTexture(graphics, texture, left, top, right, bottom, textureScale, textureYOffset);
		//- resetShaderColor();
		//# end
	}

	public static void drawTiledTexture(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			//# if MC_VERSION_NUMBER >= 12111
			Identifier texture,
			//# else
			//- ResourceLocation texture,
			//# end
			int left,
			int top,
			int right,
			int bottom,
			int textureScale,
			int textureYOffset
	) {
		int width = right - left;
		int height = bottom - top;
		graphics.blit(
				//# if MC_VERSION_NUMBER >= 12108
				RenderPipelines.GUI_TEXTURED,
				//#elif MC_VERSION_NUMBER >=12103
				//- RenderType::guiTextured,
				//# end
				texture,
				left,
				top,
				right,
				bottom + textureYOffset,
				width,
				height,
				textureScale,
				textureScale
		);
	}
	//# else
	//- public static void drawTintedTiledTexture(
	//- 		ResourceLocation texture,
	//- 		int left,
	//- 		int top,
	//- 		int right,
	//- 		int bottom,
	//- 		int z,
	//- 		float textureScale,
	//- 		int textureYOffset,
	//- 		CoatColor color
	//- ) {
	//- 	Tesselator tesselator = Tesselator.getInstance();
	//- 	setShaderTexture(texture);
	//- 	setShaderColor(color);
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShader(GameRenderer::getPositionTexShader);
	//- 	BufferBuilder bufferBuilder = tesselator.getBuilder();
	//- 	bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
	//- 	//# else
	//- 	RenderSystem.enableTexture();
	//- 	BufferBuilder bufferBuilder = tesselator.getBuilder();
	//- 	bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
	//- 	//# end
	//- 	bufferBuilder.vertex(left, bottom, z).uv(left / textureScale, (bottom + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(right, bottom, z).uv(right / textureScale, (bottom + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(right, top, z).uv(right / textureScale, (top + textureYOffset) / textureScale).endVertex();
	//- 	bufferBuilder.vertex(left, top, z).uv(left / textureScale, (top + textureYOffset) / textureScale).endVertex();
	//- 	tesselator.end();
	//- 	resetShaderColor();
	//- }
	//# end

	//# if MC_VERSION_NUMBER >= 12103
	public static void drawInsetGradientTexture(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			//# if MC_VERSION_NUMBER >= 12111
			Identifier texture,
			//# else
			//- ResourceLocation texture,
			//# end
			int left,
			int top,
			int right,
			int bottom,
			int textureScale,
			CoatColor outerColor,
			CoatColor innerColor
	) {
		int width = right - left;
		int height = bottom - top;
		int middleOffset = height / 2;

		ScreenRectangle topLeft = new ScreenRectangle(left, top, middleOffset, middleOffset);
		ScreenRectangle topMiddle = new ScreenRectangle(left + middleOffset, top, width - 2 * middleOffset, middleOffset);
		ScreenRectangle topRight = new ScreenRectangle(right - middleOffset, top, middleOffset, middleOffset);
		ScreenRectangle bottomLeft = new ScreenRectangle(left, top + middleOffset, middleOffset, middleOffset);
		ScreenRectangle bottomMiddle = new ScreenRectangle(left + middleOffset, top + middleOffset, width - 2 * middleOffset, middleOffset);
		ScreenRectangle bottomRight = new ScreenRectangle(right - middleOffset, top + middleOffset, middleOffset, middleOffset);

		//# if MC_VERSION_NUMBER >= 12108
		AbstractTexture loadedTexture = Minecraft.getInstance().getTextureManager().getTexture(texture);
		TextureSetup textureSetup = TextureSetup.singleTexture(
				loadedTexture.getTextureView()
				/*# if MC_VERSION_NUMBER >= 12111 */, loadedTexture.getSampler()/*# end */
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, topLeft, textureScale, outerColor, outerColor, innerColor, outerColor
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, topMiddle, textureScale, outerColor, outerColor, innerColor, innerColor
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, topRight, textureScale, outerColor, outerColor, outerColor, innerColor
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, bottomLeft, textureScale, outerColor, innerColor, outerColor, outerColor
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, bottomMiddle, textureScale, innerColor, innerColor, outerColor, outerColor
		);
		submitIndividuallyColoredBlitRectable(
				graphics, textureSetup, bottomRight, textureScale, innerColor, outerColor, outerColor, outerColor
		);
		//# else
		//- graphics.drawSpecial(bufferSource -> {
		//- 	VertexConsumer consumer = bufferSource.getBuffer(RenderType.guiTextured(texture));
		//- 	appendTintedTexturedRect(consumer, topLeft, textureScale, outerColor, outerColor, innerColor, outerColor);
		//- 	appendTintedTexturedRect(consumer, topMiddle, textureScale, outerColor, outerColor, innerColor, innerColor);
		//- 	appendTintedTexturedRect(consumer, topRight, textureScale, outerColor, outerColor, outerColor, innerColor);
		//- 	appendTintedTexturedRect(consumer, bottomLeft, textureScale, outerColor, innerColor, outerColor, outerColor);
		//- 	appendTintedTexturedRect(consumer, bottomMiddle, textureScale, innerColor, innerColor, outerColor, outerColor);
		//- 	appendTintedTexturedRect(consumer, bottomRight, textureScale, innerColor, outerColor, outerColor, outerColor);
		//- });
		//# end
	}
	//# else
	//- public static void drawInsetGradientTexture(
	//- 		ResourceLocation texture,
	//- 		int left,
	//- 		int top,
	//- 		int right,
	//- 		int bottom,
	//- 		int z,
	//- 		float textureScale,
	//- 		CoatColor outerColor,
	//- 		CoatColor innerColor
	//- ) {
	//- 	RenderSystem.enableDepthTest();
	//- 	RenderSystem.depthFunc(GL11.GL_LEQUAL);
	//- 	//# if MC_VERSION_NUMBER >= 12100
	//- 	RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
	//- 	//# elif MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
	//- 	//# else
	//- 	RenderSystem.shadeModel(GL11.GL_SMOOTH);
	//- 	RenderSystem.enableTexture();
	//- 	//# end
	//- 	setShaderTexture(texture);
	//- 	resetShaderColor();
	//- 	Tesselator tesselator = Tesselator.getInstance();

	//- 	int width = right - left;
	//- 	int height = bottom - top;
	//- 	int middleOffset = height / 2;


	//- 	//# if MC_VERSION_NUMBER >= 12100
	//- 	BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX_COLOR);

	//- 	withColor(buffer.addVertex(left, top, z), outerColor).setUv(0F, 0F);
	//- 	withColor(buffer.addVertex(left + middleOffset, top + middleOffset, z), innerColor).setUv(middleOffset / textureScale, middleOffset / textureScale);
	//- 	withColor(buffer.addVertex(right, top, z), outerColor).setUv(width / textureScale, 0F);
	//- 	withColor(buffer.addVertex(right - middleOffset, top + middleOffset, z), innerColor).setUv((width - middleOffset) / textureScale, middleOffset / textureScale);
	//- 	withColor(buffer.addVertex(right, bottom, z), outerColor).setUv(width / textureScale, height / textureScale);
	//- 	withColor(buffer.addVertex(left + middleOffset, bottom - middleOffset, z), innerColor).setUv(middleOffset / textureScale, (height - middleOffset) / textureScale);
	//- 	withColor(buffer.addVertex(left, bottom, z), outerColor).setUv(0F, height / textureScale);
	//- 	withColor(buffer.addVertex(left, top, z), outerColor).setUv(0F, 0F);
	//- 	BufferUploader.drawWithShader(buffer.buildOrThrow());
	//- 	//# else
	//- 	BufferBuilder buffer = tesselator.getBuilder();
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_TEX);
	//- 	//# else
	//- 	buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR_TEX);
	//- 	//# end

	//- 	withColor(buffer.vertex(left, top, z), outerColor).uv(0F, 0F).endVertex();
	//- 	withColor(buffer.vertex(left + middleOffset, top + middleOffset, z), innerColor).uv(middleOffset / textureScale, middleOffset / textureScale).endVertex();
	//- 	withColor(buffer.vertex(right, top, z), outerColor).uv(width / textureScale, 0F).endVertex();
	//- 	withColor(buffer.vertex(right - middleOffset, top + middleOffset, z), innerColor).uv((width - middleOffset) / textureScale, middleOffset / textureScale).endVertex();
	//- 	withColor(buffer.vertex(right, bottom, z), outerColor).uv(width / textureScale, height / textureScale).endVertex();
	//- 	withColor(buffer.vertex(left + middleOffset, bottom - middleOffset, z), innerColor).uv(middleOffset / textureScale, (height - middleOffset) / textureScale).endVertex();
	//- 	withColor(buffer.vertex(left, bottom, z), outerColor).uv(0F, height / textureScale).endVertex();
	//- 	withColor(buffer.vertex(left, top, z), outerColor).uv(0F, 0F).endVertex();
	//- 	tesselator.end();
	//- 	//# end
	//- }
	//# end

	//# if MC_VERSION_NUMBER >= 12108
	private static void submitIndividuallyColoredBlitRectable(
			//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
			GuiGraphicsExtractor graphics,
			//# else
			//- GuiGraphics graphics,
			//# end
			TextureSetup textureSetup,
			ScreenRectangle rect,
			float textureScale,
			CoatColor topLeftColor,
			CoatColor topRightColor,
			CoatColor bottomRightColor,
			CoatColor bottomLeftColor
	) {
		CoatGuiGraphics coatGraphics = (CoatGuiGraphics) graphics;
		coatGraphics.coat_submitGuiRenderState(new IndividuallyColoredBlitRenderState(
				RenderPipelines.GUI_TEXTURED,
				textureSetup,
				new Matrix3x2f(graphics.pose()),
				rect,
				asVector4f(rect).div(textureScale),
				topLeftColor.getArgb(),
				topRightColor.getArgb(),
				bottomRightColor.getArgb(),
				bottomLeftColor.getArgb(),
				coatGraphics.coat_currentScissorArea()
		));
	}

	private static Vector4f asVector4f(ScreenRectangle rect) {
		return new Vector4f(rect.left(), rect.top(), rect.right(), rect.bottom());
	}
	//# elif MC_VERSION_NUMBER >= 12103
	//- private static void appendTintedTexturedRect(
	//- 		VertexConsumer vc,
	//- 		ScreenRectangle rect,
	//- 		float textureScale,
	//- 		CoatColor topLeftColor,
	//- 		CoatColor topRightColor,
	//- 		CoatColor bottomRightColor,
	//- 		CoatColor bottomLeftColor
	//- ) {
	//- 	vc.addVertex(rect.left(), rect.top(), 0).setUv(rect.left() / textureScale, rect.top() / textureScale).setColor(topLeftColor.getArgb());
	//- 	vc.addVertex(rect.left(), rect.bottom(), 0).setUv(rect.left() / textureScale, rect.bottom() / textureScale).setColor(bottomLeftColor.getArgb());
	//- 	vc.addVertex(rect.right(), rect.bottom(), 0).setUv(rect.right() / textureScale, rect.bottom() / textureScale).setColor(bottomRightColor.getArgb());
	//- 	vc.addVertex(rect.right(), rect.top(), 0).setUv(rect.right() / textureScale, rect.top() / textureScale).setColor(topRightColor.getArgb());
	//- }
	//# else
	//- private static <V extends VertexConsumer> V withColor(V vertexConsumer, CoatColor color) {
	//- 	//# if MC_VERSION_NUMBER >= 12100
	//- 	vertexConsumer.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	//- 	//# else
	//- 	vertexConsumer.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	//- 	//# end
	//- 	return vertexConsumer;
	//- }
	//# end

	//# if MC_VERSION_NUMBER < 12103
	//- public static void setShaderTexture(ResourceLocation texture) {
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShaderTexture(0, texture);
	//- 	//# else
	//- 	Minecraft.getInstance().getTextureManager().bind(texture);
	//- 	//# end
	//- }

	//- public static void setShaderColor(CoatColor color) {
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShaderColor(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
	//- 	//# else
	//- 	RenderSystem.color4f(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
	//- 	//# end
	//- }

	//- public static void resetShaderColor() {
	//- 	//# if MC_VERSION_NUMBER >= 11700
	//- 	RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	//- 	//# else
	//- 	RenderSystem.color4f(1F, 1F, 1F, 1F);
	//- 	//# end
	//- }
	//# end

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
