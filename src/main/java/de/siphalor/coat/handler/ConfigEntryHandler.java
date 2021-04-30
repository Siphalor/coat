package de.siphalor.coat.handler;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ConfigEntryHandler<V> {
	V getDefault();
	@NotNull Collection<Message> getMessages(V value);
	void save(V value);
	Text asText(V value);
}
