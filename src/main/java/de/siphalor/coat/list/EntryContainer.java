package de.siphalor.coat.list;

import net.minecraft.client.gui.Element;

/**
 * Marks a container class for entries.
 */
public interface EntryContainer extends Element {
	/**
	 * Called to propagate the height change of a child element.
	 *
	 * @param element The element that updated its height
	 */
	void entryHeightChanged(Element element);

	/**
	 * Gets the width for the contained entries.
	 *
	 * @return The entry width
	 */
	int getEntryWidth();

	/**
	 * Get the parent of this container, if any.
	 *
	 * @return The parent container or <code>null</code> if this is the root entry
	 */
	default EntryContainer getParent() {
		return null;
	}

	/**
	 * Move the focus to a child element.
	 *
	 * @param element The child element to focus
	 */
	void setFocused(Element element);
}
