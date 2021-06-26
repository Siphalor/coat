package de.siphalor.coat.list;

import de.siphalor.coat.handler.Message;

import java.util.Collection;

/**
 * An entry in a {@link ConfigListWidget}.
 */
public abstract class ConfigListEntry extends DynamicEntryListWidget.Entry {
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
