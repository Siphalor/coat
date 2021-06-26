package de.siphalor.coat.list;

import de.siphalor.coat.list.category.ConfigTreeEntry;
import de.siphalor.coat.list.entry.ConfigListHorizontalBreak;
import de.siphalor.coat.list.entry.ConfigListSubTreeEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConfigListWidget extends DynamicEntryListWidget {
	private final Text name;
	private final List<ConfigListWidget> subTrees;
	private final List<ConfigListSubTreeEntry> subTreeLinks;
	private ConfigTreeEntry treeEntry;

	public ConfigListWidget(MinecraftClient client, Text name, Collection<Entry> entries, Identifier background) {
		super(client, entries, background);
		this.name = name;
		subTrees = new LinkedList<>();
		subTreeLinks = new LinkedList<>();
	}

	public Text getName() {
		return name;
	}

	public void addSubTree(ConfigListWidget subWidget) {
		if (subTrees.isEmpty()) {
			children().add(0, new ConfigListHorizontalBreak());
		}

		subTrees.add(subWidget);
		ConfigListSubTreeEntry treeEntry = new ConfigListSubTreeEntry(subWidget);
		children().add(subTreeLinks.size(), treeEntry);
		subTreeLinks.add(treeEntry);
	}

	public List<ConfigListWidget> getSubTrees() {
		return subTrees;
	}

	public ConfigTreeEntry getTreeEntry() {
		if (treeEntry == null) {
			treeEntry = new ConfigTreeEntry(name, this);
		}
		return treeEntry;
	}
}
