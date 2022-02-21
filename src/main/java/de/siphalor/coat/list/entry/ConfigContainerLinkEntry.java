package de.siphalor.coat.list.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.TranslatableText;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry linking to an entry.
 */
public class ConfigContainerLinkEntry extends ConfigContainerCompoundEntry {
	private static final TranslatableText OPEN_TEXT = new TranslatableText(Coat.MOD_ID + ".tree.open");

	private final ConfigContentWidget configWidget;
	private final ButtonWidget button;
	private String nameText;

	/**
	 * Constructs a new link entry.
	 *
	 * @param configWidget The list widget that this entry refers to
	 */
	public ConfigContainerLinkEntry(ConfigContentWidget configWidget) {
		this.configWidget = configWidget;
		button = new ButtonWidget(0, 0, 50, 20, OPEN_TEXT.asFormattedString(),
				button -> {
					ConfigScreen screen = ((ConfigScreen) MinecraftClient.getInstance().currentScreen);
					ConfigTreeEntry treeEntry = configWidget.getTreeEntry();
					if (treeEntry.getParent() != null) {
						screen.openCategory(treeEntry);
					} else {
						screen.openTemporary(treeEntry);
					}
				}
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		nameText = CoatUtil.intelliTrim(
				MinecraftClient.getInstance().textRenderer, configWidget.getName().asFormattedString(),
				newWidth - button.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int r = entryHeight / 2;

		MinecraftClient.getInstance().getTextureManager().bindTexture(configWidget.getBackground());
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR_TEXTURE);
		buffer.vertex(x, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, 0F).next();
		buffer.vertex(x + r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture(r / 32F, r / 32F).next();
		buffer.vertex(x + entryWidth, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(entryWidth / 32F, 0F).next();
		buffer.vertex(x + entryWidth - r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture((entryWidth - r) / 32F, r / 32F).next();
		buffer.vertex(x + entryWidth, y + entryHeight, -100D).color(0x33, 0x33, 0x33, 0xff).texture(entryWidth / 32F, entryHeight / 32F).next();
		buffer.vertex(x + r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture(r / 32F, r / 32F).next();
		buffer.vertex(x, y + entryHeight, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, entryHeight / 32F).next();
		buffer.vertex(x, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		button.x = x + getEntryWidth() - button.getWidth() - CoatUtil.MARGIN;
		button.y = y + CoatUtil.MARGIN;
		MinecraftClient.getInstance().textRenderer.drawWithShadow(nameText, x + CoatUtil.DOUBLE_MARGIN, y + (entryHeight - 7) / 2F, CoatUtil.TEXT_COLOR);
		button.render(mouseX, mouseY, tickDelta);

		if (hovered && !nameText.equals(configWidget.getName().asFormattedString()) && !button.isMouseOver(mouseX, mouseY)) {
			CoatUtil.renderTooltip(mouseX, mouseY, configWidget.getName().asFormattedString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return 24;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends Element> children() {
		return Collections.singletonList(button);
	}
}
