package de.siphalor.coat.handler;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ConfigEntryHandler<V> {
	V getDefault();
	@NotNull Collection<String> validate(V value);
	void save(V value);
}
