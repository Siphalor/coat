package de.siphalor.coat.list.category;

import de.siphalor.coat.Coat;
import de.siphalor.coat.ConfigScreen;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigEntryListWidget;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import de.siphalor.coat.list.ConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigTreeEntry extends ConfigListCompoundEntry {
	private static final BaseText EXPAND_PREFIX = new TranslatableText(Coat.MOD_ID + ".tree.expand");
	private static final BaseText COLLAPSE_PREFIX = new TranslatableText(Coat.MOD_ID + ".tree.collapse");

	private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
	private final BaseText name;
	private final List<ConfigTreeEntry> entries = new ArrayList<>();
	private final List<ConfigListEntry> configListEntries;
	private int x;
	private int y;
	private boolean expanded;
	protected ConfigTreeEntry focused;

	public ConfigTreeEntry(BaseText name, List<ConfigListEntry> configListEntries) {
		this.name = name;
		this.configListEntries = configListEntries;
	}

	public void addSubTree(ConfigTreeEntry entry) {
		entry.setParent(this);
		entries.add(entry);
	}

	private BaseText getExpansionPrefix() {
		return isExpanded() ? COLLAPSE_PREFIX : EXPAND_PREFIX;
	}

	private int getPrefixRight() {
		return x + textRenderer.getWidth(getExpansionPrefix()) + Coat.DOUBLE_MARGIN - Coat.HALF_MARGIN;
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;

		boolean hoverFound = false;
		BaseText expansionPrefix = getExpansionPrefix();
		int prefixRight = getPrefixRight();

		if (focused == this) {
			fill(matrices, x, y - Coat.MARGIN, x + parent.getEntryWidth(), y + 8 + Coat.MARGIN, 0x33ffffff);
		}

		if (hovered && mouseY < y + 8) {
			hoverFound = true;
			if (mouseX < prefixRight) {
				fill(matrices, x, y, prefixRight, y + 8, 0x33ffffff);
			} else {
				fill(matrices, prefixRight, y, x + parent.getEntryWidth() + Coat.HALF_MARGIN, y + 8, 0x33ffffff);
			}
		}

		textRenderer.draw(matrices, expansionPrefix, x + Coat.MARGIN, y, 0xffffff);
		textRenderer.drawTrimmed(name, prefixRight + Coat.HALF_MARGIN, y, parent.getEntryWidth() - Coat.MARGIN, 0xffffff);

		if (expanded) {
			int curY = y + 8 + Coat.MARGIN;
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
			if (mouseX < getPrefixRight()) {
				setExpanded(!isExpanded());
			} else {
				openCategory();
			}
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
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		switch (keyCode) {
			case 262: // right arrow
			case 32:  // space
				setExpanded(!expanded);
				break;
			case 257:  // enter
				openCategory();
				break;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void openCategory() {
		if (configListEntries != null) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen instanceof ConfigScreen) {
				((ConfigScreen) screen).getListWidget().replaceEntries(configListEntries);
			}
		}
	}

	@Nullable
	@Override
	public ConfigTreeEntry getFocused() {
		if (focused == this) {
			return null;
		}
		return focused;
	}

	public void setFocused(ConfigTreeEntry focused) {
		if (this.getFocused() != null) {
			this.focused.focusLost();
		}
		this.focused = focused;
	}

	@Override
	public void focusLost() {
		setFocused(null);
	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth() - Coat.MARGIN;
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		int index;
		if (focused == this) {
			index = 0;
		} else {
			index = entries.indexOf(focused);
		}
		if (lookForwards) {
			if (index < 0) {
				setFocused(this);
				return true;
			}

			for (int i = index; i < entries.size(); i++) {
				if (entries.get(i).changeFocus(true)) {
					setFocused(entries.get(i));
					return true;
				}
			}

			focusLost();
			return false;
		} else {
			if (!entries.isEmpty()) {
				for (int i = index; i >= 0; i--) {
					if (entries.get(i).changeFocus(false)) {
						setFocused(entries.get(i));
						return true;
					}
				}
			}

			setFocused(this);
			return true;
		}
	}
}
