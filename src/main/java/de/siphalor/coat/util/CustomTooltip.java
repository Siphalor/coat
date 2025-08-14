package de.siphalor.coat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CustomTooltip extends Tooltip {
	private final Supplier<List<FormattedCharSequence>> tooltipSupplier;

	public CustomTooltip(Supplier<List<FormattedCharSequence>> tooltipSupplier, @Nullable Component narration) {
		super(Component.empty(), narration);
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public List<FormattedCharSequence> toCharSequence(Minecraft minecraft) {
		return tooltipSupplier.get();
	}
}
