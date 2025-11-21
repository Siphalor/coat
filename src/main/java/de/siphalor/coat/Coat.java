package de.siphalor.coat;

import de.siphalor.coat.cursor.CoatCursorTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class Coat implements ClientModInitializer {
	public static final String MOD_ID = "coat";

	@Override
	public void onInitializeClient() {
		//# if MC_VERSION_NUMBER >= 12110
		Minecraft.getInstance().schedule(CoatCursorTypes::initialize);
		//# end
	}
}
