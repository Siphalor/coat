package de.siphalor.coat.list;

import net.minecraft.client.gui.components.events.GuiEventListener;

/**
 * Marks a container class for entries.
 */
public interface EntryContainer extends GuiEventListener {
	/**
	 * Called to propagate the height change of a child element.
	 *
	 * @param element The element that updated its height
	 */
	void entryHeightChanged(GuiEventListener element);

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
	void setFocused(GuiEventListener element);
}
