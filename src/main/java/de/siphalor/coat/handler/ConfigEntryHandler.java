package de.siphalor.coat.handler;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A handler for an entry. This should handle the entry from the implementation that controls the config entry.
 *
 * @param <V> The value type of the top be associated config entry
 */
public interface ConfigEntryHandler<V> {
	/**
	 * Gets the default value of the entry to be used when the user orders a value reset.
	 *
	 * @return The default value
	 */
	V getDefault();

	/**
	 * Gets error, warning or information messages that should be shown to the user for the given value.
	 *
	 * @param value The value to check
	 * @return The messages to display
	 */
	@NotNull Collection<Message> getMessages(V value);

	/**
	 * This method gets called to save this entry. After all entries have been saved this way,
	 * the save handler of {@link de.siphalor.coat.screen.ConfigScreen} will be called.
	 *
	 * @param value The value to save
	 * @see de.siphalor.coat.screen.ConfigScreen#setOnSave(Runnable)
	 */
	void save(V value);

	/**
	 * Converts the given value to a text so it can be displayed to the user.
	 *
	 * @param value The value to convert
	 * @return The equivalent text
	 */
	Text asText(V value);
}
