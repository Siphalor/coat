package de.siphalor.coat.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * A Coat config screen.
 */
public class ConfigScreen extends Screen {
	private static final String ABORT_TEXT_KEY = Coat.MOD_ID + ".action.abort";
	private static final String SAVE_TEXT_KEY =  Coat.MOD_ID + ".action.save";

	private final Screen parent;
	private final Collection<ConfigListWidget> widgets;
	private ConfigTreeEntry openCategory;
	private Runnable onSave = () -> {};
	private Text visualTitle;

	private int panelWidth;
	private DynamicEntryListWidget<ConfigTreeEntry> treeWidget;
	private ButtonWidget abortButton;
	private ButtonWidget saveButton;
	private ConfigListWidget listWidget;

	/**
	 * Creates a new config screen.
	 * @param parent  The previously opened screen that this screen should return the user to
	 * @param title   The title of this config screen. Typically contains the name of the mod
	 * @param widgets The categories/lists that this screen will be displaying
	 */
	public ConfigScreen(Screen parent, Text title, Collection<ConfigListWidget> widgets) {
		super(title);
		this.visualTitle = title.copy().append(" - ").append("missingno");
		this.parent = parent;
		this.widgets = widgets;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		panelWidth = 200;
		treeWidget = new DynamicEntryListWidget<>(MinecraftClient.getInstance(), panelWidth, height - 60, 20, (int) (panelWidth * 0.8F));
		treeWidget.setBackgroundBrightness(0.5F);
		treeWidget.setBackground(new Identifier("textures/block/stone_bricks.png"));
		children.add(treeWidget);

		for (ConfigListWidget widget : widgets) {
			treeWidget.addEntry(widget.getTreeEntry());
		}

		abortButton = new ButtonWidget(CoatUtil.MARGIN, 0, 0, 20, I18n.translate(ABORT_TEXT_KEY), button -> onClose());
		saveButton =  new ButtonWidget(CoatUtil.MARGIN, 0, 0, 20, I18n.translate(SAVE_TEXT_KEY), this::clickSave);
		addButton(abortButton);
		addButton(saveButton);

		super.init();

		openCategory(treeWidget.getEntry(0));
	}

	/**
	 * Gets the tree pane widget.
	 *
	 * @return The tree widget
	 */
	public DynamicEntryListWidget<ConfigTreeEntry> getTreeWidget() {
		return treeWidget;
	}

	/**
	 * Gets the currently opened list widget.
	 *
	 * @return The list widget
	 */
	public ConfigListWidget getListWidget() {
		return listWidget;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Sets a {@link Runnable} that runs after all {@link de.siphalor.coat.handler.ConfigEntryHandler#save(Object)}
	 * calls when the user tries to save the configuration changes.
	 *
	 * @param onSave The runnable
	 */
	public void setOnSave(Runnable onSave) {
		this.onSave = onSave;
	}

	/**
	 * Triggers the save listeners
	 */
	protected void onSave() {
		for (ConfigListWidget widget : widgets) {
			widget.save();
		}
		onSave.run();
	}

	/**
	 * Called when the user clicks on the save button.
	 * This method checks for issues in the configuration and displays them to the user.
	 * If the user confirms the save procedures will be triggered.
	 *
	 * @param button The button that has been clicked on - unused
	 */
	protected void clickSave(ButtonWidget button) {
		List<Message> warnings = new LinkedList<>();
		List<Message> errors = new LinkedList<>();
		int warningSev = Message.Level.WARNING.getSeverity();
		int errorSev = Message.Level.ERROR.getSeverity();
		treeWidget.children().stream().flatMap(entry -> entry.getMessages().stream()).forEach(message -> {
			int sev = message.getLevel().getSeverity();
			if (sev >= errorSev) {
				errors.add(message);
			} else if (sev >= warningSev) {
				warnings.add(message);
			}
		});

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

	/**
	 * Open a certain category/list by a tree entry.
	 *
	 * @param category The tree entry that's list widget shall be opened
	 */
	public void openCategory(ConfigTreeEntry category) {
		if (openCategory != null) {
			openCategory.setOpen(false);
			children.remove(openCategory.getConfigWidget());
		}
		openCategory = category;
		category.setOpen(true);

		EntryContainer parent = category;
		while ((parent = parent.getParent()) instanceof ConfigTreeEntry) {
			((ConfigTreeEntry) parent).setExpanded(true);
		}

		listWidget = category.getConfigWidget();
		children.add(listWidget);
		listWidget.setPosition(panelWidth, 20);
		listWidget.setRowWidth(500);

		if (listWidget.getName() != null && !listWidget.getName().getString().isEmpty()) {
			visualTitle = title.copy().append(" - ").append(listWidget.getName());
		} else {
			visualTitle = title;
		}

		resize(MinecraftClient.getInstance(), width, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;

		panelWidth = Math.max(100, (int) (width * 0.2));
		treeWidget.resize(panelWidth, height - 20);
		listWidget.setPosition(panelWidth, 20);
		listWidget.resize(width - panelWidth, height - 20);

		saveButton.y  = height - 20 - CoatUtil.MARGIN;
		abortButton.y = saveButton.y - 20 - CoatUtil.MARGIN;
		saveButton.setWidth(panelWidth - CoatUtil.DOUBLE_MARGIN);
		abortButton.setWidth(saveButton.getWidth());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {
		super.tick();
		treeWidget.tick();
		listWidget.tick();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(int mouseX, int mouseY, float delta) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		treeWidget.render(mouseX, mouseY, delta);
		listWidget.render(mouseX, mouseY, delta);

		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.enableBlend();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlphaTest();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableTexture();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(panelWidth,      height, 0D).color(0, 0, 0, 200).next();
		bufferBuilder.vertex(panelWidth + 8D, height, 0D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth + 8D, 20D,    0D).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(panelWidth,      20D,    0D).color(0, 0, 0, 200).next();
		tessellator.draw();

		GlStateManager.disableDepthTest();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture();
		MinecraftClient.getInstance().getTextureManager().bindTexture(listWidget.getBackground());
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(0D,    20D, 0D).texture(0F,          20F / 32F).color(0x77, 0x77, 0x77, 0xff).next();
		bufferBuilder.vertex(width, 20D, 0D).texture(width / 32F, 20F / 32F).color(0x77, 0x77, 0x77, 0xff).next();
		bufferBuilder.vertex(width,  0D, 0D).texture(width / 32F, 0F       ).color(0x77, 0x77, 0x77, 0xff).next();
		bufferBuilder.vertex(0D,     0D, 0D).texture(0F,          0F       ).color(0x77, 0x77, 0x77, 0xff).next();
		tessellator.draw();

		drawCenteredString(MinecraftClient.getInstance().textRenderer, visualTitle.asFormattedString(), width / 2, 8, 0xffffff);

		super.render(mouseX, mouseY, delta);
	}
}
