package de.siphalor.coat.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CustomTooltip extends Tooltip {
	private final Supplier<List<OrderedText>> tooltipSupplier;

	public CustomTooltip(Supplier<List<OrderedText>> tooltipSupplier, @Nullable Text narration) {
		super(Text.empty(), narration);
		this.tooltipSupplier = tooltipSupplier;
	}

	@Override
	public List<OrderedText> getLines(MinecraftClient client) {
		return tooltipSupplier.get();
	}
}
