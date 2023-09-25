package de.siphalor.coat.testmod;

import com.google.common.collect.ImmutableList;
import de.siphalor.amecs.api.PriorityKeyBinding;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.SliderConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.complex.ConfigListWidget;
import de.siphalor.coat.list.entry.*;
import de.siphalor.coat.screen.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class CoatTestmod implements ClientModInitializer {
	private static final String MOD_ID = "coat_testmod";

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(new ScreenBinding(MOD_ID, 84, MOD_ID));
	}

	public static ConfigScreen createScreen() {
		LinkedList<ConfigContainerEntry> list = new LinkedList<>();
		list.add(new ConfigListTextEntry(Text.literal("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(Text.literal("This is some stupidly long text! this is even fucking hell longer.")));
		list.add(new ConfigListTextEntry(Text.literal("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(Text.literal("I'd probably just kill all humans if it came to it - profjb")));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("A boolean with a pretty long name to demonstrate the spacing improvements"),
				Text.literal("The checkbox needs less space, so more space for the name :)"),
				new GenericEntryHandler<>(true, s -> Collections.emptyList()),
				new CheckBoxConfigInput(null, true, false)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("Blub"),
				Text.literal("This is a fine description"),
				new GenericEntryHandler<>("default", s ->
						Collections.singleton(new Message(Message.Level.WARNING, Text.literal("Requires restart!")))
				),
				new TextConfigInput("Some value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("This is a really long title for a config entry"),
				Text.literal("This is a fine description"),
				new GenericEntryHandler<>("test", s ->
						StringUtils.isAllLowerCase(s)
								? Collections.emptyList()
								: ImmutableList.of(
										new Message(Message.Level.INFO, Text.literal("Some information about why this config aught to be lowercase letters only")),
										new Message(Message.Level.ERROR, Text.literal("Must be all lowercase!"))
								)
				),
				new TextConfigInput("Another value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("Blub"),
				Text.literal("This is a fine description"),
				new GenericEntryHandler<>("default", s -> Collections.emptyList()),
				new TextConfigInput("Blub")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("Integer Slider"),
				Text.literal("Some rounding slider"),
				new GenericEntryHandler<>(23, v -> Collections.emptyList()),
				new SliderConfigInput<>(23, -50, 50)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Text.literal("Double Slider"),
				Text.literal(""),
				new GenericEntryHandler<>(3.14D, v -> Collections.emptyList()),
				new SliderConfigInput<>(0D, -10D, 90D)
		));

		ConfigCategoryWidget widget = new ConfigCategoryWidget(MinecraftClient.getInstance(), Text.literal("Hi, my name is Fry"), list, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

		LinkedList<ConfigContainerEntry> list1 = new LinkedList<>();
		list1.add(new ConfigListTextEntry(Text.literal("You know the rules and so do I")));
		list1.add(new ConfigListTextEntry(Text.literal("A full commitment's what I'm thinking of")));
		widget.addSubTree(new ConfigCategoryWidget(MinecraftClient.getInstance(), Text.literal("Abc Def"), list1, new Identifier("textures/block/acacia_planks.png")));

		LinkedList<ConfigContainerEntry> list2 = new LinkedList<>();
		list2.add(new ConfigListTextEntry(Text.literal("Heyho")));
		widget.addSubTree(new ConfigCategoryWidget(MinecraftClient.getInstance(), Text.literal("This is a kinda long category name"), list2, new Identifier("textures/block/end_stone.png")));

		ConfigCategoryWidget widget2 = new ConfigCategoryWidget(MinecraftClient.getInstance(), Text.literal("Ho, this is a no go"), Collections.emptyList(), DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

		ConfigListWidget<String> listWidget = new ConfigListWidget<>(MinecraftClient.getInstance(), Arrays.asList(
				new ConfigListEntry<>(new TextConfigInput("a")),
				new ConfigListEntry<>(new TextConfigInput("bcdef"))
		), new Identifier("textures/block/cobblestone.png"), widget2, Text.literal("A list"), new GenericEntryHandler<>(
				Arrays.asList("Hello", "World"), v -> Collections.emptyList()
		), () -> new ConfigListEntry<>(new TextConfigInput("")));

		widget2.addEntry(new ConfigContainerLinkEntry(listWidget));

		ConfigScreen screen = new ConfigScreen(MinecraftClient.getInstance().currentScreen, Text.literal("Coat Test Mod"), ImmutableList.of(widget, widget2));

		return screen;
	}

	public static class ScreenBinding extends KeyBinding implements PriorityKeyBinding {
		public ScreenBinding(String translationKey, int code, String category) {
			super(translationKey, code, category);
		}

		@Override
		public boolean onPressedPriority() {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.currentScreen instanceof TitleScreen || client.world != null) {
				client.setScreen(createScreen());
			}
			return false;
		}
	}
}
