package de.siphalor.coat.list.entry;

public interface ConfigEntryWrapper {
	String getDescription();
	default String getTooltipDescription() {
		return getDescription();
	}
}
