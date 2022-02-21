package de.siphalor.coat.list.entry;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.DynamicEntryListWidget;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;

import java.util.Collection;

/**
 * An entry in a {@link ConfigCategoryWidget}.
 */
public abstract class ConfigContainerEntry extends DynamicEntryListWidget.Entry {
	/**
	 * Gets the messages brought up by this entry.
	 *
	 * @return A collection of messages
	 */
	public abstract Collection<Message> getMessages();

	/**
	 * Called to save this entry and its possible children.
	 */
	public void save() {

	}
}
