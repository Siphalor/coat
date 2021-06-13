package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.TextButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ConfigListConfigEntry<V> extends ConfigListCompoundEntry implements InputChangeListener<V> {
	private static final Text DEFAULT_TEXT = new TranslatableText(Coat.MOD_ID + ".default");
	private final TextRenderer textRenderer;
	private final TextButtonWidget nameWidget;
	private final Text description;
	private MultilineText descriptionMultiline;
	private final ConfigEntryHandler<V> entryHandler;
	private final ConfigInput<V> input;
	private final ButtonWidget defaultButton;
	private Collection<Message> messages;
	private boolean expanded;
	protected int x;
	protected int y;

	public ConfigListConfigEntry(BaseText name, BaseText description, ConfigEntryHandler<V> entryHandler, ConfigInput<V> input) {
		super();
		nameWidget = new TextButtonWidget(x, y, 100, 12, name, button -> setExpanded(!isExpanded()));
		setName(name.copy());
		this.description = description;
		this.entryHandler = entryHandler;
		this.input = input;
		input.setChangeListener(this);
		MinecraftClient client = MinecraftClient.getInstance();
		textRenderer = client.textRenderer;
		defaultButton = new ButtonWidget(0, 0, 10, 20, DEFAULT_TEXT, button ->
				input.setValue(entryHandler.getDefault()),
				(button, matrices, mouseX, mouseY) -> {
					List<OrderedText> wrappedLines = CoatUtil.wrapTooltip(textRenderer, client, entryHandler.asText(entryHandler.getDefault()));
					ArrayList<OrderedText> list = new ArrayList<>(wrappedLines.size() + 1);
					list.addAll(wrappedLines);
					list.add(0, new TranslatableText(Coat.MOD_ID + ".default.hover").asOrderedText());
					client.currentScreen.renderOrderedTooltip(matrices, list, mouseX, mouseY);
				}
		);

		inputChanged(input.getValue());
	}

	public boolean isExpanded() {
		return expanded;
	}

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

	protected void updateExpanded(int width) {
		descriptionMultiline = MultilineText.create(MinecraftClient.getInstance().textRenderer, description, width);
	}

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

	protected void setName(BaseText name) {
		Message.Level level = getHighestMessageLevel();
		if (level == null) {
			name.setStyle(Style.EMPTY);
		} else {
			name.setStyle(level.getTextStyle());
		}
		nameWidget.setMessage(name);
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of(nameWidget, input, defaultButton);
	}

	@Override
	public void tick() {
		input.tick();
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;

		int namePart = (int) getNamePart(entryWidth);
		int configEntryPart = (int) getConfigEntryPart(entryWidth);
		int inputHeight = input.getHeight();

		int textY = y + (int)((inputHeight - 8) / 2F) + CoatUtil.MARGIN;

		input.render(matrices, x + namePart + CoatUtil.HALF_MARGIN, y + CoatUtil.MARGIN, configEntryPart - CoatUtil.MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		defaultButton.y = y + CoatUtil.MARGIN;
		defaultButton.x = x + entryWidth - (int) getControlsPart(entryWidth) + CoatUtil.HALF_MARGIN;
		defaultButton.render(matrices, mouseX, mouseY, tickDelta);
		nameWidget.x = x;
		nameWidget.y = textY - 2;
		nameWidget.render(matrices, mouseX, mouseY, tickDelta);

		float curY = y + CoatUtil.MARGIN + Math.max(20F, inputHeight) + CoatUtil.MARGIN;
		float msgX = x + CoatUtil.DOUBLE_MARGIN;
		int msgWidth = entryWidth - CoatUtil.DOUBLE_MARGIN - CoatUtil.DOUBLE_MARGIN;
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

			descriptionMultiline.draw(matrices, x + CoatUtil.DOUBLE_MARGIN, (int) curY, 9, CoatUtil.SECONDARY_TEXT_COLOR);
		}
	}

	public double getNamePart(int width) {
		return width * 0.3;
	}

	public double getConfigEntryPart(int width) {
		return width * 0.5;
	}

	public double getControlsPart(int width) {
		return width * 0.2;
	}

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

	@Override
	public int getHeight() {
		if (isExpanded()) {
			return getBaseHeight() + getExpansionHeight();
		} else {
			return getBaseHeight();
		}
	}

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

	@Override
	public int getEntryWidth() {
		return (int) getConfigEntryPart(parent.getEntryWidth());
	}

	@Override
	public void inputChanged(V newValue) {
		if (!Objects.equals(newValue, entryHandler.getDefault())) {
			defaultButton.active = true;
		} else {
			defaultButton.active = false;
			if (defaultButton.isFocused()) {
				changeFocus(false);
			}
		}
		setMessages(entryHandler.getMessages(newValue));
	}

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

	@Override
	public Collection<Message> getMessages() {
		return messages;
	}

	protected void setMessages(Collection<Message> messages) {
		this.messages = messages;
		for (Message message : messages) {
			message.setOrigin(this);
		}
		if (parent != null) {
			parent.entryHeightChanged(this);
		}
		// shallow copy is required because the OrderedText in BaseText is cached, so the style needs to be force updated
		setName((BaseText) nameWidget.getOriginalMessage().shallowCopy());
	}
}
