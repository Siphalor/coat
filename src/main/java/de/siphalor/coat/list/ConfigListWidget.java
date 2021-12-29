package de.siphalor.coat.list;

import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

/**
 * @deprecated use {@link ConfigCategoryWidget} instead
 */
@Deprecated
public class ConfigListWidget extends ConfigCategoryWidget {
	/**
	 * @inheritDoc
	 */
	public ConfigListWidget(MinecraftClient client, Text name, Collection<ConfigContainerEntry> entries, Identifier background) {
		super(client, name, entries, background);
	}
}
