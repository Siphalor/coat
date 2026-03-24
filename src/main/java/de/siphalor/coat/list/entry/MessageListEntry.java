package de.siphalor.coat.list.entry;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.screen.MessagesScreen;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
//- import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list entry linking to a message and providing functionality to jump to it.
 */
public class MessageListEntry extends ConfigContainerCompoundEntry {
	private static final String JUMP_TEXT_KEY = Coat.MOD_ID + ".message.jump";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component JUMP_TEXT = Component.translatable(JUMP_TEXT_KEY);
	//# else
	//- private static final Component JUMP_TEXT = new TranslatableComponent(JUMP_TEXT_KEY);
	//# end

	private final Message message;
	private final Button jumpButton;
	//# if MC_VERSION_NUMBER >= 12108
	private final WidgetTooltipHolder tooltipHolder = new WidgetTooltipHolder();
	//# else
	//- private Component text;
	//# end

	/**
	 * Constructs a new message list entry.
	 *
	 * @param message The message to link
	 */
	public MessageListEntry(Message message) {
		this.message = message;
		//# if MC_VERSION_NUMBER >= 11903
		jumpButton = Button.builder(JUMP_TEXT, button -> this.jumpClicked()).size(100, 20).build();
		//# else
		//- jumpButton = new Button(0, 0, 100, 20, JUMP_TEXT, button -> this.jumpClicked());
		//# end
	}

	private void jumpClicked() {
		if (message.getOrigin() instanceof DynamicEntryListWidget.Entry) {
			GuiEventListener last = (GuiEventListener) message.getOrigin();
			EntryContainer category = ((DynamicEntryListWidget.Entry) message.getOrigin()).getParent();
			if (category == null) return;
			while (!(category instanceof ConfigCategoryWidget)) {
				last = category;
				category = category.getParent();
				if (category == null) {
					return;
				}
				category.setFocused(last);
			}

			Screen currentScreen = Minecraft.getInstance().screen;
			ConfigScreen configScreen = null;
			if (currentScreen instanceof MessagesScreen) {
				configScreen = ((MessagesScreen) currentScreen).getParent();
				Minecraft.getInstance().setScreen(configScreen);
			} else if (currentScreen instanceof ConfigScreen) {
				configScreen = (ConfigScreen) currentScreen;
			}

			if (configScreen != null) {
				configScreen.openCategory(((ConfigCategoryWidget) category).getTreeEntry());
				configScreen.setFocused(category);
				ConfigCategoryWidget listWidget = (ConfigCategoryWidget) configScreen.getContentWidget();
				listWidget.setFocused(last);
				//# if MC_VERSION_NUMBER >= 11904
				listWidget.setFocused(true);
				//# end
			}
		}
	}

	//# if MC_VERSION_NUMBER < 12108
	//- /**
	//-  * {@inheritDoc}
	//-  */
	//- @Override
	//- public void widthChanged(int newWidth) {
	//- 	super.widthChanged(newWidth);
	//- 	text = CoatUtil.intelliTrim(
	//- 			Minecraft.getInstance().font, message.getText(),
	//- 			newWidth - CoatUtil.MARGIN - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN
	//- 	);
	//- }
	//# end

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
	public void render(GuiGraphicsExtractor graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	//- public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# else
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		Font font = Minecraft.getInstance().font;
		//# if MC_VERSION_NUMBER >= 12108
		ScreenRectangle nameRect = new ScreenRectangle(
				x + CoatUtil.MARGIN,
				y + 6,
				entryWidth - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN - CoatUtil.MARGIN,
				entryHeight - 12
		);
		CoatUtil.drawLeftAlignedText(graphics, font, message.getText(), nameRect, CoatUtil.TEXT_COLOR);
		//# elif RENDERING == "GUI_GRAPHICS"
		//- graphics.drawString(font, text, x + CoatUtil.MARGIN, y + 6, CoatUtil.TEXT_COLOR.getArgb(), false);
		//# elif RENDERING == "POSE_STACK"
		//- font.draw(graphics, text, x + CoatUtil.MARGIN, y + 6, CoatUtil.TEXT_COLOR.getArgb());
		//# end

		int jumpButtonX = x + entryWidth - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN;
		CoatUtil.setButtonPosition(jumpButton, jumpButtonX, y);

		//# if MC_VERSION_NUMBER >= 260100
		jumpButton.extractRenderState(graphics, mouseX, mouseY, tickDelta);
		//# else
		//- jumpButton.render(graphics, mouseX, mouseY, tickDelta);
		//# end

		//# if MC_VERSION_NUMBER >= 12108
		tooltipHolder.refreshTooltipForNextRenderPass(graphics, mouseX, mouseY, hovered, isFocused(), nameRect);
		//# else
		//- if (hovered && mouseX < jumpButtonX) {
		//- 	//# if RENDERING == "GUI_GRAPHICS"
		//- 	graphics.renderTooltip(font, message.getText(), mouseX, mouseY);
		//- 	//# elif RENDERING == "POSE_STACK"
		//- 	CoatUtil.renderTooltip(graphics, mouseX, mouseY, message.getText());
		//- 	//# end
		//- }
		//# end
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return 20 + CoatUtil.MARGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return Collections.singleton(message);
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
		return Collections.singletonList(jumpButton);
	}
}
