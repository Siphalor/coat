package de.siphalor.coat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class Coat implements ClientModInitializer {
	public static final String MOD_ID = "coat";

	public static final int TEXT_COLOR = 0xdddddd;
	public static final int SECONDARY_TEXT_COLOR = 0xaaaaaa;
	public static final int MARGIN = 2;
	public static final int HALF_MARGIN = MARGIN / 2;
	public static final int DOUBLE_MARGIN = MARGIN * 2;

	public static void playClickSound() {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1F));
	}

	@Override
	public void onInitializeClient() {

	}
}
