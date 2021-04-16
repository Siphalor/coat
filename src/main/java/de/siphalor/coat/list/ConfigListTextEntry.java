package de.siphalor.coat.list;

import de.siphalor.coat.Coat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigListTextEntry extends ConfigEntryListWidget.Entry {
	private final TextRenderer textRenderer;
	private final Text text;
	private List<OrderedText> multilineText;
	private int height;

	public ConfigListTextEntry(Text text) {
		super();
		this.text = text;
		textRenderer = MinecraftClient.getInstance().textRenderer;
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		multilineText = textRenderer.wrapLines(text, newWidth);
		height = multilineText.size() * 9 + Coat.MARGIN + Coat.MARGIN;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryHeight, int entryWidth, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		for (int i = 0; i < multilineText.size(); i++) {
			textRenderer.draw(matrices, multilineText.get(i), x, y + i * 9, Coat.TEXT_COLOR);
		}
	}

	@Override
	public void tick() {

	}
}
