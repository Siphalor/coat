package de.siphalor.coat.util.renderstate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RenderStateHelper {
	public static ScreenRectangle computeRectBounds(
			ScreenRectangle elementRect,
			Matrix3x2f pose,
			@Nullable ScreenRectangle scissorRect
	) {
		if (scissorRect == null) {
			return elementRect.transformMaxBounds(pose);
		} else {
			return elementRect.transformMaxBounds(pose).intersection(scissorRect);
		}
	}
}
