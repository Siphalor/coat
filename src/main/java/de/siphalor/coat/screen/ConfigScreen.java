package de.siphalor.coat.screen;

import com.mojang.blaze3d.systems.RenderSystem;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.util.CoatColor;
import de.siphalor.coat.util.CoatUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
//- import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;
import org.lwjgl.opengl.GL32;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * A Coat config screen.
 */
public class ConfigScreen extends Screen {
	private static final String ABORT_TEXT_KEY = Coat.MOD_ID + ".action.abort";
	private static final String ABORT_SCREEN_TITLE_TEXT_KEY = Coat.MOD_ID + ".action.abort.screen.title";
	private static final String ABORT_SCREEN_DESC_TEXT_KEY = Coat.MOD_ID + ".action.abort.screen.desc";
	private static final String SAVE_TEXT_KEY = Coat.MOD_ID + ".action.save";
	private static final String SAVE_WARNINGS_TEXT_KEY = Coat.MOD_ID + ".action.save.warnings";
	private static final String SAVE_ERRORS_TEXT_KEY = Coat.MOD_ID + ".action.save.errors";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component ABORT_TEXT = Component.translatable(ABORT_TEXT_KEY);
	private static final Component ABORT_SCREEN_TITLE_TEXT = Component.translatable(ABORT_SCREEN_TITLE_TEXT_KEY);
	private static final Component ABORT_SCREEN_DESC_TEXT = Component.translatable(ABORT_SCREEN_DESC_TEXT_KEY)
	private static final Component SAVE_TEXT = Component.translatable(SAVE_TEXT_KEY);
	private static final Component SAVE_WARNINGS_TEXT = Component.translatable(SAVE_WARNINGS_TEXT_KEY);
	private static final Component SAVE_ERRORS_TEXT = Component.translatable(SAVE_ERRORS_TEXT_KEY);
	//# else
	//- private static final Component ABORT_TEXT = new TranslatableComponent(ABORT_TEXT_KEY);
	//- private static final Component ABORT_SCREEN_TITLE_TEXT = new TranslatableComponent(ABORT_SCREEN_TITLE_TEXT_KEY);
	//- private static final Component ABORT_SCREEN_DESC_TEXT = new TranslatableComponent(ABORT_SCREEN_DESC_TEXT_KEY);
	//- private static final Component SAVE_TEXT = new TranslatableComponent(SAVE_TEXT_KEY);
	//- private static final Component SAVE_WARNINGS_TEXT = new TranslatableComponent(SAVE_WARNINGS_TEXT_KEY);
	//- private static final Component SAVE_ERRORS_TEXT = new TranslatableComponent(SAVE_ERRORS_TEXT_KEY);
	//# end
	private static final CoatColor BACKGROUND_TEXTURE_TINT_COLOR = CoatColor.rgb(0x777777);

	private final Screen parent;
	private final Collection<ConfigCategoryWidget> widgets;
	private ConfigTreeEntry openCategory;
	/**
	 * A {@link Runnable} that runs after all {@link de.siphalor.coat.handler.ConfigEntryHandler#save(Object)}
	 * calls when the user tries to save the configuration changes.
	 */
	@Setter
	private Runnable onSave = () -> {};
	private Component visualTitle;

	private int panelWidth;
	/**
	 * The tree pane widget
	 */
	@Getter
	private DynamicEntryListWidget<ConfigTreeEntry> treeWidget;
	private Button abortButton;
	private Button saveButton;
	/**
	 * The currently opened list widget.
	 */
	@Getter
	private ConfigContentWidget contentWidget;

