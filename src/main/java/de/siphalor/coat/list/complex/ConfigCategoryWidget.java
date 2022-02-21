package de.siphalor.coat.list.complex;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.entry.ConfigCategoryConfigEntry;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import de.siphalor.coat.list.entry.ConfigContainerLinkEntry;
import de.siphalor.coat.list.entry.ConfigListHorizontalBreak;
import de.siphalor.coat.screen.ConfigContentWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A config list with entries and subTrees.
 */
public class ConfigCategoryWidget extends DynamicEntryListWidget<ConfigContainerEntry> implements ConfigContentWidget {
	private final Text name;
	private final List<ConfigCategoryWidget> subTrees;
	private final List<ConfigContainerLinkEntry> subTreeLinks;
	private ConfigTreeEntry treeEntry;

	/**
	 * Constructs a new list widget.
	 *
	 * @param client     The {@link MinecraftClient} instance
	 * @param name       The name of this config category
	 * @param entries    A collection of entries to directly add to the widget
	 * @param background An identifier referring to a background texture
	 */
	public ConfigCategoryWidget(MinecraftClient client, Text name, Collection<ConfigContainerEntry> entries, Identifier background) {
		super(client, entries, background);
		this.name = name;
		subTrees = new LinkedList<>();
		subTreeLinks = new LinkedList<>();
	}

	/**
	 * Gets the name of this config list/category.
	 *
	 * @return The name
	 */
	@Override
	public Text getName() {
		return name;
	}

	/**
	 * Adds a new sub tree to this list and creates a {@link ConfigContainerLinkEntry} as a link for it.
	 *
	 * @param subWidget The sub list to link
	 */
	public void addSubTree(ConfigCategoryWidget subWidget) {
		if (subTrees.isEmpty()) {
			children().add(0, new ConfigListHorizontalBreak());
		}

		subTrees.add(subWidget);
		ConfigContainerLinkEntry treeEntry = new ConfigContainerLinkEntry(subWidget);
		children().add(subTreeLinks.size(), treeEntry);
		subTreeLinks.add(treeEntry);
	}

	/**
	 * Gets all sub trees/lists.
	 *
	 * @return A list of all sub trees
	 */
	public List<ConfigCategoryWidget> getSubTrees() {
		return subTrees;
	}

	/**
	 * Gets or creates a tree entry that represents this config list in the tree pane.
	 *
	 * @return The associated tree entry
	 */
	@Override
	public ConfigTreeEntry getTreeEntry() {
		if (treeEntry == null) {
			treeEntry = new ConfigTreeEntry(name, this);
		}
		return treeEntry;
	}

	/**
	 * Collects all messages of all entries and sub trees in this list.
	 *
	 * @return A collection of all these entries
	 */
	public Collection<Message> getMessages() {
		return children().stream().flatMap(entry -> entry.getMessages().stream()).collect(Collectors.toList());
	}

	/**
	 * Triggers a save on all entries and sub trees in this list.
	 *
	 * @see de.siphalor.coat.screen.ConfigScreen#setOnSave(Runnable)
	 * @see ConfigCategoryConfigEntry#save()
	 */
	@Override
	public void save() {
		for (ConfigCategoryWidget subTree : subTrees) {
			subTree.save();
		}
		for (ConfigContainerEntry entry : children()) {
			entry.save();
		}
	}
}
