package de.siphalor.coat.testmod;

import de.siphalor.coat.handler.ConfigEntryHandler;
import de.siphalor.coat.handler.Message;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

public class GenericEntryHandler<V> implements ConfigEntryHandler<V> {
	private final V def;
	private final Function<V, Collection<Message>> validator;

	public GenericEntryHandler(V def, Function<V, Collection<Message>> validator) {
		this.def = def;
		this.validator = validator;
	}

	@Override
	public V getDefault() {
		return def;
	}

	@Override
	public @NotNull Collection<Message> getMessages(V value) {
		return validator.apply(value);
	}

	@Override
	public void save(V value) {

	}

	@Override
	public Text asText(V value) {
		return new LiteralText(value.toString());
	}
}
