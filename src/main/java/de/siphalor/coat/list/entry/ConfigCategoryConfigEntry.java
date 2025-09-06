package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.CustomTooltip;
import de.siphalor.coat.util.TextButtonWidget;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
//- import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A config entry with an input, a description and a reset button.
 *
 * @param <V> The value type
 */
public class ConfigCategoryConfigEntry<V> extends ConfigContainerCompoundEntry implements InputChangeListener<V> {
	private static final String DEFAULT_TEXT_KEY = Coat.MOD_ID + ".default";
	private static final String DEFAULT_HOVER_TEXT_KEY = Coat.MOD_ID + ".default.hover";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component DEFAULT_TEXT = Component.translatable(DEFAULT_TEXT_KEY);
	private static final Component DEFAULT_HOVER_TEXT = Component.translatable(DEFAULT_HOVER_TEXT_KEY);
	//# else
	//- private static final Component DEFAULT_TEXT = new TranslatableComponent(DEFAULT_TEXT_KEY);
	//- private static final Component DEFAULT_HOVER_TEXT = new TranslatableComponent(DEFAULT_HOVER_TEXT_KEY);
	//# end
	private static final int TEXT_INDENT = 8;
	private final Font font;
	private final TextButtonWidget nameWidget;
	private final Component description;
	private MultiLineLabel descriptionMultiline;
	private final ConfigEntryHandler<V> entryHandler;
	private final ConfigInput<V> input;
	@Getter(AccessLevel.PRIVATE)
	private final Button defaultButton;
	private Collection<Message> messages;
	/**
	 * Whether the description and messages of this entry are currently displayed.
	 */
	@Getter
	private boolean expanded;
	private boolean hovered;
	private int leftInputOffset;
	private int inputWidth;

	/**
	 * Constructs a new config entry.
	 *
	 * @param name         The name of this entry
	 * @param description  The description text of this entry
	 * @param entryHandler An entry handler for this entry
	 * @param input        The config input to use
	 */
	public ConfigCategoryConfigEntry(MutableComponent name, MutableComponent description, ConfigEntryHandler<V> entryHandler, ConfigInput<V> input) {
		super();
		nameWidget = new TextButtonWidget(0, 0, 100, 12, name, button -> setExpanded(!isExpanded()));
		nameWidget.setHoverEffect(false);
		setName(name.copy());
		this.description = description;
		this.entryHandler = entryHandler;
		this.input = input;
		input.setChangeListener(this);
		Minecraft minecraft = Minecraft.getInstance();
		font = minecraft.font;
		//# if MC_VERSION_NUMBER >= 11903
		defaultButton = Button.builder(DEFAULT_TEXT, button ->
				input.setValue(entryHandler.getDefault())
		).size(10, 20).tooltip(
				new CustomTooltip(() -> {
					if (!getDefaultButton().active) {
						return Collections.emptyList();
					}
					List<FormattedCharSequence> wrappedLines = CoatUtil.wrapTooltip(font, minecraft, entryHandler.asText(entryHandler.getDefault()));
					ArrayList<FormattedCharSequence> list = new ArrayList<>(wrappedLines.size() + 1);
					list.addAll(wrappedLines);
					list.add(0, Component.translatable(Coat.MOD_ID + ".default.hover").getVisualOrderText());
					return list;
				}, null)
		).build();
		//# else
		//- defaultButton = new Button(
		//- 		0, 0, 10, 20, DEFAULT_TEXT, button -> input.setValue(entryHandler.getDefault()),
		//- 		(button, context, mouseX, mouseY) -> {
		//- 			if (button.active) {
		//- 				List<FormattedCharSequence> wrappedLines = CoatUtil.wrapTooltip(
		//- 						font,
		//- 						minecraft,
		//- 						entryHandler.asText(entryHandler.getDefault())
		//- 				);
		//- 				List<FormattedCharSequence> all = new ArrayList<>(wrappedLines.size() + 1);
		//- 				all.add(DEFAULT_HOVER_TEXT.getVisualOrderText());
		//- 				all.addAll(wrappedLines);
		//- 				minecraft.screen.renderTooltip(context, all, mouseX, mouseY);
		//- 			}
		//- 		}
		//- );
		//# end

		inputChanged(input.getValue());
	}

