package de.siphalor.coat.list.complex;

import de.siphalor.coat.list.entry.ConfigListEntry;

@FunctionalInterface
public interface ConfigListEntryFactory<V> {
	ConfigListEntry<V> create();
}
