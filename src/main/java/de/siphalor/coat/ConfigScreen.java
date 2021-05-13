package de.siphalor.coat;

import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.list.ConfigListEntry;
import de.siphalor.coat.list.ConfigEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final String modid;
	private final Collection<ConfigListEntry> entries;
	private final Collection<ConfigTreeEntry> trees;
	private Runnable onSave;

	private int panelWidth;
	private ConfigEntryListWidget treeWidget;
	private ConfigEntryListWidget listWidget;

	public ConfigScreen(Screen parent, String modid, Collection<ConfigListEntry> entries, Collection<ConfigTreeEntry> trees) {
		super(new TranslatableText("coat.screen." + modid));
		this.parent = parent;
		this.modid = modid;
		this.entries = entries;
		this.trees = trees;
	}

	@Override
	protected void init() {
		panelWidth = 200;
		treeWidget = new ConfigEntryListWidget(client, panelWidth, height - 60, 20, height - 40, (int) (panelWidth * 0.8F));
		treeWidget.setRenderBackground(true);
		treeWidget.setBackground(new Identifier("textures/block/stone_bricks.png"));
		children.add(treeWidget);
		listWidget = new ConfigEntryListWidget(client, width - panelWidth, height - 20, 20, height, 260);
		listWidget.setLeftPos(panelWidth);
		listWidget.setRenderBackground(true);
		listWidget.setBackground(new Identifier("textures/block/bricks.png"));
		children.add(listWidget);

		for (ConfigListEntry entry : entries) {
			listWidget.addEntry(entry);
		}

		for (ConfigTreeEntry tree : trees) {
			treeWidget.addEntry(tree);
		}

		super.init();
	}

	public ConfigEntryListWidget getTreeWidget() {
		return treeWidget;
	}

	public ConfigEntryListWidget getListWidget() {
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

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;

		panelWidth = Math.max(64, (int) (width * 0.3));
		treeWidget.widthChanged(panelWidth);
		listWidget.setLeftPos(panelWidth);
		listWidget.widthChanged(width - panelWidth);
	}

	@Override
	public void tick() {
		super.tick();
		listWidget.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		treeWidget.render(matrices, mouseX, mouseY, delta);
		listWidget.render(matrices, mouseX, mouseY, delta);

		client.getTextureManager().bindTexture(listWidget.getBackground());
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(519);
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
