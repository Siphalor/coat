package de.siphalor.coat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

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

	public static List<OrderedText> wrapTooltip(TextRenderer textRenderer, MinecraftClient minecraftClient, Text text) {
		return textRenderer.wrapLines(text, minecraftClient.currentScreen.width / 2);
	}

	public static void renderTooltip(MatrixStack matrices, int x, int y, Text text) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.currentScreen.renderOrderedTooltip(
				matrices,
				wrapTooltip(client.textRenderer, client, text),
				x, y
		);
	}

	@Override
	public void onInitializeClient() {

	}
}
