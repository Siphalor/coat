package de.siphalor.coat.list.entry;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.EntryContainer;
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

public class MessageListEntry extends ConfigListCompoundEntry {
	private static final Text JUMP_TEXT = new TranslatableText(Coat.MOD_ID + ".message.jump");

	private final Message message;
	private Text text;
	private ButtonWidget jumpButton;

	public MessageListEntry(Message message) {
		this.message = message;
		jumpButton = new ButtonWidget(0, 0, 100, 20, JUMP_TEXT, button -> {
			if (message.getOrigin() instanceof DynamicEntryListWidget.Entry) {
				EntryContainer last;
				EntryContainer category = ((DynamicEntryListWidget.Entry) message.getOrigin()).getParent();
				do {
					last = category;
					category = category.getParent();
					if (category == null) {
						return;
					}
				} while (!(category instanceof ConfigListWidget));

				Screen currentScreen = MinecraftClient.getInstance().currentScreen;
				ConfigScreen configScreen = null;
				if (currentScreen instanceof MessagesScreen) {
					configScreen = ((MessagesScreen) currentScreen).getParent();
					MinecraftClient.getInstance().openScreen(configScreen);
				} else if (currentScreen instanceof ConfigScreen) {
					configScreen = (ConfigScreen) currentScreen;
				}

				if (configScreen != null) {
					configScreen.openCategory(((ConfigListWidget) category).getTreeEntry());
					configScreen.getListWidget().focusOn(last);
					configScreen.getListWidget().changeFocus(true);
				}
			}
		});
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		text = CoatUtil.intelliTrim(
				MinecraftClient.getInstance().textRenderer, message.getText(),
				newWidth - CoatUtil.MARGIN - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN
		);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		MinecraftClient.getInstance().textRenderer.draw(matrices, text, x + CoatUtil.MARGIN, y + 6.5F, CoatUtil.TEXT_COLOR);
		jumpButton.y = y;
		jumpButton.x = x + entryWidth - jumpButton.getWidth() - CoatUtil.DOUBLE_MARGIN;
		jumpButton.render(matrices, mouseX, mouseY, tickDelta);

		if (hovered && mouseX < jumpButton.x) {
			CoatUtil.renderTooltip(matrices, mouseX, mouseY, message.getText());
		}
	}

	@Override
	public int getHeight() {
		return 20 + CoatUtil.MARGIN;
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.singleton(message);
	}

	@Override
	public void tick() {

	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth();
	}

	@Override
	public List<? extends Element> children() {
		return Collections.singletonList(jumpButton);
	}
}
