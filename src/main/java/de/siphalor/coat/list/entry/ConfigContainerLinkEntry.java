package de.siphalor.coat.list.entry;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.util.CoatColor;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry linking to an entry.
 */
public class ConfigContainerLinkEntry extends ConfigContainerCompoundEntry {
	private static final TranslatableText OPEN_TEXT = new TranslatableText(Coat.MOD_ID + ".tree.open");
	private static final CoatColor BACKGROUND_OUTER_COLOR = CoatColor.rgb(0x333333);
	private static final CoatColor BACKGROUND_INNER_COLOR = CoatColor.rgb(0x777777);

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
		button = new ButtonWidget(0, 0, 50, 20, OPEN_TEXT,
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
				MinecraftClient.getInstance().textRenderer, configWidget.getName(),
				newWidth - button.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

		CoatUtil.drawInsetGradientTexture(x, y, x + entryWidth, y + entryHeight, -100, configWidget.getBackground(), 32F, BACKGROUND_OUTER_COLOR, BACKGROUND_INNER_COLOR);

		button.x = x + getEntryWidth() - button.getWidth() - CoatUtil.MARGIN;
		button.y = y + CoatUtil.MARGIN;
		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, nameText, x + CoatUtil.DOUBLE_MARGIN, y + (entryHeight - 7) / 2F, CoatUtil.TEXT_COLOR.getArgb());
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
