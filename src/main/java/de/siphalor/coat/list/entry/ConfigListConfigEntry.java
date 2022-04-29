package de.siphalor.coat.list.entry;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.input.ConfigInput;
import net.minecraft.text.MutableText;

/**
 * @deprecated use {@link ConfigCategoryConfigEntry} instead
 */
@Deprecated
public class ConfigListConfigEntry<V> extends ConfigCategoryConfigEntry<V> {
	/**
	 * Constructs a new config entry.
	 *
	 * @param name         The name of this entry
	 * @param description  The description text of this entry
	 * @param entryHandler An entry handler for this entry
	 * @param input        The config input to use
	 */
	public ConfigListConfigEntry(MutableText name, MutableText description, ConfigEntryHandler<V> entryHandler, ConfigInput<V> input) {
		super(name, description, entryHandler, input);
	}
}
