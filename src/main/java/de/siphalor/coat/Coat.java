package de.siphalor.coat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Coat implements ClientModInitializer {
	public static final String MOD_ID = "coat";

	public static final int TEXT_COLOR = 0xdddddd;
	public static final int MARGIN = 2;
	public static final int HALF_MARGIN = MARGIN / 2;
	public static final int DOUBLE_MARGIN = MARGIN * 2;

	@Override
	public void onInitializeClient() {

	}
}
