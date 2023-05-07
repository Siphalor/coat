package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.CustomTooltip;
import de.siphalor.coat.util.TextButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.*;

/**
 * A config entry with an input, a description and a reset button.
 *
 * @param <V> The value type
 */
public class ConfigCategoryConfigEntry<V> extends ConfigContainerCompoundEntry implements InputChangeListener<V> {
	private static final Text DEFAULT_TEXT = Text.translatable(Coat.MOD_ID + ".default");
	private static final int TEXT_INDENT = 8;
	private final TextRenderer textRenderer;
	private final TextButtonWidget nameWidget;
	private final Text description;
	private MultilineText descriptionMultiline;
	private final ConfigEntryHandler<V> entryHandler;
	private final ConfigInput<V> input;
	private final ButtonWidget defaultButton;
	private Collection<Message> messages;
	private boolean expanded;

	/**
	 * Constructs a new config entry.
	 *
	 * @param name         The name of this entry
	 * @param description  The description text of this entry
	 * @param entryHandler An entry handler for this entry
	 * @param input        The config input to use
	 */
	public ConfigCategoryConfigEntry(MutableText name, MutableText description, ConfigEntryHandler<V> entryHandler, ConfigInput<V> input) {
		super();
		nameWidget = new TextButtonWidget(0, 0, 100, 12, name, button -> setExpanded(!isExpanded()));
		setName(name.copy());
		this.description = description;
		this.entryHandler = entryHandler;
		this.input = input;
		input.setChangeListener(this);
		MinecraftClient client = MinecraftClient.getInstance();
		textRenderer = client.textRenderer;
		defaultButton = ButtonWidget.builder(DEFAULT_TEXT, button ->
				input.setValue(entryHandler.getDefault())
		).size(10, 20).tooltip(
				new CustomTooltip(() -> {
					if (!getDefaultButton().active) {
						return Collections.emptyList();
					}
					List<OrderedText> wrappedLines = CoatUtil.wrapTooltip(textRenderer, client, entryHandler.asText(entryHandler.getDefault()));
					ArrayList<OrderedText> list = new ArrayList<>(wrappedLines.size() + 1);
					list.addAll(wrappedLines);
					list.add(0, Text.translatable(Coat.MOD_ID + ".default.hover").asOrderedText());
					return list;
				}, null)
		).build();

		inputChanged(input.getValue());
	}

	private ButtonWidget getDefaultButton() {
		return defaultButton;
	}

	/**
	 * Gets whether the description and messages of this entry are currently displayed.
	 *
	 * @return Whether it is expanded
	 */
	public boolean isExpanded() {
		return expanded;
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
		descriptionMultiline = MultilineText.create(MinecraftClient.getInstance().textRenderer, description, width - TEXT_INDENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);

		int namePart = (int) getNamePart(newWidth) - CoatUtil.MARGIN;
		nameWidget.setWidth(namePart);

		int controlsPart = (int) getControlsPart(newWidth);
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
	protected void setName(MutableText name) {
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
	public List<? extends Element> children() {
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
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int namePart = (int) getNamePart(entryWidth);
		int configEntryPart = (int) getConfigEntryPart(entryWidth);
		int inputHeight = input.getHeight();

		int textY = y + (int) ((inputHeight - 8) / 2F) + CoatUtil.MARGIN;

		input.render(matrices, x + namePart + CoatUtil.HALF_MARGIN, y + CoatUtil.MARGIN, configEntryPart - CoatUtil.MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		defaultButton.setY(y + CoatUtil.MARGIN);
		defaultButton.setX(x + entryWidth - (int) getControlsPart(entryWidth) + CoatUtil.HALF_MARGIN);
		defaultButton.render(matrices, mouseX, mouseY, tickDelta);
		nameWidget.setPosition(x, textY - 2);
		nameWidget.render(matrices, mouseX, mouseY, tickDelta);

		float curY = y + CoatUtil.MARGIN + Math.max(20F, inputHeight) + CoatUtil.MARGIN;
		float msgX = x + TEXT_INDENT;
		int msgWidth = entryWidth - TEXT_INDENT;
		for (Message message : messages) {
			if (message.getLevel().getSeverity() >= Message.Level.DISPLAY_THRESHOLD) {
				List<OrderedText> lines = textRenderer.wrapLines(message.getText(), msgWidth);
				for (OrderedText line : lines) {
					textRenderer.draw(matrices, line, msgX, curY, 0xffffff);
					curY += 9;
				}
				curY += CoatUtil.MARGIN;
			}
		}

		if (isExpanded()) {
			for (Message message : messages) {
				if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
					List<OrderedText> lines = textRenderer.wrapLines(message.getText(), msgWidth);
					for (OrderedText line : lines) {
						textRenderer.draw(matrices, line, msgX, curY, 0xffffff);
						curY += 9;
					}
					curY += CoatUtil.MARGIN;
				}
			}

			descriptionMultiline.draw(matrices, x + TEXT_INDENT, (int) curY, 9, CoatUtil.SECONDARY_TEXT_COLOR);
		}
	}

	/**
	 * Gets the width used for the name of the config entry.
	 *
	 * @param width The width of the whole entry
	 * @return The partial width to be used for the name
	 */
	public double getNamePart(int width) {
		return width * 0.3;
	}

	/**
	 * Gets the width used for the input of the config entry.
	 *
	 * @param width The width of the whole entry
	 * @return The partial width to be used for the config input
	 */
	public double getConfigEntryPart(int width) {
		return width * 0.5;
	}

	/**
	 * Gets the width used for the controls section of the config entry.
	 *
	 * @param width The width of the whole entry
	 * @return The partial width to be used for the controls section
	 */
	public double getControlsPart(int width) {
		return width * 0.2;
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
				msgHeight += textRenderer.wrapLines(message.getText(), parent.getEntryWidth()).size() * 9 + CoatUtil.MARGIN;
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
		if (descriptionMultiline != MultilineText.EMPTY) {
			height += CoatUtil.MARGIN + descriptionMultiline.count() * 9;
		}
		for (Message message : messages) {
			if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
				height += textRenderer.wrapLines(message.getText(), parent.getEntryWidth()).size() * 9 + CoatUtil.MARGIN;
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
	public void setFocused(Element focused) {
		Element old = getFocused();
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
		return (int) getConfigEntryPart(parent.getEntryWidth());
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
				setFocused(false);
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
		setName(nameWidget.getOriginalMessage().copyContentOnly());
	}
}
