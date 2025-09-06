package de.siphalor.coat.input;

//- import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.NumberFormat;

/**
 * A numeric config input displayed as a slider.
 * Automatically adapts to the number type used.
 *
 * @param <N> The number type
 */
public class SliderConfigInput<N extends Number> extends AbstractSliderButton implements ConfigInput<N> {
	private final Class<N> valueClass;
	private final N min;
	private final N max;
	private InputChangeListener<N> changeListener;
	/**
	 *  The number of fractional digits to be displayed.
	 *  If not explicitly specified, appropriate precision will be guessed.
	 */
	@Setter
	private int precision;

	/**
	 * Creates a new slider input.
	 *
	 * @param value The initial value of this input
	 * @param min   The minimum of the slider
	 * @param max   The maximum of the slider
	 */
	public SliderConfigInput(N value, N min, N max) {
		super(0, 0, 100, 20, Component.empty(), toInternalValue(value, min, max));
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

	protected static double toInternalValue(Number value, Number min, Number max) {
		return Mth.clamp(
				(value.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue()),
				0D, 1D
		);
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
		value = toInternalValue(realValue, min, max);
		applyValue();
		updateMessage();
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
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		setPosition(x, y);
		setWidth(width);

		render(graphics, mouseX, mouseY, tickDelta);
	}

	//# if MC_VERSION_NUMBER < 11903
	//- private void setPosition(int x, int y) {
	//- 	this.x = x;
	//- 	this.y = y;
	//- }
	//# end

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateMessage() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(precision);
		format.setMinimumFractionDigits(precision);
		setMessage(Component.literal(format.format(getRealValue())));
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
