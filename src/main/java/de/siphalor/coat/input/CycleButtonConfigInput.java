package de.siphalor.coat.input;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.Coat;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.EnumeratedMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CycleButtonConfigInput<T> extends AbstractContainerEventHandler implements ConfigInput<T> {
	private static final String NULL_TEXT_KEY = Coat.MOD_ID + ".input.enumerated.null";
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component NULL_TEXT = Component.translatable(NULL_TEXT_KEY);
	//# else
	//- private static final Component NULL_TEXT = new TranslatableComponent(NULL_TEXT_KEY);
	//# end

	private static final int BUTTON_PADDING = 2;
	private static final int DEFAULT_WIDTH = 100;

	private final EnumeratedMaterial<T> enumeratedMaterial;
	private final boolean nullAllowed;
	private final Button button;
	private int preferredWidth = DEFAULT_WIDTH;
	private T value;
	private InputChangeListener<T> changeListener;

	public CycleButtonConfigInput(EnumeratedMaterial<T> enumeratedMaterial, boolean nullAllowed, T value) {
		this.enumeratedMaterial = enumeratedMaterial;
		this.nullAllowed = nullAllowed;
		this.value = value;
		determinePreferredWidth();
		//# if MC_VERSION_NUMBER >= 11903
		this.button = Button.builder(getButtonText(), (_button) -> onClick())
				.size(preferredWidth, 20)
				.build();
		//# else
		//- this.button = new Button(0, 0, preferredWidth, 20, getButtonText(), (_button) -> onClick());
		//# end
	}

	private void determinePreferredWidth() {
		Font font = Minecraft.getInstance().font;
		preferredWidth = Arrays.stream(enumeratedMaterial.values())
				.mapToInt(value -> font.width(enumeratedMaterial.asText(value)))
				.max()
				.orElse(DEFAULT_WIDTH) + 2 * BUTTON_PADDING;
	}

	@Override
	public int getHeight() {
		return button.getHeight();
	}

	@Override
	public int getPreferredWidth() {
		return preferredWidth;
	}

	private void onClick() {
		setValue(nextValue());
	}

	private T nextValue() {
		for (int i = 0; i < enumeratedMaterial.values().length; i++) {
			if (enumeratedMaterial.values()[i] == value) {
				if (i + 1 < enumeratedMaterial.values().length) {
					return enumeratedMaterial.values()[i + 1];
				} else if (nullAllowed) {
					return null;
				} else {
					return enumeratedMaterial.values()[0];
				}
			}
		}
		return enumeratedMaterial.values()[0];
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		if (this.value != value) {
			this.value = value;
			valueChanged();
		}
	}

	private void valueChanged() {
		if (value != null) {
			button.setMessage(enumeratedMaterial.asText(value));
		} else {
			button.setMessage(getButtonText());
		}
		if (changeListener != null) {
			changeListener.inputChanged(value);
		}
	}

	@Override
	public void setChangeListener(InputChangeListener<T> changeListener) {
		this.changeListener = changeListener;
	}

	private Component getButtonText() {
		if (value != null) {
			return enumeratedMaterial.asText(value);
		}
		try {
			return enumeratedMaterial.asText(null);
		} catch (Exception e) {
			return NULL_TEXT;
		}
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return button.isMouseOver(mouseX, mouseY);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return Collections.singletonList(button);
	}

	@Override
	public void setFocused(boolean focused) {
		//# if MC_VERSION_NUMBER >= 11904
		button.setFocused(focused);
		//# else
		//- button.changeFocus(true);
		//# end
	}

	//# if MC_VERSION_NUMBER >= 11904
	@Override
	public boolean isFocused() {
		return button.isFocused();
	}
	//# end

	@Override
	public void render(
			//# if RENDERING == "GUI_GRAPHICS"
			GuiGraphics context,
			//# else
			//- PoseStack context,
			//# end
			int x,
			int y,
			int width,
			int entryHeight,
			int mouseX,
			int mouseY,
			boolean hovered,
			float tickDelta
	) {
		CoatUtil.setButtonPosition(button, x, y);
		button.setWidth(width);
		render(context, mouseX, mouseY, tickDelta);
	}

	@Override
	public void render(
			//# if RENDERING == "GUI_GRAPHICS"
			GuiGraphics context,
			//# else
			//- PoseStack context,
			//# end
			int mouseX,
			int mouseY,
			float tickDelta
	) {
		button.render(context, mouseX, mouseY, tickDelta);
	}
}
