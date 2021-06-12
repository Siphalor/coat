package de.siphalor.coat.testmod;

import com.google.common.collect.ImmutableList;
import de.siphalor.amecs.api.PriorityKeyBinding;
import de.siphalor.coat.screen.ConfigScreen;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.ConfigListEntry;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.list.entry.ConfigListConfigEntry;
import de.siphalor.coat.list.entry.ConfigListTextEntry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;

public class CoatTestmod implements ClientModInitializer {
	private static final String MOD_ID = "coat_testmod";

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(new ScreenBinding(MOD_ID, 84, MOD_ID));
	}

	public static ConfigScreen createScreen() {
		LinkedList<ConfigListEntry> list = new LinkedList<>();
		list.add(new ConfigListTextEntry(new LiteralText("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(new LiteralText("This is some stupidly long text! this is even fucking hell longer.")));
		list.add(new ConfigListTextEntry(new LiteralText("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(new LiteralText("I'd probably just kill all humans if it came to it - profjb")));
		list.add(new ConfigListConfigEntry<>(
				new LiteralText("a boolean"),
				new LiteralText("noop"),
				new GenericEntryHandler<>(true, s -> Collections.emptyList()),
				new CheckBoxConfigInput(null, true, false)
		));
		list.add(new ConfigListConfigEntry<>(
				new LiteralText("Blub"),
				new LiteralText("This is a fine description"),
				new GenericEntryHandler<>("default", s ->
						Collections.singleton(new Message(Message.Level.WARNING, new LiteralText("Requires restart!")))
				),
				new TextConfigInput("Some value")
		));
		list.add(new ConfigListConfigEntry<>(
				new LiteralText("This is a really long title for a config entry"),
				new LiteralText("This is a fine description"),
				new GenericEntryHandler<>("test", s ->
						StringUtils.isAllLowerCase(s)
								? Collections.emptyList()
								: ImmutableList.of(
										new Message(Message.Level.INFO, new LiteralText("Some information about why this config aught to be lowercase letters only")),
										new Message(Message.Level.ERROR, new LiteralText("Must be all lowercase!"))
								)
				),
				new TextConfigInput("Another value")
		));
		list.add(new ConfigListConfigEntry<>(
				new LiteralText("Blub"),
				new LiteralText("This is a fine description"),
				new GenericEntryHandler<>("default", s -> Collections.emptyList()),
				new TextConfigInput("Blub")
		));

		ConfigListWidget widget = new ConfigListWidget(MinecraftClient.getInstance(), new LiteralText("Hi"), list, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

		LinkedList<ConfigListEntry> list1 = new LinkedList<>();
		list1.add(new ConfigListTextEntry(new LiteralText("You know the rules and so do I")));
		list1.add(new ConfigListTextEntry(new LiteralText("A full commitment's what I'm thinking of")));
		widget.addSubTree(new ConfigListWidget(MinecraftClient.getInstance(), new LiteralText("a"), list1, new Identifier("textures/block/acacia_planks.png")));

		LinkedList<ConfigListEntry> list2 = new LinkedList<>();
		list2.add(new ConfigListTextEntry(new LiteralText("Heyho")));
		widget.addSubTree(new ConfigListWidget(MinecraftClient.getInstance(), new LiteralText("This is a kinda long category name"), list2, new Identifier("textures/block/end_stone.png")));

		ConfigScreen screen = new ConfigScreen(MinecraftClient.getInstance().currentScreen, new LiteralText("Coat Test Mod"), ImmutableList.of(widget));

		return screen;
	}

	public static class ScreenBinding extends KeyBinding implements PriorityKeyBinding {
		public ScreenBinding(String translationKey, int code, String category) {
			super(translationKey, code, category);
		}

		@Override
		public boolean onPressedPriority() {
			if (MinecraftClient.getInstance().currentScreen instanceof TitleScreen) {
				MinecraftClient.getInstance().openScreen(createScreen());
			}
			return false;
		}
	}
}
