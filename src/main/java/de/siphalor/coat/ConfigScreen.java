package de.siphalor.coat;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.list.ConfigListEntry;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final Collection<ConfigListWidget> widgets;
	private ConfigTreeEntry openCategory;
	private Runnable onSave;
	private Text title;

	private int panelWidth;
	private DynamicEntryListWidget treeWidget;
	private ConfigListWidget listWidget;

	public ConfigScreen(Screen parent, Text title, Collection<ConfigListEntry> entries, Collection<ConfigListWidget> widgets) {
		super(LiteralText.EMPTY);
		this.title = title.copy().append(" - ").append("missingno");
		this.parent = parent;
		this.widgets = widgets;
	}

	@Override
	protected void init() {
		panelWidth = 200;
		treeWidget = new DynamicEntryListWidget(client, panelWidth, height - 60, 20, (int) (panelWidth * 0.8F));
		treeWidget.setRenderBackground(false);
		treeWidget.setBackground(new Identifier("textures/block/stone_bricks.png"));
		children.add(treeWidget);

		for (ConfigListWidget widget : widgets) {
			treeWidget.addEntry(widget.getTreeEntry());
		}

		super.init();

		openCategory((ConfigTreeEntry) treeWidget.getEntry(0));
	}

	public DynamicEntryListWidget getTreeWidget() {
		return treeWidget;
	}

	public ConfigListWidget getListWidget() {
		return listWidget;
	}

	@Override
	public void onClose() {
		MinecraftClient.getInstance().openScreen(parent);
	}

	public void setOnSave(Runnable onSave) {
		this.onSave = onSave;
	}

	protected void onSave() {
		onSave.run();
	}

	public void openCategory(ConfigTreeEntry category) {
		if (openCategory != null) {
			openCategory.setOpen(false);
			children.remove(openCategory);
		}
		openCategory = category;
		category.setOpen(true);

		ConfigListEntry parent = category;
		while ((parent = parent.getParent()) instanceof ConfigTreeEntry) {
			((ConfigTreeEntry) parent).setExpanded(true);
		}

		listWidget = category.getConfigWidget();
		children.add(listWidget);
		listWidget.setPosition(panelWidth, 20);
		listWidget.setRowWidth(260);

		title.getSiblings().set(1, listWidget.getName());
		title = title.shallowCopy(); // Required to force update the rendered text

		resize(client, width, height);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;

		panelWidth = Math.max(64, (int) (width * 0.3));
		treeWidget.resize(panelWidth, height - 20);
		listWidget.setPosition(panelWidth, 20);
		listWidget.resize(width - panelWidth, height - 20);
	}

	@Override
	public void tick() {
		super.tick();
		treeWidget.tick();
		listWidget.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(519);

		client.getTextureManager().bindTexture(treeWidget.getBackground());
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(0D,         height, -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, height / 32F).next();
		bufferBuilder.vertex(panelWidth, height, -100D).color(0x77, 0x77, 0x77, 0xff).texture(panelWidth / 32F, height / 32F).next();
		bufferBuilder.vertex(panelWidth, 20D,    -100D).color(0x77, 0x77, 0x77, 0xff).texture(panelWidth / 32F, 0F).next();
		bufferBuilder.vertex(0D,         20D,    -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		treeWidget.render(matrices, mouseX, mouseY, delta);
		listWidget.render(matrices, mouseX, mouseY, delta);

		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(panelWidth,      height, -100D).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(panelWidth + 8D, height, -100D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth + 8D, 20D,    -100D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth,      20D,    -100D).color(0, 0, 0, 200).next();
		tessellator.draw();

		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		client.getTextureManager().bindTexture(listWidget.getBackground());
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(0D,    20D, -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 20F / 32F).next();
		bufferBuilder.vertex(width, 20D, -100D).color(0x77, 0x77, 0x77, 0xff).texture(width / 32F, 20F / 32F).next();
		bufferBuilder.vertex(width,  0D, -100D).color(0x77, 0x77, 0x77, 0xff).texture(width / 32F, 0F).next();
		bufferBuilder.vertex(0D,     0D, -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xffffff);

		super.render(matrices, mouseX, mouseY, delta);
	}
}
