package de.siphalor.coat.list.entry;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A textual entry for config list widgets.
 */
public class ConfigListTextEntry extends ConfigContainerEntry {
	private final TextRenderer textRenderer;
	private final Text text;
	private List<String> multilineText;
	private int height;

	public ConfigListTextEntry(Text text) {
		super();
		this.text = text;
		textRenderer = MinecraftClient.getInstance().textRenderer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		multilineText = textRenderer.wrapStringToWidthAsList(text.asFormattedString(), newWidth);
		height = multilineText.size() * 9 + CoatUtil.MARGIN + CoatUtil.MARGIN;
		parent.entryHeightChanged(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		for (int i = 0; i < multilineText.size(); i++) {
			textRenderer.draw(multilineText.get(i), x, y + i * 9, CoatUtil.TEXT_COLOR);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}
}
