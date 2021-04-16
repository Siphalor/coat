package de.siphalor.coat.input;

import java.util.function.Consumer;

public interface InputChangeListener<V> extends Consumer<V> {
	void inputChanged(V newValue);

	@Override
	default void accept(V v) {
		inputChanged(v);
	};
}
