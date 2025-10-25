package de.siphalor.coat.util;

import net.minecraft.network.chat.Component;

public interface EnumeratedMaterial<T> {
	T[] values();
	Component asText(T value);
}
