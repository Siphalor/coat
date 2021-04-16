package de.siphalor.coat.list;

import com.google.common.collect.ImmutableList;
import de.siphalor.coat.Coat;
import de.siphalor.coat.input.ConfigInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class ConfigListConfigEntry<V> extends ConfigListCompoundEntry {
	private static final Text DEFAULT_TEXT = new TranslatableText(Coat.MOD_ID + ".default");
	private final TextRenderer textRenderer;
	private final Text name;
	private Text trimmedName;
	private final Text description;
	private final ConfigInput<V> input;
	private final ButtonWidget defaultButton;

	public ConfigListConfigEntry(Text name, Text description, ConfigInput<V> input) {
		super();
		this.name = name;
		this.trimmedName = name;
		this.description = description;
		this.input = input;
		defaultButton = new ButtonWidget(0, 0, 10, 20, DEFAULT_TEXT, button ->
			input.setValue(input.getEntryHandler().getDefault())
		);
		textRenderer = MinecraftClient.getInstance().textRenderer;
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);

		int namePart = (int) getNamePart(newWidth) - Coat.MARGIN;
		if (textRenderer.getWidth(name) > namePart) {
			String rawName = name.getString();
			int length = textRenderer.trimToWidth("..." + rawName, namePart).length() - 3;
			trimmedName = new LiteralText(rawName.substring(0, length).trim() + "...");
		} else {
			trimmedName = name;
		}

		int controlsPart = (int) getControlsPart(newWidth);
		defaultButton.setWidth(controlsPart - Coat.HALF_MARGIN);
	}

	public void inputChanged() {
		defaultButton.active = !input.isDefault();
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
		int namePart = (int) getNamePart(entryWidth);
		int configEntryPart = (int) getConfigEntryPart(entryWidth);

		textRenderer.draw(matrices, trimmedName, x, y + (input.getHeight() - 8F) / 2F + Coat.MARGIN, Coat.TEXT_COLOR);
		input.render(matrices, x + namePart + Coat.HALF_MARGIN, y + Coat.MARGIN, configEntryPart - Coat.MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		defaultButton.y = y + Coat.MARGIN;
		defaultButton.x = x + entryWidth - (int) getControlsPart(entryWidth) + Coat.HALF_MARGIN;
		defaultButton.render(matrices, mouseX, mouseY, tickDelta);

		if (hovered && mouseX - x < namePart && trimmedName != name) {
			MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, name, mouseX, mouseY);
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

	@Override
	public int getHeight() {
		return Math.max(20, input.getHeight());
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
}
