package de.siphalor.coat.input;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public interface ConfigInput<V> extends Drawable, Element {
	int getHeight();
	V getValue();
	void setValue(V value);
	void setChangeListener(InputChangeListener<V> changeListener);
	void setFocused(boolean focused);

	void tick();
	void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);
}
