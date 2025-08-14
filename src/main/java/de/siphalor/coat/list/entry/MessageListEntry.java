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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list entry linking to a message and providing functionality to jump to it.
 */
public class MessageListEntry extends ConfigContainerCompoundEntry {
	private static final Component JUMP_TEXT = Component.translatable(Coat.MOD_ID + ".message.jump");

	private final Message message;
	private Component text;
	private Button jumpButton;

	/**
	 * Constructs a new message list entry.
	 *
	 * @param message The message to link
	 */
	public MessageListEntry(Message message) {
		this.message = message;
		jumpButton = Button.builder(JUMP_TEXT, button -> {
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
					listWidget.setFocused(true);
				}
			}
		}).size(100, 20).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		text = CoatUtil.intelliTrim(
				Minecraft.getInstance().font, message.getText(),
				newWidth - CoatUtil.MARGIN - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		Font font = Minecraft.getInstance().font;
		//# if RENDERING == "POSE_STACK"
		//- font.draw(graphics, text, x + CoatUtil.MARGIN, y + 6, CoatUtil.TEXT_COLOR.getArgb());
		//# elif RENDERING == "GUI_GRAPHICS"
		graphics.drawString(font, text, x + CoatUtil.MARGIN, y + 6, CoatUtil.TEXT_COLOR.getArgb(), false);
		//# end
		jumpButton.setY(y);
		jumpButton.setX(x + entryWidth - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN);
		jumpButton.render(graphics, mouseX, mouseY, tickDelta);

		if (hovered && mouseX < jumpButton.getX()) {
			//# if RENDERING == "POSE_STACK"
			//- CoatUtil.renderTooltip(graphics, mouseX, mouseY, message.getText());
			//# elif RENDERING == "GUI_GRAPHICS"
			graphics.renderTooltip(font, message.getText(), mouseX, mouseY);
			//# end
		}
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
