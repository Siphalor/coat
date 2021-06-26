package de.siphalor.coat.input;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

import java.text.NumberFormat;

public class SliderConfigInput<N extends Number> extends SliderWidget implements ConfigInput<N> {
	private final Class<N> valueClass;
	private final N min;
	private final N max;
	private InputChangeListener<N> changeListener;
	private int precision;

	public SliderConfigInput(N value, N min, N max) {
		super(0, 0, 100, 20, LiteralText.EMPTY, value.doubleValue());
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
	public N getValue() {
		return getRealValue();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
	}

	@Override
	public void setValue(N realValue) {
		value = (realValue.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
		value = MathHelper.clamp(value, 0D, 1D);
		applyValue();
		updateMessage();
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public void setChangeListener(InputChangeListener<N> changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public void tick() {

	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int width, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.x = x;
		this.y = y;
		setWidth(width);

		render(matrices, mouseX, mouseY, tickDelta);
	}

	@Override
	protected void updateMessage() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(precision);
		format.setMinimumFractionDigits(precision);
		setMessage(new LiteralText(format.format(getRealValue())));
	}

	@Override
	protected void applyValue() {
		changeListener.inputChanged(getRealValue());
	}

	@SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
	protected N getRealValue() {
		Number realValue = min.doubleValue() + this.value * (max.doubleValue() - min.doubleValue());
		if (valueClass != Float.class && valueClass != Double.class) {
			realValue = (double) Math.round((Double) realValue);
		}


		if (valueClass == Byte.class) {
			return (N)(Object) realValue.byteValue();
		} else if (valueClass == Short.class) {
			return (N)(Object) realValue.shortValue();
		} else if (valueClass == Integer.class) {
			return (N)(Object) realValue.intValue();
		} else if (valueClass == Long.class) {
			return (N)(Object) realValue.longValue();
		} else if (valueClass == Float.class) {
			return (N)(Object) realValue.floatValue();
		} else if (valueClass == Double.class) {
			return (N)(Object) realValue.doubleValue();
		}
		throw new RuntimeException("Number class " + valueClass.getSimpleName() + " is not supported!");
	}
}
