package de.siphalor.coat.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListEntry;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConfigScreen extends Screen {
	private static final Text ABORT_TEXT = new TranslatableText(Coat.MOD_ID + ".action.abort");
	private static final Text SAVE_TEXT =  new TranslatableText(Coat.MOD_ID + ".action.save");

	private final Screen parent;
	private final Collection<ConfigListWidget> widgets;
	private ConfigTreeEntry openCategory;
	private Runnable onSave = () -> {};
	private Text visualTitle;

	private int panelWidth;
	private DynamicEntryListWidget treeWidget;
	private ButtonWidget abortButton;
	private ButtonWidget saveButton;
	private ConfigListWidget listWidget;

	public ConfigScreen(Screen parent, Text title, Collection<ConfigListWidget> widgets) {
		super(title);
		this.visualTitle = title.copy().append(" - ").append("missingno");
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

		abortButton = new ButtonWidget(CoatUtil.MARGIN, 0, 0, 20, ABORT_TEXT, button -> onClose());
		saveButton =  new ButtonWidget(CoatUtil.MARGIN, 0, 0, 20, SAVE_TEXT, this::clickSave);
		addButton(abortButton);
		addButton(saveButton);

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
		MinecraftClient.getInstance().openScreen(
				new ConfirmScreen(action -> {
					if (action) {
						MinecraftClient.getInstance().openScreen(parent);
					} else {
						MinecraftClient.getInstance().openScreen(this);
					}
				},
				new TranslatableText(Coat.MOD_ID + ".action.abort.screen.title"),
				new TranslatableText(Coat.MOD_ID + ".action.abort.screen.desc")));
	}

	public void setOnSave(Runnable onSave) {
		this.onSave = onSave;
	}

	protected void onSave() {
		onSave.run();
	}

	protected void clickSave(ButtonWidget button) {
		List<Message> warnings = new LinkedList<>();
		List<Message> errors = new LinkedList<>();
		int warningSev = Message.Level.WARNING.getSeverity();
		int errorSev = Message.Level.ERROR.getSeverity();
		int sev;
		for (Message message : treeWidget.getMessages()) {
			sev = message.getLevel().getSeverity();
			if (sev >= errorSev) {
				errors.add(message);
			} else if (sev >= warningSev) {
				warnings.add(message);
			}
		}

		Runnable saveRunnable = () -> {
			onSave();
			MinecraftClient.getInstance().openScreen(parent);
		};

		Runnable warningOpener = () -> {
			MinecraftClient.getInstance().openScreen(new MessagesScreen(
					new TranslatableText(Coat.MOD_ID + ".action.save.warnings"),
					this,
					saveRunnable,
					warnings
			));
		};

		if (!errors.isEmpty()) {
			MinecraftClient.getInstance().openScreen(new MessagesScreen(
					new TranslatableText(Coat.MOD_ID + ".action.save.errors"),
					this,
					warnings.isEmpty() ? saveRunnable : warningOpener,
					errors
			));
		} else if (!warnings.isEmpty()) {
			warningOpener.run();
		} else {
			saveRunnable.run();
		}
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

		if (listWidget.getName() != null && !listWidget.getName().getString().isEmpty()) {
			visualTitle = title.copy().append(" - ").append(listWidget.getName());
		} else {
			visualTitle = title;
		}

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

		saveButton.y  = height - 20 - CoatUtil.MARGIN;
		abortButton.y = saveButton.y - 20 - CoatUtil.MARGIN;
		saveButton.setWidth(panelWidth - CoatUtil.DOUBLE_MARGIN);
		abortButton.setWidth(saveButton.getWidth());
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
		RenderSystem.depthFunc(GL11.GL_LEQUAL);

		client.getTextureManager().bindTexture(treeWidget.getBackground());
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(0D,         height, -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, height / 32F).next();
		bufferBuilder.vertex(panelWidth, height, -100D).color(0x77, 0x77, 0x77, 0xff).texture(panelWidth / 32F, height / 32F).next();
		bufferBuilder.vertex(panelWidth, 20D,    -100D).color(0x77, 0x77, 0x77, 0xff).texture(panelWidth / 32F, 0F).next();
		bufferBuilder.vertex(0D,         20D,    -100D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		treeWidget.render(matrices, mouseX, mouseY, delta);
		listWidget.render(matrices, mouseX, mouseY, delta);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(panelWidth,      height, 0D).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(panelWidth + 8D, height, 0D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth + 8D, 20D,    0D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth,      20D,    0D).color(0, 0, 0, 200).next();
		tessellator.draw();

		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		client.getTextureManager().bindTexture(listWidget.getBackground());
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
		bufferBuilder.vertex(0D,    20D, 0D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 20F / 32F).next();
		bufferBuilder.vertex(width, 20D, 0D).color(0x77, 0x77, 0x77, 0xff).texture(width / 32F, 20F / 32F).next();
		bufferBuilder.vertex(width,  0D, 0D).color(0x77, 0x77, 0x77, 0xff).texture(width / 32F, 0F).next();
		bufferBuilder.vertex(0D,     0D, 0D).color(0x77, 0x77, 0x77, 0xff).texture(0F, 0F).next();
		tessellator.draw();

		drawCenteredText(matrices, this.textRenderer, this.visualTitle, this.width / 2, 8, 0xffffff);

		super.render(matrices, mouseX, mouseY, delta);
	}
}
