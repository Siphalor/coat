package de.siphalor.coat.list.category;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.entry.ConfigContainerCompoundEntry;
import de.siphalor.coat.screen.ConfigContentWidget;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.TextButtonWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An entry in the tree pane which represents a config category/{@link ConfigCategoryWidget}.
 *
 * @see ConfigCategoryWidget
 */
public class ConfigTreeEntry extends ConfigContainerCompoundEntry {
	private static final Component EXPAND_TEXT = Component.translatable(Coat.MOD_ID + ".tree.expand");
	private static final Component COLLAPSE_TEXT = Component.translatable(Coat.MOD_ID + ".tree.collapse");

	private final TextButtonWidget collapseButton;
	private final TextButtonWidget nameButton;
	private final List<ConfigTreeEntry> subTrees;
	private final ConfigContentWidget contentWidget;
	@Getter
	private final boolean temporary;
	private int x;
	private int y;
	/**
	 *  Whether this config category is currently opened.
	 */
	@Getter
	private boolean open = false;
	/**
	 * Whether the subtrees are visible.
	 */
	@Getter
	private boolean expanded;

	/**
	 * The currently focused element.
	 */
	@Getter
	@Setter
	protected @Nullable GuiEventListener focused;

	public ConfigTreeEntry(Component name, ConfigContentWidget contentWidget) {
		this(name, contentWidget, false);
	}

	public ConfigTreeEntry(Component name, ConfigContentWidget contentWidget, boolean temporary) {
		this.contentWidget = contentWidget;
		this.temporary = temporary;
		collapseButton = new TextButtonWidget(x, y, 7, 9, EXPAND_TEXT, button -> setExpanded(!isExpanded()));
		nameButton = new TextButtonWidget(x, y, 100, 9, name, button -> ((ConfigScreen) Minecraft.getInstance().screen).openCategory(this));

		List<ConfigTreeEntry> list = new ArrayList<>();
		if (contentWidget instanceof ConfigCategoryWidget) {
			for (ConfigCategoryWidget configCategoryWidget : ((ConfigCategoryWidget) contentWidget).getSubTrees()) {
				ConfigTreeEntry treeEntry = configCategoryWidget.getTreeEntry();
				treeEntry.setParent(this);
				list.add(treeEntry);
			}
		}
		subTrees = list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		this.x = x;
		this.y = y;

		boolean hoverFound = false;
		int indent = x + 6 + CoatUtil.DOUBLE_MARGIN;
		int innerWidth = getEntryWidth();

		if (!subTrees.isEmpty()) {
			CoatUtil.setButtonPosition(collapseButton, x, y);
			collapseButton.render(graphics, mouseX, mouseY, tickDelta);
		}

		CoatUtil.setButtonPosition(nameButton, indent, y);
		nameButton.setWidth(innerWidth);
		nameButton.render(graphics, mouseX, mouseY, tickDelta);

		if (expanded) {
			int curY = y + getBaseHeight();
			for (ConfigTreeEntry entry : subTrees) {
				if (!hoverFound && mouseY > curY) {
					hoverFound = true;
					entry.render(graphics, indent, curY, innerWidth, entryHeight, mouseX, mouseY, true, tickDelta);
				} else {
					entry.render(graphics, indent, curY, innerWidth, entryHeight, mouseX, mouseY, false, tickDelta);
				}
				curY += entry.getHeight();
			}
		}
	}

	/**
	 * Sets the status of whether this category is opened right now.
	 *
	 * @param open The status to set
	 */
	public void setOpen(boolean open) {
		if (this.open != open) {
			if (open) {
				nameButton.setMessage(
						nameButton.getOriginalMessage().copy().withStyle(style -> nameButton.getOriginalMessage().getStyle().withItalic(true))
				);
				setExpanded(true);
			} else {
				nameButton.setMessage(
						nameButton.getOriginalMessage().copy().withStyle(style -> nameButton.getOriginalMessage().getStyle().withItalic(false))
				);
			}
		}
		this.open = open;
	}

	/**
	 * Sets whether the subtrees are visible - opens the collapse.
	 *
	 * @param expanded Whether the subtrees are visible
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		if (expanded) {
			return getBaseHeight() + getExpansionHeight();
		} else {
			return getBaseHeight();
		}
	}

	/**
	 * Gets the height of the unexpanded part of the entry.
	 *
	 * @return The base height
	 */
	public int getBaseHeight() {
		return CoatUtil.DOUBLE_MARGIN + nameButton.getHeight();
	}

	/**
	 * Gets the height of the subtrees.
	 *
	 * @return The expansion height
	 */
	public int getExpansionHeight() {
		int height = 0;
		for (ConfigTreeEntry child : subTrees) {
			height += child.getHeight();
		}
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		List<Message> messages = subTrees.stream().flatMap(entry -> entry.getMessages().stream()).collect(Collectors.toList());
		messages.addAll(contentWidget.getMessages());
		return messages;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GuiEventListener> children() {
		ArrayList<GuiEventListener> children = new ArrayList<>(subTrees.size() + 2);
		if (!subTrees.isEmpty()) {
			children.add(collapseButton);
		}
		children.add(nameButton);
		children.addAll(subTrees);
		return children;
	}

	public List<ConfigTreeEntry> getSubTrees() {
		return subTrees;
	}

	public void addTemporaryTree(ConfigTreeEntry temporaryTreeEntry) {
		temporaryTreeEntry.setParent(this);
		subTrees.add(temporaryTreeEntry);
		parent.entryHeightChanged(this);
	}

	public boolean removeTemporaryTrees() {
		boolean changed = false;
		for (Iterator<ConfigTreeEntry> iterator = subTrees.iterator(); iterator.hasNext(); ) {
			ConfigTreeEntry subTree = iterator.next();
			if (subTree.isTemporary()) {
				iterator.remove();
			} else {
				changed |= subTree.removeTemporaryTrees();
			}
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {
		for (ConfigTreeEntry subTree : subTrees) {
			subTree.tick();
		}
	}

	// TODO: Fix javadoc
	/**
	 * Gets the list widget that this tree entry is linked to.
	 *
	 * @return The linked list widget.
	 */
	public ConfigContentWidget getContentWidget() {
		return contentWidget;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusLost() {
		setFocused(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth() - 8 - CoatUtil.MARGIN;
	}
}
