package de.siphalor.coat.list;

import de.siphalor.coat.handler.Message;

import java.util.Collection;

public abstract class ConfigListEntry extends DynamicEntryListWidget.Entry {
	public abstract Collection<Message> getMessages();

	public void save() {

	}
}
