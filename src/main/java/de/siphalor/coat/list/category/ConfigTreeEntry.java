package de.siphalor.coat.list.category;

import de.siphalor.coat.Coat;
import de.siphalor.coat.ConfigScreen;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import de.siphalor.coat.list.entry.ConfigListSubtreeEntry;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.TextButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigTreeEntry extends ConfigListCompoundEntry {
	private static final BaseText EXPAND_TEXT   = new TranslatableText(Coat.MOD_ID + ".tree.expand");
	private static final BaseText COLLAPSE_TEXT = new TranslatableText(Coat.MOD_ID + ".tree.collapse");

	private final TextButtonWidget collapseButton;
	private final TextButtonWidget nameButton;
	private final List<ConfigTreeEntry> subTrees = new ArrayList<>();
	private DynamicEntryListWidget configWidget;
	private int x;
	private int y;
	private boolean open = false;
	private boolean expanded;
	protected Element focused;

	public ConfigTreeEntry(BaseText name, DynamicEntryListWidget configWidget) {
		this.configWidget = configWidget;
		collapseButton = new TextButtonWidget(x, y, 7, 9, EXPAND_TEXT, button -> setExpanded(!isExpanded()));
		nameButton = new TextButtonWidget(x, y, 100, 9, name, button -> ((ConfigScreen) MinecraftClient.getInstance().currentScreen).openCategory(this));
	}

	public void addSubTree(ConfigTreeEntry entry) {
		entry.setParent(this);
		subTrees.add(entry);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;

		boolean hoverFound = false;
		int indent = x + 8 + CoatUtil.DOUBLE_MARGIN;

		if (!subTrees.isEmpty()) {
			collapseButton.x = x;
			collapseButton.y = y;
			collapseButton.render(matrices, mouseX, mouseY, tickDelta);
		}

		nameButton.x = indent;
		nameButton.y = y;
		nameButton.setWidth(getEntryWidth() - 8 - CoatUtil.MARGIN);
		nameButton.render(matrices, mouseX, mouseY, tickDelta);

		if (expanded) {
			int curY = y + 8 + CoatUtil.MARGIN;
			for (ConfigTreeEntry entry : subTrees) {
				if (!hoverFound && mouseY > curY) {
					hoverFound = true;
					entry.render(matrices, indent, curY, entryHeight, mouseX, mouseY, true, tickDelta);
				} else {
					entry.render(matrices, indent, curY, entryHeight, mouseX, mouseY, false, tickDelta);
				}
				curY += entry.getHeight() + CoatUtil.MARGIN;
			}
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		if (this.open != open) {
			if (open) {
				nameButton.setMessage(
						nameButton.getOriginalMessage().copy().setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC))
				);
			} else {
				nameButton.setMessage(
						nameButton.getOriginalMessage().copy().setStyle(Style.EMPTY)
				);
			}
		}
		this.open = open;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		if (parent != null) {
			parent.entryHeightChanged(this);
		}
		if (expanded) {
			collapseButton.setMessage(COLLAPSE_TEXT);
		} else {
			collapseButton.setMessage(EXPAND_TEXT);
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
		for (ConfigTreeEntry entry : subTrees) {
			height += entry.getHeight() + CoatUtil.MARGIN;
		}
		return height + CoatUtil.MARGIN + 9;
	}

	public int getExpansionHeight() {
		int height = 0;
		for (ConfigTreeEntry child : subTrees) {
			height += child.getHeight();
		}
		if (height > 0) {
			height += CoatUtil.MARGIN;
		}
		return height;
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public List<Element> children() {
		ArrayList<Element> children = new ArrayList<>(subTrees.size() + 2);
		if (!subTrees.isEmpty()) {
			children.add(collapseButton);
		}
		children.add(nameButton);
		children.addAll(subTrees);
		return children;
	}

	@Override
	public void tick() {
		for (ConfigTreeEntry subTree : subTrees) {
			subTree.tick();
		}
	}

	public Text getName() {
		return nameButton.getOriginalMessage();
	}

	public DynamicEntryListWidget getConfigWidget(boolean linkSubtrees) {
		if (!linkSubtrees) {
			return configWidget;
		}

		DynamicEntryListWidget widget = configWidget;
		if (widget == null) {
			widget = new DynamicEntryListWidget(MinecraftClient.getInstance(), 100, 100, 0, 100, getEntryWidth());
		}

		for (int i = 0; i < subTrees.size(); i++) {
			widget.addEntry(i, new ConfigListSubtreeEntry(subTrees.get(i)));
		}
		return widget;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return focused;
	}

	@Override
	public void setFocused(Element focused) {
		this.focused = focused;
	}

	@Override
	public void focusLost() {
		setFocused(null);
	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth() - 8 - CoatUtil.MARGIN;
	}
}
