package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.input.InputChangeListener;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
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
	private final BaseText name;
	private BaseText trimmedName;
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
		this.name = name;
		setTrimmedName(name.copy());
		this.description = description;
		this.entryHandler = entryHandler;
		this.input = input;
		input.setChangeListener(this);
		MinecraftClient client = MinecraftClient.getInstance();
		textRenderer = client.textRenderer;
		defaultButton = new ButtonWidget(0, 0, 10, 20, DEFAULT_TEXT, button ->
				input.setValue(entryHandler.getDefault()),
				(button, matrices, mouseX, mouseY) -> {
					List<OrderedText> wrappedLines = Coat.wrapTooltip(textRenderer, client, entryHandler.asText(entryHandler.getDefault()));
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
			updateExpanded(parentList.getEntryWidth());
		}
		boolean old = this.expanded;
		this.expanded = expanded;
		if (old != expanded) {
			parentList.entryHeightChanged(this);
		}
	}

	protected void updateExpanded(int width) {
		descriptionMultiline = MultilineText.create(MinecraftClient.getInstance().textRenderer, description, width);
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);

		int namePart = (int) getNamePart(newWidth) - Coat.MARGIN;
		if (textRenderer.getWidth(name) > namePart) {
			String rawName = name.getString();
			int length = textRenderer.trimToWidth("..." + rawName, namePart).length() - 3;
			setTrimmedName(new LiteralText(rawName.substring(0, length).trim() + "..."));
		} else {
			setTrimmedName(name.copy());
		}

		int controlsPart = (int) getControlsPart(newWidth);
		defaultButton.setWidth(controlsPart - Coat.HALF_MARGIN);

		if (isExpanded()) {
			updateExpanded(newWidth);
		}
	}

	protected void setTrimmedName(BaseText trimmedName) {
		this.trimmedName = trimmedName;
		Message.Level level = getHighestMessageLevel();
		if (level == null) {
			trimmedName.setStyle(Style.EMPTY);
		} else {
			trimmedName.setStyle(level.getTextStyle());
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		}
		if (mouseX < x + getNamePart(parentList.getEntryWidth()) && mouseY < y + input.getHeight()) {
			Coat.playClickSound();
			setExpanded(!isExpanded());
			return true;
		}
		return false;
	}

	@Override
	public List<? extends Element> children() {
		return ImmutableList.of(input, defaultButton);
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

		float textY = y + (inputHeight - 8F) / 2F + Coat.MARGIN;

		textRenderer.draw(matrices, trimmedName, x, textY, 0xffffff);
		input.render(matrices, x + namePart + Coat.HALF_MARGIN, y + Coat.MARGIN, configEntryPart - Coat.MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		defaultButton.y = y + Coat.MARGIN;
		defaultButton.x = x + entryWidth - (int) getControlsPart(entryWidth) + Coat.HALF_MARGIN;
		defaultButton.render(matrices, mouseX, mouseY, tickDelta);

		float curY = y + Coat.MARGIN + Math.max(20F, inputHeight) + Coat.MARGIN;
		float msgX = x + Coat.DOUBLE_MARGIN;
		int msgWidth = entryWidth - Coat.DOUBLE_MARGIN - Coat.DOUBLE_MARGIN;
		for (Message message : messages) {
			if (message.getLevel().getSeverity() >= Message.Level.DISPLAY_THRESHOLD) {
				List<OrderedText> lines = textRenderer.wrapLines(message.getText(), msgWidth);
				for (OrderedText line : lines) {
					textRenderer.draw(matrices, line, msgX, curY, 0xffffff);
					curY += 9;
				}
				curY += Coat.MARGIN;
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
					curY += Coat.MARGIN;
				}
			}

			curY += Coat.MARGIN;
			descriptionMultiline.draw(matrices, x + Coat.DOUBLE_MARGIN, (int) curY, 9, Coat.SECONDARY_TEXT_COLOR);
		}

		if (hovered && mouseX - x < namePart && mouseY < y + inputHeight) {
			fill(matrices, x - Coat.DOUBLE_MARGIN, y + Coat.MARGIN, x + namePart, y + inputHeight, 0x33ffffff);
			if (!trimmedName.equals(name)) {
				Coat.renderTooltip(matrices, mouseX, mouseY, name);
			}
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
				msgHeight += textRenderer.wrapLines(message.getText(), parentList.getEntryWidth()).size() * 9 + Coat.MARGIN;
			}
		}
		if (msgHeight > 0) {
			msgHeight += Coat.MARGIN;
		}
		return Coat.MARGIN + Math.max(20, input.getHeight()) + msgHeight;
	}

	public int getExpansionHeight() {
		int height = 0;
		if (descriptionMultiline != MultilineText.EMPTY) {
			height += Coat.MARGIN + descriptionMultiline.count() * 9;
		}
		for (Message message : messages) {
			if (message.getLevel().getSeverity() < Message.Level.DISPLAY_THRESHOLD) {
				height += textRenderer.wrapLines(message.getText(), parentList.getEntryWidth()).size() * 9 + Coat.MARGIN;
			}
		}
		if (height > 0) {
			height += Coat.MARGIN;
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
	public void inputChanged(V newValue) {
		defaultButton.active = !Objects.equals(newValue, entryHandler.getDefault());
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
		if (parentList != null) {
			parentList.entryHeightChanged(this);
		}
		// shallow copy is required because the OrderedText in BaseText is cached, so the style needs to be force updated
		setTrimmedName((BaseText) trimmedName.shallowCopy());
	}
}
