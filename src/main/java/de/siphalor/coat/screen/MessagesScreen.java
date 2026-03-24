package de.siphalor.coat.screen;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.entry.MessageListEntry;
import de.siphalor.coat.util.CoatUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
//- import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A screen that shows the user a list of messages with the option to acknowledging them
 * or to return to the previous screen.
 */
public class MessagesScreen extends Screen {
	private static final String ABORT_TEXT_KEY = Coat.MOD_ID + ".action.abort";
	private static final String ACCEPT_TEXT_KEY = Coat.MOD_ID + ".action.accept_risk";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component ABORT_TEXT = Component.translatable(ABORT_TEXT_KEY);
	private static final Component ACCEPT_TEXT = Component.translatable(ACCEPT_TEXT_KEY);
	//# else
	//- private static final Component ABORT_TEXT = new TranslatableComponent(ABORT_TEXT_KEY);
	//- private static final Component ACCEPT_TEXT = new TranslatableComponent(ACCEPT_TEXT_KEY);
	//# end

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

		//# if MC_VERSION_NUMBER >= 11903
		abortButton = Button.builder(ABORT_TEXT, button -> this.abortClicked())
				.pos(0, 38).size(100, 20).build();
		acceptButton = Button.builder(ACCEPT_TEXT, button -> this.acceptClicked())
				.pos(0, 38).size(100, 20).build();
		//# else
		//- abortButton = new Button(0, 38, 100, 20, ABORT_TEXT, button -> this.abortClicked());
		//- acceptButton = new Button(0, 38, 100, 20, ACCEPT_TEXT, button -> this.acceptClicked());
		//# end

		//# if MC_VERSION_NUMBER >= 11700
		addRenderableWidget(abortButton);
		addRenderableWidget(acceptButton);
		//# else
		//- addButton(abortButton);
		//- addButton(acceptButton);
		//# end

		messagesList = new DynamicEntryListWidget<>(Minecraft.getInstance(), width, height - 62, 62, 260);
		messagesList.addEntries(messages.stream().map(MessageListEntry::new).collect(Collectors.toList()));
		//# if MC_VERSION_NUMBER >= 11700
		addRenderableWidget(messagesList);
		//# else
		//- addWidget(messagesList);
		//# end

		resize(/*# if MC_VERSION_NUMBER < 12111 *//*- Minecraft.getInstance(),  *//*# end */width, height);
	}

	private void abortClicked() {
		Minecraft.getInstance().setScreen(parent);
	}

	private void acceptClicked() {
		acceptRunnable.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(
			/*# if MC_VERSION_NUMBER < 12111 *//*- Minecraft minecraft, *//*# end */
			int width,
			int height
	) {
		this.width = width;
		this.height = height;

		messagesList.resize(width, height);
		titleLines = MultiLineLabel.create(minecraft.font, title, 260);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS_EXTRACTOR"
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	//- public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	//# else
	//- public void render(PoseStack graphics, int mouseX, int mouseY, float delta) {
	//# end
		int left = width / 2 - 130;
		//# if !TRANSPARENT_MENUS
		//- //# if MC_VERSION_NUMBER < 12002
		//- renderBackground(graphics);
		//- //# else
		//- renderBackground(graphics, mouseX, mouseY, delta);
		//- //# end
		//# end

		CoatUtil.setButtonX(abortButton, width / 2 - CoatUtil.MARGIN - abortButton.getWidth());
		CoatUtil.setButtonX(acceptButton, width / 2 + CoatUtil.MARGIN);

		//# if MC_VERSION_NUMBER >= 260100
		super.extractRenderState(graphics, mouseX, mouseY, delta);
		//# else
		//- super.render(graphics, mouseX, mouseY, delta);
		//# end

		//# if MC_VERSION_NUMBER >= 12111
		titleLines.visitLines(TextAlignment.LEFT, left, CoatUtil.DOUBLE_MARGIN, 10, graphics.textRenderer());
		//# elif MC_VERSION_NUMBER >= 12110
		//- titleLines.render(graphics, MultiLineLabel.Align.LEFT, left, CoatUtil.DOUBLE_MARGIN, 10, true, CoatUtil.TEXT_COLOR.getArgb());
		//# else
		//- titleLines.renderLeftAligned(graphics, left, CoatUtil.DOUBLE_MARGIN, 10, CoatUtil.TEXT_COLOR.getArgb());
		//# end

		//# if MC_VERSION_NUMBER < 11700
		//- messagesList.render(graphics, mouseX, mouseY, delta);
		//# end
	}
}
