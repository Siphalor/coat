package de.siphalor.coat.list.entry;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListEntry;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collection;
import java.util.Collections;

public class ConfigListHorizontalBreak extends ConfigListEntry {
	private static final int PADDING = 10;

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		fill(matrices, x, y + PADDING, x + entryWidth, y + PADDING + 1, CoatUtil.SECONDARY_TEXT_COLOR);
	}

	@Override
	public int getHeight() {
		return PADDING + 1 + PADDING;
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public void tick() {

	}
}
