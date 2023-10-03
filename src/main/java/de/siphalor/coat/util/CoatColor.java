package de.siphalor.coat.util;

import lombok.Value;

public interface CoatColor {
	CoatColor BLACK = rgb(0x000000);
	CoatColor WHITE = rgb(0xFFFFFF);
	CoatColor TRANSPARENT = rgba(0x00000000);

	int getRed();

	default float getRedF() {
		return getRed() / 255F;
	}

	int getGreen();

	default float getGreenF() {
		return getGreen() / 255F;
	}

	int getBlue();

	default float getBlueF() {
		return getBlue() / 255F;
	}

	int getAlpha();

	default float getAlphaF() {
		return getAlpha() / 255F;
	}

	int getRgba();

	int getArgb();

	CoatColor withRed(int red);

	CoatColor withGreen(int green);

	CoatColor withBlue(int blue);

	CoatColor withAlpha(int alpha);

	static CoatColor rgb(int rgb) {
		return rgb(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF);
	}

	static CoatColor rgb(int red, int green, int blue) {
		return rgba(red, green, blue, 255);
	}

	static CoatColor rgba(int rgba) {
		return rgba(rgba >> 24 & 0xFF, rgba >> 16 & 0xFF, rgba >> 8 & 0xFF, rgba & 0xFF);
	}

	static CoatColor rgba(int red, int green, int blue, int alpha) {
		return new Static(
				red, green, blue, alpha,
				(red << 24) | (green << 16) | (blue << 8) | alpha,
				(alpha << 24) | (red << 16) | (green << 8) | blue
		);
	}

	static CoatColor argb(int argb) {
		return rgba(argb >> 16 & 0xFF, argb >> 8 & 0xFF, argb & 0xFF, argb >> 24 & 0xFF);
	}

	static CoatColor argb(int alpha, int red, int green, int blue) {
		return rgba(red, green, blue, alpha);
	}

	@Value
	class Static implements CoatColor {
		int red;
		int green;
		int blue;
		int alpha;
		int rgba;
		int argb;

		@Override
		public CoatColor withRed(int red) {
			return CoatColor.rgba(red, green, blue, alpha);
		}

		@Override
		public CoatColor withGreen(int green) {
			return CoatColor.rgba(red, green, blue, alpha);
		}

		@Override
		public CoatColor withBlue(int blue) {
			return CoatColor.rgba(red, green, blue, alpha);
		}

		@Override
		public CoatColor withAlpha(int alpha) {
			return CoatColor.rgba(red, green, blue, alpha);
		}
	}
}
