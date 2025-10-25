package de.siphalor.coat.testmod;

import de.siphalor.coat.util.EnumeratedMaterial;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;

import java.util.Locale;

public class EnumMaterial<T extends Enum<T>> implements EnumeratedMaterial<T> {
	private final Class<T> enumClass;

	public EnumMaterial(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public T[] values() {
		return enumClass.getEnumConstants();
	}

	@Override
	public Component asText(T value) {
		String key = "coat.testmod."
				+ enumClass.getSimpleName().toLowerCase(Locale.ROOT)
				+ "."
				+ value.name().toLowerCase(Locale.ROOT);
		//# if MC_VERSION_NUMBER >= 11900
		return Component.translatable(key);
		//# else
		//- return new TranslatableComponent(key);
		//# end
	}
}
