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
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry linking to an entry.
 */
public class ConfigContainerLinkEntry extends ConfigContainerCompoundEntry {
	private static final Text OPEN_TEXT = Text.translatable(Coat.MOD_ID + ".tree.open");

	private final ConfigContentWidget configWidget;
	private final ButtonWidget button;
	private Text nameText;

	/**
	 * Constructs a new link entry.
	 *
	 * @param configWidget The list widget that this entry refers to
	 */
	public ConfigContainerLinkEntry(ConfigContentWidget configWidget) {
		this.configWidget = configWidget;
		button = ButtonWidget.createBuilder(OPEN_TEXT,
				button -> {
					ConfigScreen screen = ((ConfigScreen) MinecraftClient.getInstance().currentScreen);
					ConfigTreeEntry treeEntry = configWidget.getTreeEntry();
					if (treeEntry.getParent() != null) {
						screen.openCategory(treeEntry);
					} else {
						screen.openTemporary(treeEntry);
					}
				}
		).setSize(50, 20).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		nameText = CoatUtil.intelliTrim(
				MinecraftClient.getInstance().textRenderer, configWidget.getName(),
				newWidth - button.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int r = entryHeight / 2;

		MinecraftClient.getInstance().getTextureManager().bindTexture(configWidget.getBackground());
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, configWidget.getBackground());
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR_TEXTURE);
		buffer.vertex(x, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, 0F).next();
		buffer.vertex(x + r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture(r / 32F, r / 32F).next();
		buffer.vertex(x + entryWidth, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(entryWidth / 32F, 0F).next();
		buffer.vertex(x + entryWidth - r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture((entryWidth - r) / 32F, r / 32F).next();
		buffer.vertex(x + entryWidth, y + entryHeight, -100D).color(0x33, 0x33, 0x33, 0xff).texture(entryWidth / 32F, entryHeight / 32F).next();
		buffer.vertex(x + r, y + r, -100D).color(0x77, 0x77, 0x77, 0xff).texture(r / 32F, r / 32F).next();
		buffer.vertex(x, y + entryHeight, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, entryHeight / 32F).next();
		buffer.vertex(x, y, -100D).color(0x33, 0x33, 0x33, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		button.setX(x + getEntryWidth() - button.getWidth() - CoatUtil.MARGIN);
		button.setY(y + CoatUtil.MARGIN);
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, nameText, x + CoatUtil.DOUBLE_MARGIN, y + (entryHeight - 7) / 2F, CoatUtil.TEXT_COLOR);
		button.render(matrices, mouseX, mouseY, tickDelta);

		if (hovered && nameText != configWidget.getName() && !button.isMouseOver(mouseX, mouseY)) {
			CoatUtil.renderTooltip(matrices, mouseX, mouseY, configWidget.getName());
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
