package de.siphalor.coat.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL32;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * A Coat config screen.
 */
public class ConfigScreen extends Screen {
	private static final Text ABORT_TEXT = Text.translatable(Coat.MOD_ID + ".action.abort");
	private static final Text SAVE_TEXT =  Text.translatable(Coat.MOD_ID + ".action.save");

	private final Screen parent;
	private final Collection<ConfigCategoryWidget> widgets;
	private ConfigTreeEntry openCategory;
	private Runnable onSave = () -> {};
	private Text visualTitle;

	private int panelWidth;
	private DynamicEntryListWidget<ConfigTreeEntry> treeWidget;
	private ButtonWidget abortButton;
	private ButtonWidget saveButton;
	private ConfigContentWidget contentWidget;

	/**
	 * Creates a new config screen.
	 * @param parent  The previously opened screen that this screen should return the user to
	 * @param title   The title of this config screen. Typically contains the name of the mod
	 * @param widgets The categories/lists that this screen will be displaying
	 */
	public ConfigScreen(Screen parent, Text title, Collection<ConfigCategoryWidget> widgets) {
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
		treeWidget = new DynamicEntryListWidget<>(client, panelWidth, height - 60, 20, (int) (panelWidth * 0.8F));
		treeWidget.setBackgroundBrightness(0.5F);
		treeWidget.setBackground(new Identifier("textures/block/cherry_log.png"));
		addDrawableChild(treeWidget);

		for (ConfigCategoryWidget widget : widgets) {
			treeWidget.addEntry(widget.getTreeEntry());
		}

		abortButton = ButtonWidget.builder(ABORT_TEXT, button -> close())
				.position(CoatUtil.MARGIN, 0).size(0, 20).build();
		saveButton =  ButtonWidget.builder(SAVE_TEXT, this::clickSave)
				.position(CoatUtil.MARGIN, 0).size(0, 20).build();
		addDrawableChild(abortButton);
		addDrawableChild(saveButton);

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
	public ConfigContentWidget getContentWidget() {
		return contentWidget;
	}

	@Override
	public void close() {
		MinecraftClient.getInstance().setScreen(
				new ConfirmScreen(action -> {
					if (action) {
						MinecraftClient.getInstance().setScreen(parent);
					} else {
						MinecraftClient.getInstance().setScreen(this);
					}
				},
				Text.translatable(Coat.MOD_ID + ".action.abort.screen.title"),
				Text.translatable(Coat.MOD_ID + ".action.abort.screen.desc")));
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
	protected void clickSave(ButtonWidget button) {
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
			MinecraftClient.getInstance().setScreen(parent);
		};

		Runnable warningOpener = () -> {
			MinecraftClient.getInstance().setScreen(new MessagesScreen(
					Text.translatable(Coat.MOD_ID + ".action.save.warnings"),
					this,
					saveRunnable,
					warnings
			));
		};

		if (!errors.isEmpty()) {
			MinecraftClient.getInstance().setScreen(new MessagesScreen(
					Text.translatable(Coat.MOD_ID + ".action.save.errors"),
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
			remove(contentWidget);
		}
		openCategory = category;
		category.setOpen(true);

		EntryContainer parent = category;
		while ((parent = parent.getParent()) instanceof ConfigTreeEntry) {
			((ConfigTreeEntry) parent).setExpanded(true);
		}

		contentWidget = category.getContentWidget();
		addDrawableChild(contentWidget);
		contentWidget.setPosition(panelWidth, 20);
		contentWidget.setRowWidth(500);

		if (contentWidget.getName() == null || contentWidget.getName().getString().isEmpty() || contentWidget.getName().getString().equals(title.getString())) {
			visualTitle = title;
		} else {
			visualTitle = title.copy().append(" - ").append(contentWidget.getName());
		}

		resize(client, width, height);
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
	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;

		panelWidth = Math.max(100, (int) (width * 0.2));
		treeWidget.resize(panelWidth, height - 20);
		contentWidget.setPosition(panelWidth, 20);
		contentWidget.resize(width - panelWidth, height - 20);

		saveButton.setY(height - 20 - CoatUtil.MARGIN);
		abortButton.setY(saveButton.getY() - 20 - CoatUtil.MARGIN);
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
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		super.render(drawContext, mouseX, mouseY, delta);

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL32.GL_LEQUAL);

		CoatUtil.drawHorizontalGradient(panelWidth, 20, panelWidth + 8, height, 0x00000077, 0x00000000);

		RenderSystem.disableDepthTest();

		CoatUtil.drawVerticalGradientTexture(0, 0, width, 20, contentWidget.getBackground(), 32F, 0x777777ff, 0x777777ff);

		drawContext.getMatrices().translate(0, 0, 10);
		drawContext.drawCenteredTextWithShadow(this.textRenderer, this.visualTitle, this.width / 2, 8, 0xffffff);
		drawContext.getMatrices().translate(0, 0, -10);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackgroundTexture(context);
	}
}