	/**
	 * Gets whether there is any description or messages to display.
	 *
	 * @return Whether the expansion is empty
	 */
	public boolean isExpansionEmpty() {
		if (description != null) {
			return false;
		}
		for (Message message : messages) {
			if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets whether the additional is visible.
	 *
	 * @param expanded Whether the entry should be expanded
	 */
	public void setExpanded(boolean expanded) {
		if (expanded) {
			updateExpanded(parent.getEntryWidth());
		}
		boolean old = this.expanded;
		this.expanded = expanded;
		if (old != expanded) {
			parent.entryHeightChanged(this);
		}
	}

	/**
	 * Called when this entry is collapsed or gets expanded.
	 *
	 * @param width The new width of this entry
	 */
	protected void updateExpanded(int width) {
		descriptionMultiline = MultiLineLabel.create(Minecraft.getInstance().font, description, width - TEXT_INDENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);

		int inputWidth = input.getPreferredWidth(); // actual width without gaps
		// these parts include the gaps
		int namePart = (int) (newWidth * 0.3);
		int controlsPart = (int) (newWidth * 0.2);
		if (inputWidth > 0) {
			int rest = newWidth - namePart - inputWidth - CoatUtil.MARGIN - controlsPart;
			if (rest > 0) {
				namePart += rest;
			} else {
				inputWidth += rest;
			}
		} else { // auto input width
			inputWidth = newWidth - namePart - CoatUtil.MARGIN - controlsPart;
		}

		nameWidget.setWidth(namePart - CoatUtil.HALF_MARGIN);
		this.inputWidth = inputWidth;
		this.leftInputOffset = namePart + CoatUtil.HALF_MARGIN;
		defaultButton.setWidth(controlsPart - CoatUtil.HALF_MARGIN);

		if (isExpanded()) {
			updateExpanded(newWidth);
		}
	}

	/**
	 * Updates the name of the entry and uses the appropriate text style.
	 *
	 * @param name The new name
	 */
	protected void setName(MutableComponent name) {
		Message.Level level = getHighestMessageLevel();
		if (level == null) {
			name.setStyle(Style.EMPTY);
		} else {
			name.setStyle(level.getTextStyle());
		}
		nameWidget.setMessage(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends GuiEventListener> children() {
		return ImmutableList.of(nameWidget, input, defaultButton);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {
		input.tickConfigInput();
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
		int inputHeight = input.getHeight();
		int messageHeight = Math.max(20, inputHeight);
		int top = y + CoatUtil.MARGIN;
		int right = x + entryWidth;
		int bottom = y + entryHeight;

		this.hovered = hovered;
		if (hovered) {
			//# if RENDERING == "GUI_GRAPHICS"
			graphics.fill(x, top, right, bottom, CoatUtil.HOVER_BG_COLOR.getArgb());
			//# elif RENDERING == "POSE_STACK"
			//- fill(graphics, x, top, right, bottom, CoatUtil.HOVER_BG_COLOR.getArgb());
			//# end
		}

		int textY = top + (int) ((messageHeight - 8) / 2F);

		input.render(graphics, x + leftInputOffset, top + (messageHeight - inputHeight) / 2, inputWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

		CoatUtil.setButtonPosition(defaultButton, x + entryWidth - defaultButton.getWidth() + CoatUtil.HALF_MARGIN, top);
		CoatUtil.setButtonPosition(nameWidget, x, textY - 2);

		defaultButton.render(graphics, mouseX, mouseY, tickDelta);
		nameWidget.render(graphics, mouseX, mouseY, tickDelta);

		int curY = top + messageHeight + CoatUtil.MARGIN;
		int msgX = x + TEXT_INDENT;
		int msgWidth = entryWidth - TEXT_INDENT;
		for (Message message : messages) {
			if (message.getLevel().getSeverity() >= Message.Level.DISPLAY_THRESHOLD) {
				List<FormattedCharSequence> lines = font.split(message.getText(), msgWidth);
				for (FormattedCharSequence line : lines) {
					//# if RENDERING == "GUI_GRAPHICS"
					graphics.drawString(font, line, msgX, curY, 0xffffff, false);
					//# else
					//- font.draw(graphics, line, msgX, curY, 0xffffff);
					//# end
					curY += 9;
				}
				curY += CoatUtil.MARGIN;
			}
		}

		if (isExpanded()) {
			for (Message message : messages) {
				if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
					List<FormattedCharSequence> lines = font.split(message.getText(), msgWidth);
					for (FormattedCharSequence line : lines) {
						//# if RENDERING == "GUI_GRAPHICS"
						graphics.drawString(font, line, msgX, curY, 0xffffff, false);
						//# else
						//- font.draw(graphics, line, msgX, curY, 0xffffff);
						//# end
						curY += 9;
					}
					curY += CoatUtil.MARGIN;
				}
			}

			descriptionMultiline.renderLeftAlignedNoShadow(graphics, x + TEXT_INDENT, curY, 9, CoatUtil.SECONDARY_TEXT_COLOR.getArgb());
		}
	}

	/**
	 * Gets the base height for this entry.
	 *
	 * @return The height of the collapsed entry
	 */
	public int getBaseHeight() {
		int msgHeight = 0;
		for (Message message : messages) {
			if (message.getLevel().getSeverity() >= Message.Level.DISPLAY_THRESHOLD) {
				msgHeight += font.split(message.getText(), parent.getEntryWidth()).size() * 9 + CoatUtil.MARGIN;
			}
		}
		if (msgHeight > 0) {
			msgHeight += CoatUtil.MARGIN;
		}
		return CoatUtil.MARGIN + Math.max(20, input.getHeight()) + msgHeight;
	}

	/**
	 * Gets the height of the expansion.
	 *
	 * @return The height of the expansion
	 */
	public int getExpansionHeight() {
		int height = 0;
		if (descriptionMultiline != MultiLineLabel.EMPTY) {
			height += CoatUtil.MARGIN + descriptionMultiline.getLineCount() * 9;
		}
		for (Message message : messages) {
			if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
				height += font.split(message.getText(), parent.getEntryWidth()).size() * 9 + CoatUtil.MARGIN;
			}
		}
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		if (isExpanded()) {
			return getBaseHeight() + getExpansionHeight();
		} else {
			return getBaseHeight();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocused(GuiEventListener focused) {
		GuiEventListener old = getFocused();
		if (old != focused) {
			if (old == input) {
				input.setFocused(false);
			}
			super.setFocused(focused);
			if (focused == input) {
				input.setFocused(true);
			}
		}
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
	public void inputChanged(V newValue) {
		if (!Objects.equals(newValue, entryHandler.getDefault())) {
			defaultButton.active = true;
		} else {
			if (defaultButton.isFocused()) {
				//# if MC_VERSION_NUMBER >= 11904
				setFocused(false);
				//# else
				//- setFocused(null);
				//# end
			}
			defaultButton.active = false;
		}
		setMessages(entryHandler.getMessages(newValue));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save() {
		super.save();
		entryHandler.save(input.getValue());
	}

	/**
	 * Gets the highest severity level of the currently defined messages on this entry.
	 *
	 * @return The message level
	 */
	public Message.Level getHighestMessageLevel() {
		if (messages == null) {
			return null;
		}

		Message.Level highestLevel = null;
		int highestSeverity = Integer.MIN_VALUE, severity;
		for (Message message : messages) {
			severity = message.getLevel().getSeverity();
			if (severity > highestSeverity) {
				highestSeverity = severity;
				highestLevel = message.getLevel();
			}
		}
		return highestLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return messages;
	}

	/**
	 * Sets the current messages to be displayed for this entry.
	 *
	 * @param messages The messages to be displayed
	 */
	protected void setMessages(Collection<Message> messages) {
		this.messages = messages;
		for (Message message : messages) {
			message.setOrigin(this);
		}
		if (parent != null) {
			parent.entryHeightChanged(this);
		}
		// shallow copy is required because the OrderedText in MutableText is cached, so the style needs to be force updated
		setName(nameWidget.getOriginalMessage().plainCopy());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!super.mouseClicked(mouseX, mouseY, button)) {
			if (hovered && !isExpansionEmpty()) {
				CoatUtil.playClickSound();
				setExpanded(!isExpanded());
				return true;
			}
			return false;
		}
		return true;
	}
}
