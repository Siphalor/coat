package de.siphalor.coat.input;

import de.siphalor.coat.handler.ConfigEntryHandler;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Objects;

public interface ConfigInput<V> extends Drawable, Element {
	int getHeight();
	V getValue();
	void setValue(V value);
	ConfigEntryHandler<V> getEntryHandler();
	void setFocused(boolean focused);

	default boolean isDefault() {
		return Objects.equals(getEntryHandler().getDefault(), getValue());
	}

	void tick();
	void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);
}
