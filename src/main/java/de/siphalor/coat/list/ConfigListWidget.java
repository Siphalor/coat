package de.siphalor.coat.list;

import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.entry.ConfigContainerEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

/**
 * @deprecated use {@link ConfigCategoryWidget} instead
 */
@Deprecated
public class ConfigListWidget extends ConfigCategoryWidget {
	/**
	 * @inheritDoc
	 */
	public ConfigListWidget(Minecraft client, Component name, Collection<ConfigContainerEntry> entries, ResourceLocation background) {
		super(client, name, entries, background);
	}
}
