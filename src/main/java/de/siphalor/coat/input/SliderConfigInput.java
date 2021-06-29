package de.siphalor.coat.input;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.math.MathHelper;

import java.text.NumberFormat;

/**
 * A numeric config input displayed as a slider.
 * Automatically adapts to the number type used.
 *
 * @param <N> The number type
 */
public class SliderConfigInput<N extends Number> extends SliderWidget implements ConfigInput<N> {
	private final Class<N> valueClass;
	private final N min;
	private final N max;
	private InputChangeListener<N> changeListener;
	private int precision;

	/**
	 * Creates a new slider input.
	 *
	 * @param value The initial value of this input
	 * @param min   The minimum of the slider
	 * @param max   The maximum of the slider
	 */
	public SliderConfigInput(N value, N min, N max) {
		super(0, 0, 100, 20, value.doubleValue());
		//noinspection unchecked
		valueClass = (Class<N>) value.getClass();
		this.min = min;
		this.max = max;
		if (valueClass == Double.class || valueClass == Float.class) {
			precision = Math.max((int) Math.log10(min.doubleValue()), (int) Math.log10(max.doubleValue()));
			precision = Math.max(0, 4 - precision);
		} else {
			precision = 0;
		}
		updateMessage();
	}

	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getValue() {
		return getRealValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(N realValue) {
		value = (realValue.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
		value = MathHelper.clamp(value, 0D, 1D);
		applyValue();
		updateMessage();
	}

	/**
	 * Sets the amount of fractional digits to be displayed.
	 * If not explicitly specified an appropriate precision will be guessed.
	 *
	 * @param precision The precision of this slider
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setChangeListener(InputChangeListener<N> changeListener) {
		this.changeListener = changeListener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;
		setWidth(width);

		render(mouseX, mouseY, tickDelta);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateMessage() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(precision);
		format.setMinimumFractionDigits(precision);
		setMessage(format.format(getRealValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void applyValue() {
		changeListener.inputChanged(getRealValue());
	}

	/**
	 * Gets the actual value of the config input normalized and and adjusted to the numeric type.
	 *
	 * @return The real value
	 */
	@SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
	protected N getRealValue() {
		Number realValue = min.doubleValue() + this.value * (max.doubleValue() - min.doubleValue());
		if (valueClass != Float.class && valueClass != Double.class) {
			realValue = (double) Math.round((Double) realValue);
		}


		if (valueClass == Byte.class) {
			return (N) (Object) realValue.byteValue();
		} else if (valueClass == Short.class) {
			return (N) (Object) realValue.shortValue();
		} else if (valueClass == Integer.class) {
			return (N) (Object) realValue.intValue();
		} else if (valueClass == Long.class) {
			return (N) (Object) realValue.longValue();
		} else if (valueClass == Float.class) {
			return (N) (Object) realValue.floatValue();
		} else if (valueClass == Double.class) {
			return (N) (Object) realValue.doubleValue();
		}
		throw new RuntimeException("Number class " + valueClass.getSimpleName() + " is not supported!");
	}
}
