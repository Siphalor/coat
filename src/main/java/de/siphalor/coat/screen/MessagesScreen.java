package de.siphalor.coat.screen;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.entry.MessageListEntry;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A screen that shows the user a list of messages with the option to acknowledging them
 * or to return to the previous screen.
 */
public class MessagesScreen extends Screen {
	private final ConfigScreen parent;
	private final Runnable acceptRunnable;
	private final List<Message> messages;
	private MultilineText titleLines;
	private ButtonWidget acceptButton;
	private ButtonWidget abortButton;
	private DynamicEntryListWidget<MessageListEntry> messagesList;

	/**
	 * Creates a new instance.
	 *
	 * @param title          The main message of this screen
	 * @param parent         The screen that the user might return to
	 * @param acceptRunnable A runnable that gets executed when the user acknowledges the messages
	 * @param messages       A list of messages to show
	 */
	public MessagesScreen(Text title, ConfigScreen parent, Runnable acceptRunnable, List<Message> messages) {
		super(title);
		this.parent = parent;
		this.acceptRunnable = acceptRunnable;
		this.messages = messages;
	}

	/**
	 * Get the screen to return to.
	 *
	 * @return The parent screen
	 */
	public ConfigScreen getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		super.init();

		abortButton = ButtonWidget.builder(
				Text.translatable(Coat.MOD_ID + ".action.abort"),
				button -> MinecraftClient.getInstance().setScreen(parent)
		).position(0, 38).size(100, 20).build();
		acceptButton = ButtonWidget.builder(
				Text.translatable(Coat.MOD_ID + ".action.accept_risk"),
				button -> acceptRunnable.run()
		).position(0, 38).size(100, 20).build();
		addDrawableChild(abortButton);
		addDrawableChild(acceptButton);

		messagesList = new DynamicEntryListWidget(MinecraftClient.getInstance(), width, height - 62, 62, 260);
		messagesList.addEntries(messages.stream().map(MessageListEntry::new).collect(Collectors.toList()));
		addDrawableChild(messagesList);

		resize(MinecraftClient.getInstance(), width, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;

		messagesList.resize(width, height);
		titleLines = MultilineText.create(client.textRenderer, title, 260);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
		int left = width / 2 - 130;
		renderBackground(drawContext);
		abortButton.setX(width / 2 - CoatUtil.MARGIN - abortButton.getWidth());
		acceptButton.setX(width / 2 + CoatUtil.MARGIN);
		titleLines.draw(drawContext, left, CoatUtil.DOUBLE_MARGIN, 10, CoatUtil.TEXT_COLOR);
		// messagesList.render(matrices, mouseX, mouseY, delta);

		super.render(drawContext, mouseX, mouseY, delta);
	}
}