	/**
	 * Creates a new config screen.
	 * @param parent  The previously opened screen that this screen should return the user to
	 * @param title   The title of this config screen. Typically contains the name of the mod
	 * @param widgets The categories/lists that this screen will be displaying
	 */
	public ConfigScreen(Screen parent, Component title, Collection<ConfigCategoryWidget> widgets) {
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
		treeWidget = new DynamicEntryListWidget<>(minecraft, panelWidth, height - 60, 20, (int) (panelWidth * 0.8F));
		//# if !TRANSPARENT_MENUS
		//- treeWidget.setBackgroundBrightness(0.5F);
		//# end

		//# if MC_VERSION_NUMBER >= 11903
		abortButton = Button.builder(ABORT_TEXT, button -> onClose()).pos(CoatUtil.MARGIN, 0).size(0, 20).build();
		saveButton = Button.builder(SAVE_TEXT, this::clickSave).pos(CoatUtil.MARGIN, 0).size(0, 20).build();
		//# else
		//- abortButton = new Button(CoatUtil.MARGIN, 0, 0, 20, ABORT_TEXT, button -> onClose());
		//- saveButton = new Button(CoatUtil.MARGIN, 0, 0, 20, SAVE_TEXT, this::clickSave);
		//# end

		//# if MC_VERSION_NUMBER >= 11700
		addRenderableWidget(abortButton);
		addRenderableWidget(saveButton);
		addRenderableWidget(treeWidget);
		//# else
		//- addButton(abortButton);
		//- addButton(saveButton);
		//- children.add(treeWidget);
		//# end

		for (ConfigCategoryWidget widget : widgets) {
			treeWidget.addEntry(widget.getTreeEntry());
		}

		super.init();

		openCategory(treeWidget.getEntry(0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose() {
		Minecraft.getInstance().setScreen(
				new ConfirmScreen(action -> {
					if (action) {
						Minecraft.getInstance().setScreen(parent);
					} else {
						Minecraft.getInstance().setScreen(this);
					}
				},
				ABORT_SCREEN_TITLE_TEXT,
				ABORT_SCREEN_DESC_TEXT));
	}

	/**
	 * Triggers the save listeners
	 */
	protected void onSave() {
		for (ConfigCategoryWidget widget : widgets) {
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
	protected void clickSave(Button button) {
		List<Message> warnings = new LinkedList<>();
		List<Message> errors = new LinkedList<>();
		int warningSev = Message.Level.WARNING.getSeverity();
		int errorSev = Message.Level.ERROR.getSeverity();
		treeWidget.entries().stream().flatMap(entry -> entry.getMessages().stream()).forEach(message -> {
			int sev = message.getLevel().getSeverity();
			if (sev >= errorSev) {
				errors.add(message);
			} else if (sev >= warningSev) {
				warnings.add(message);
			}
		});

		Runnable saveRunnable = () -> {
			onSave();
			Minecraft.getInstance().setScreen(parent);
		};

		Runnable warningOpener = () -> {
			Minecraft.getInstance().setScreen(new MessagesScreen(
					SAVE_WARNINGS_TEXT,
					this,
					saveRunnable,
					warnings
			));
		};

		if (!errors.isEmpty()) {
			Minecraft.getInstance().setScreen(new MessagesScreen(
					SAVE_ERRORS_TEXT,
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

			Deque<EntryContainer> newHierarchy = new LinkedList<>();
			newHierarchy.add(category);
			while (newHierarchy.getFirst().getParent() != treeWidget) {
				newHierarchy.push(newHierarchy.getFirst().getParent());
			}

			EntryContainer cur = openCategory;
			EntryContainer last = null;
			while (cur != treeWidget) {
				if (cur == category) {
					break;
				}
				if (newHierarchy.contains(cur)) {
					if (last != null) {
						if (last instanceof ConfigTreeEntry) {
							((ConfigTreeEntry) last).removeTemporaryTrees();
						}
					}
					break;
				}
				last = cur;
				cur = cur.getParent();
			}
			if (cur == treeWidget) {
				if (((ConfigTreeEntry) last).removeTemporaryTrees()) {
					treeWidget.entryHeightChanged(last);
				}
			}
		}

		if (contentWidget != null) {
			//# if MC_VERSION_NUMBER >= 11700
			removeWidget(contentWidget);
			//# else
			//- children.remove(contentWidget);
			//# end
		}
		openCategory = category;
		category.setOpen(true);

		EntryContainer parent = category;
		while ((parent = parent.getParent()) instanceof ConfigTreeEntry) {
			((ConfigTreeEntry) parent).setExpanded(true);
		}

		contentWidget = category.getContentWidget();
		//# if MC_VERSION_NUMBER >= 11700
		addRenderableWidget(contentWidget);
		//# else
		//- children.add(contentWidget);
		//# end
		contentWidget.setPosition(panelWidth, 20);
		contentWidget.setRowWidth(500);

		if (contentWidget.getName() == null || contentWidget.getName().getString().isEmpty() || contentWidget.getName().getString().equals(title.getString())) {
			visualTitle = title;
		} else {
			visualTitle = title.copy().append(" - ").append(contentWidget.getName());
		}

		resize(minecraft, width, height);
	}

	public void openTemporary(ConfigTreeEntry temporaryTreeEntry) {
		// I just assume that temporaryWidget is a child of openCategory

		openCategory.removeTemporaryTrees();
		openCategory.addTemporaryTree(temporaryTreeEntry);
		openCategory(temporaryTreeEntry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		this.width = width;
		this.height = height;

		panelWidth = Math.max(100, (int) (width * 0.2));
		treeWidget.resize(panelWidth, height - 20);
		contentWidget.setPosition(panelWidth, 20);
		contentWidget.resize(width - panelWidth, height - 20);

		CoatUtil.setButtonY(saveButton, height - 20 - CoatUtil.MARGIN);
		CoatUtil.setButtonY(abortButton, height - 40 - CoatUtil.DOUBLE_MARGIN);
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
		contentWidget.tick();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# end

		//# if MC_VERSION_NUMBER < 11700
		//- treeWidget.render(graphics, mouseX, mouseY, delta);
		//- contentWidget.renderWidget(graphics, mouseX, mouseY, delta);
		//# end

		super.render(graphics, mouseX, mouseY, delta);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL32.GL_LEQUAL);

		CoatUtil.drawHorizontalGradient(panelWidth, 20, panelWidth + 8, height, CoatColor.BLACK.withAlpha(0x77), CoatColor.TRANSPARENT);

		RenderSystem.disableDepthTest();

		RenderSystem.enableBlend();
		//# if MC_VERSION_NUMBER >= 12100
		CoatUtil.drawTintedTexture(graphics, 0, 0, width, 20, contentWidget.getBackground(), 32, 0, BACKGROUND_TEXTURE_TINT_COLOR);
		//# else
		//- CoatUtil.drawTintedTexture(0, 0, width, 20, 0, contentWidget.getBackground(), 32F, 0, BACKGROUND_TEXTURE_TINT_COLOR);
		//# end
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();

		//# if RENDERING == "GUI_GRAPHICS"
		graphics.pose().translate(0, 0, 10);
		graphics.drawCenteredString(this.font, this.visualTitle, this.width / 2, 8, CoatColor.WHITE.getArgb());
		graphics.pose().translate(0, 0, -10);
		//# elif RENDERING == "POSE_STACK"
		//- graphics.translate(0, 0, 10);
		//- drawCenteredString(graphics, font, this.visualTitle, this.width / 2, 8, CoatColor.WHITE.getArgb());
		//- graphics.translate(0, 0, -10);
		//# end
	}
}
