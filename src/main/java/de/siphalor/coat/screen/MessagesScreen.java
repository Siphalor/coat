package de.siphalor.coat.screen;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.entry.MessageListEntry;
import de.siphalor.coat.util.CoatUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A screen that shows the user a list of messages with the option to acknowledging them
 * or to return to the previous screen.
 */
public class MessagesScreen extends Screen {
	@Getter
	private final ConfigScreen parent;
	private final Runnable acceptRunnable;
	private final List<Message> messages;
	private MultiLineLabel titleLines;
	private Button acceptButton;
	private Button abortButton;
	private DynamicEntryListWidget<MessageListEntry> messagesList;

	/**
	 * Creates a new instance.
	 *
	 * @param title          The main message of this screen
	 * @param parent         The screen that the user might return to
	 * @param acceptRunnable A runnable that gets executed when the user acknowledges the messages
	 * @param messages       A list of messages to show
	 */
	public MessagesScreen(Component title, ConfigScreen parent, Runnable acceptRunnable, List<Message> messages) {
		super(title);
		this.parent = parent;
		this.acceptRunnable = acceptRunnable;
		this.messages = messages;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		super.init();

		abortButton = Button.builder(
				Component.translatable(Coat.MOD_ID + ".action.abort"),
				button -> Minecraft.getInstance().setScreen(parent)
		).pos(0, 38).size(100, 20).build();
		acceptButton = Button.builder(
				Component.translatable(Coat.MOD_ID + ".action.accept_risk"),
				button -> acceptRunnable.run()
		).pos(0, 38).size(100, 20).build();
		addRenderableWidget(abortButton);
		addRenderableWidget(acceptButton);

		messagesList = new DynamicEntryListWidget<>(Minecraft.getInstance(), width, height - 62, 62, 260);
		messagesList.addEntries(messages.stream().map(MessageListEntry::new).collect(Collectors.toList()));
		addRenderableWidget(messagesList);

		resize(Minecraft.getInstance(), width, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(Minecraft minecraft, int width, int height) {
		this.width = width;
		this.height = height;

		messagesList.resize(width, height);
		titleLines = MultiLineLabel.create(minecraft.font, title, 260);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# end
		int left = width / 2 - 130;
		//# if !TRANSPARENT_MENUS
		//- renderBackground(graphics);
		//# end
		abortButton.setX(width / 2 - CoatUtil.MARGIN - abortButton.getWidth());
		acceptButton.setX(width / 2 + CoatUtil.MARGIN);

		super.render(graphics, mouseX, mouseY, delta);

		titleLines.renderLeftAligned(graphics, left, CoatUtil.DOUBLE_MARGIN, 10, CoatUtil.TEXT_COLOR.getArgb());
		// messagesList.render(matrices, mouseX, mouseY, delta);
	}
}
