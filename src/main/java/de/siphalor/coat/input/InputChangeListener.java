package de.siphalor.coat.input;

import java.util.function.Consumer;

/**
 * A listener to be called on {@link ConfigInput} changes.
 *
 * @param <V> The value type of the input
 */
public interface InputChangeListener<V> extends Consumer<V> {
	/**
	 * Called when the input changed.
	 *
	 * @param newValue The new value
	 */
	void inputChanged(V newValue);

	/**
	 * Not to be used. Allows this listener to be used as a consumer.
	 *
	 * @param v The new value
	 * @see InputChangeListener#inputChanged(V)
	 */
	@Deprecated
	@Override
	default void accept(V v) {
		inputChanged(v);
	}
}
