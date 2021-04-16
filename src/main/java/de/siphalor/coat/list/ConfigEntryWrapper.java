package de.siphalor.coat.list;

public interface ConfigEntryWrapper {
	String getDescription();
	default String getTooltipDescription() {
		return getDescription();
	}
}
