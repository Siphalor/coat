package de.siphalor.coat.list.entry;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.screen.MessagesScreen;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list entry linking to a message and providing functionality to jump to it.
 */
public class MessageListEntry extends ConfigContainerCompoundEntry {
	private static final Text JUMP_TEXT = new TranslatableText(Coat.MOD_ID + ".message.jump");

	private final Message message;
	private Text text;
	private ButtonWidget jumpButton;

	/**
	 * Constructs a new message list entry.
	 *
	 * @param message The message to link
	 */
	public MessageListEntry(Message message) {
		this.message = message;
		jumpButton = new ButtonWidget(0, 0, 100, 20, JUMP_TEXT, button -> {
			if (message.getOrigin() instanceof DynamicEntryListWidget.Entry) {
				Element last = (Element) message.getOrigin();
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

				Screen currentScreen = MinecraftClient.getInstance().currentScreen;
				ConfigScreen configScreen = null;
				if (currentScreen instanceof MessagesScreen) {
					configScreen = ((MessagesScreen) currentScreen).getParent();
					MinecraftClient.getInstance().setScreen(configScreen);
				} else if (currentScreen instanceof ConfigScreen) {
					configScreen = (ConfigScreen) currentScreen;
				}

				if (configScreen != null) {
					configScreen.openCategory(((ConfigCategoryWidget) category).getTreeEntry());
					configScreen.setFocused(category);
					ConfigCategoryWidget listWidget = (ConfigCategoryWidget) configScreen.getContentWidget();
					listWidget.focusOn(last);
					listWidget.changeFocus(true);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		text = CoatUtil.intelliTrim(
				MinecraftClient.getInstance().textRenderer, message.getText(),
				newWidth - CoatUtil.MARGIN - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		MinecraftClient.getInstance().textRenderer.draw(matrices, text, x + CoatUtil.MARGIN, y + 6.5F, CoatUtil.TEXT_COLOR.getArgb());
		jumpButton.y = y;
		jumpButton.x = x + entryWidth - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN;
		jumpButton.render(matrices, mouseX, mouseY, tickDelta);

		if (hovered && mouseX < jumpButton.x) {
			CoatUtil.renderTooltip(matrices, mouseX, mouseY, message.getText());
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
	public List<? extends Element> children() {
		return Collections.singletonList(jumpButton);
	}
}
