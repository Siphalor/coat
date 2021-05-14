package de.siphalor.coat.list.category;

import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigTreeEntry extends ConfigListCompoundEntry {
	private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	private final Text name;
	private final List<ConfigTreeEntry> entries = new ArrayList<>();
	private int focusIndex = -1;
	private int y;
	private boolean expanded;

	public ConfigTreeEntry(Text name) {
		this.name = name;
	}

	public void addSubTree(ConfigTreeEntry entry) {
		entry.setParent(this);
		entries.add(entry);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.y = y;
		textRenderer.drawTrimmed(name, x, y, parent.getEntryWidth() - Coat.MARGIN, 0xffffff);

		if (expanded) {
			int curY = y + 8 + Coat.MARGIN;
			boolean hoverFound = false;
			for (ConfigTreeEntry entry : children()) {
				if (!hoverFound && mouseY > curY) {
					hoverFound = true;
					entry.render(matrices, x + Coat.DOUBLE_MARGIN, curY, entryHeight, mouseX, mouseY, true, tickDelta);
				} else {
					entry.render(matrices, x + Coat.DOUBLE_MARGIN, curY, entryHeight, mouseX, mouseY, false, tickDelta);
				}
				curY += entry.getHeight() + Coat.MARGIN;
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseY < y + 8 && mouseY > y) {
			setExpanded(!isExpanded());
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (parent != null) {
			parent.entryHeightChanged(this);
		}
	}

	public boolean isExpanded() {
		return expanded;
	}

	@Override
	public int getHeight() {
		if (expanded) {
			return getBaseHeight() + getExpansionHeight();
		} else {
			return getBaseHeight();
		}
	}

	public int getBaseHeight() {
		int height = 0;
		for (ConfigTreeEntry entry : entries) {
			height += entry.getHeight() + Coat.MARGIN;
		}
		return height + Coat.MARGIN + 8;
	}

	public int getExpansionHeight() {
		int height = 0;
		for (ConfigTreeEntry child : children()) {
			height += child.getHeight();
		}
		if (height > 0) {
			height += Coat.MARGIN;
		}
		return height;
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public List<ConfigTreeEntry> children() {
		return entries;
	}

	@Override
	public void tick() {

	}

	@Override
	public void focusLost() {
		super.focusLost();
	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth() - Coat.MARGIN;
	}
}
