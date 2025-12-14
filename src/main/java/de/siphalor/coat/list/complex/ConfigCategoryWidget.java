package de.siphalor.coat.list.complex;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import de.siphalor.coat.list.entry.ConfigContainerLinkEntry;
import de.siphalor.coat.list.entry.ConfigListHorizontalBreak;
import de.siphalor.coat.screen.ConfigContentWidget;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
//- import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A config list with entries and subTrees.
 */
public class ConfigCategoryWidget extends DynamicEntryListWidget<ConfigContainerEntry> implements ConfigContentWidget {
	@Getter
	private final Component name;
	@Getter
	private final List<ConfigContentWidget> subTrees;
	private final List<ConfigContainerLinkEntry> subTreeLinks;
	private ConfigTreeEntry treeEntry;

	/**
	 * Constructs a new list widget.
	 *
	 * @param minecraft     The {@link Minecraft} instance
	 * @param name       The name of this config category
	 * @param entries    A collection of entries to directly add to the widget
	 * @param background An identifier referring to a background texture
	 */
	public ConfigCategoryWidget(
			Minecraft minecraft,
			Component name,
			Collection<ConfigContainerEntry> entries,
			//# if MC_VERSION_NUMBER >= 12111
			@Nullable Identifier background
			//# else
			//- @Nullable ResourceLocation background
			//# end
	) {
		super(minecraft, entries, background);
		this.name = name;
		subTrees = new LinkedList<>();
		subTreeLinks = new LinkedList<>();
	}

	/**
	 * Adds a new sub tree to this list and creates a {@link ConfigContainerLinkEntry} as a link for it.
	 *
	 * @param subWidget The sub list to link
	 */
	public void addSubTree(ConfigContentWidget subWidget) {
		if (subTrees.isEmpty()) {
			entries().add(0, new ConfigListHorizontalBreak());
		}

		subTrees.add(subWidget);
		ConfigContainerLinkEntry treeEntry = new ConfigContainerLinkEntry(subWidget);
		entries().add(subTreeLinks.size(), treeEntry);
		subTreeLinks.add(treeEntry);
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
		return entries().stream().flatMap(entry -> entry.getMessages().stream()).collect(Collectors.toList());
	}

	/**
	 * Triggers a save on all entries and sub trees in this list.
	 *
	 * @see de.siphalor.coat.screen.ConfigScreen#setOnSave(Runnable)
	 * @see ConfigCategoryConfigEntry#save()
	 */
	@Override
	public void save() {
		for (ConfigContentWidget subTree : subTrees) {
			subTree.save();
		}
		for (ConfigContainerEntry entry : entries()) {
			entry.save();
		}
	}
}
