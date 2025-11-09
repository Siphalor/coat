package de.siphalor.coat.list.entry;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.util.CoatColor;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//- import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An entry linking to an entry.
 */
public class ConfigContainerLinkEntry extends ConfigContainerCompoundEntry {
	private static final String OPEN_TEXT_KEY = Coat.MOD_ID + ".tree.open";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component OPEN_TEXT = Component.translatable(OPEN_TEXT_KEY);
	//# else
	//- private static final Component OPEN_TEXT = new TranslatableComponent(OPEN_TEXT_KEY);
	//# end
	private static final CoatColor BACKGROUND_OUTER_COLOR = CoatColor.rgb(0x333333);
	private static final CoatColor BACKGROUND_INNER_COLOR = CoatColor.rgb(0x777777);

	private final ConfigContentWidget configWidget;
	private final Button button;
	//# if MC_VERSION_NUMBER >= 12108
	private final WidgetTooltipHolder tooltipHolder = new WidgetTooltipHolder();
	private int nameWidth;
	//# else
	//- private Component nameText;
	//# end

	/**
	 * Constructs a new link entry.
	 *
	 * @param configWidget The list widget that this entry refers to
	 */
	public ConfigContainerLinkEntry(ConfigContentWidget configWidget) {
		this.configWidget = configWidget;
		//# if MC_VERSION_NUMBER >= 11903
		button = Button.builder(OPEN_TEXT, button -> this.clicked()).size(50, 20).build();
		//# else
		//- button = new Button(0, 0, 50, 20, OPEN_TEXT, button -> this.clicked());
		//# end
		//# if MC_VERSION_NUMBER >= 12108
		tooltipHolder.set(Tooltip.create(configWidget.getName()));
		//# end
	}

	private void clicked() {
		ConfigScreen screen = ((ConfigScreen) Minecraft.getInstance().screen);
		ConfigTreeEntry treeEntry = configWidget.getTreeEntry();
		if (treeEntry.getParent() != null) {
			screen.openCategory(treeEntry);
		} else {
			screen.openTemporary(treeEntry);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		//# if MC_VERSION_NUMBER >= 12108
		nameWidth = newWidth - button.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN;
		//# else
		//- nameText = CoatUtil.intelliTrim(
		//- 		Minecraft.getInstance().font, configWidget.getName(),
		//- 		newWidth - button.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN
		//- );
		//# end
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end

		//# if MC_VERSION_NUMBER >= 12103
		CoatUtil.drawInsetGradientTexture(
				graphics,
				configWidget.getBackground(),
				x, y, x + entryWidth, y + entryHeight, 32,
				BACKGROUND_OUTER_COLOR, BACKGROUND_INNER_COLOR
		);
		//# else
		//- CoatUtil.drawInsetGradientTexture(
		//- 		configWidget.getBackground(),
		//- 		x, y, x + entryWidth, y + entryHeight, 0,
		//- 		32F, BACKGROUND_OUTER_COLOR, BACKGROUND_INNER_COLOR);
		//# end

		CoatUtil.setButtonPosition(button, x + getEntryWidth() - button.getWidth() - CoatUtil.MARGIN, y + CoatUtil.MARGIN);
		button.render(graphics, mouseX, mouseY, tickDelta);

		int nameX = x + CoatUtil.DOUBLE_MARGIN;
		int nameY = y + (entryHeight - 7) / 2;
		//# if MC_VERSION_NUMBER >= 12108
		CoatUtil.drawLeftAlignedText(
				graphics,
				Minecraft.getInstance().font,
				configWidget.getName(),
				new ScreenRectangle(nameX, nameY, nameWidth, 10),
				CoatUtil.TEXT_COLOR
		);
		//# else
		//- //# if RENDERING == "GUI_GRAPHICS"
		//- graphics.drawString(Minecraft.getInstance().font, nameText, nameX, nameY, CoatUtil.TEXT_COLOR.getArgb(), true);
		//- //# elif RENDERING == "POSE_STACK"
		//- Minecraft.getInstance().font.drawShadow(graphics, nameText, nameX, nameY, CoatUtil.TEXT_COLOR.getArgb());
		//- //# end

		//- if (hovered && nameText != configWidget.getName() && !button.isMouseOver(mouseX, mouseY)) {
		//- 	CoatUtil.renderTooltip(graphics, mouseX, mouseY, configWidget.getName());
		//- }
		//# end
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
	public List<? extends GuiEventListener> children() {
		return Collections.singletonList(button);
	}
}
