package de.siphalor.coat.input;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

/**
 * A user input for a configuration entry.
 *
 * @param <V> The value type to be read and written
 */
public interface ConfigInput<V> extends Drawable, Element {
	/**
	 * Gets the current height of the input.
	 * Call to {@link de.siphalor.coat.list.EntryContainer#entryHeightChanged(Element)} to propagate height changes.
	 *
	 * @return The current height
	 */
	int getHeight();

	/**
	 * The current value of this input.
	 *
	 * @return The current value
	 */
	V getValue();

	/**
	 * Updates the value of this input.
	 *
	 * @param value The new value
	 */
	void setValue(V value);

	/**
	 * Set a listener that should be called whenever the input changes.
	 *
	 * @param changeListener The listener to use
	 */
	void setChangeListener(InputChangeListener<V> changeListener);

	/**
	 * Sets whether this input is currently focused and should render and function like that.
	 *
	 * @param focused The focus
	 */
	void setFocused(boolean focused);

	/**
	 * Called on every render tick.
	 * @deprecated Use {@link ConfigInput#tickConfigInput()} instead.
	 */
	@Deprecated
	default void tick() {

	}

	/**
	 * Called on every render tick.
	 */
	default void tickConfigInput() {
		tick();
	}

	/**
	 * Renders this config input
	 * @param matrices    The matrix stack used for rendering
	 * @param x           The x position where the render area for this input begins
	 * @param y           The y position to render this
	 * @param width       The maximum width to render this width
	 * @param entryHeight The height of the enclosing config entry
	 * @param mouseX      The current x position of the mouse
	 * @param mouseY      The current y position of the mouse
	 * @param hovered     Whether this input is currently hovered by the mouse
	 * @param tickDelta   The render tick delta
	 */
	void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);
}
