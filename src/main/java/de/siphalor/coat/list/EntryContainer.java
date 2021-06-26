package de.siphalor.coat.list;

import net.minecraft.client.gui.Element;

public interface EntryContainer extends Element {
	void entryHeightChanged(Element element);
	int getEntryWidth();

	default EntryContainer getParent() {
		return null;
	}
}
