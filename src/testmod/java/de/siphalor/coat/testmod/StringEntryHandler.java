package de.siphalor.coat.testmod;

import de.siphalor.coat.handler.ConfigEntryHandler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class StringEntryHandler implements ConfigEntryHandler<String> {
	@Override
	public String getDefault() {
		return "default";
	}

	@Override
	public @NotNull Collection<String> validate(String value) {
		if (StringUtils.isAllLowerCase(value)) {
			return Collections.emptyList();
		}
		return Collections.singleton("No upper case allowed!");
	}

	@Override
	public void save(String value) {
		System.out.println("Save value \"" + value + "\"");
	}
}
