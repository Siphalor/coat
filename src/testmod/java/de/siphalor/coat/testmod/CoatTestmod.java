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
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
		list.add(new ConfigListTextEntry(Component.literal("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(Component.literal("This is some stupidly long text! this is even fucking hell longer.")));
		list.add(new ConfigListTextEntry(Component.literal("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(Component.literal("I'd probably just kill all humans if it came to it - profjb")));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("A boolean with a pretty long name to demonstrate the spacing improvements"),
				Component.literal("The checkbox needs less space, so more space for the name :)"),
				new GenericEntryHandler<>(true, s -> Collections.emptyList()),
				new CheckBoxConfigInput(null, true, false)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("Blub"),
				Component.literal("This is a fine description"),
				new GenericEntryHandler<>("default", s ->
						Collections.singleton(new Message(Message.Level.WARNING, Component.literal("Requires restart!")))
				),
				new TextConfigInput("Some value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("This is a really long title for a config entry"),
				Component.literal("This is a fine description"),
				new GenericEntryHandler<>("test", s ->
						StringUtils.isAllLowerCase(s)
								? Collections.emptyList()
								: ImmutableList.of(
										new Message(Message.Level.INFO, Component.literal("Some information about why this config aught to be lowercase letters only")),
										new Message(Message.Level.ERROR, Component.literal("Must be all lowercase!"))
								)
				),
				new TextConfigInput("Another value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("Blub"),
				Component.literal("This is a fine description"),
				new GenericEntryHandler<>("default", s -> Collections.emptyList()),
				new TextConfigInput("Blub")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("Integer Slider"),
				Component.literal("Some rounding slider"),
				new GenericEntryHandler<>(23, v -> Collections.emptyList()),
				new SliderConfigInput<>(23, -50, 50)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				Component.literal("Double Slider"),
				Component.literal(""),
				new GenericEntryHandler<>(3.14D, v -> Collections.emptyList()),
				new SliderConfigInput<>(0D, -10D, 90D)
		));

		ConfigCategoryWidget widget = new ConfigCategoryWidget(Minecraft.getInstance(), Component.literal("Hi, my name is Fry"), list, null);

		LinkedList<ConfigContainerEntry> list1 = new LinkedList<>();
		list1.add(new ConfigListTextEntry(Component.literal("You know the rules and so do I")));
		list1.add(new ConfigListTextEntry(Component.literal("A full commitment's what I'm thinking of")));

		ResourceLocation achachaPlanks = createIdentifier("textures/block/acacia_planks.png");
		widget.addSubTree(new ConfigCategoryWidget(Minecraft.getInstance(), Component.literal("Abc Def"), list1, achachaPlanks));


		LinkedList<ConfigContainerEntry> list2 = new LinkedList<>();
		list2.add(new ConfigListTextEntry(Component.literal("Heyho")));

		ResourceLocation endStone = createIdentifier("textures/block/end_stone.png");
		widget.addSubTree(new ConfigCategoryWidget(Minecraft.getInstance(), Component.literal("This is a kinda long category name"), list2, endStone));

		ConfigCategoryWidget widget2 = new ConfigCategoryWidget(Minecraft.getInstance(), Component.literal("Ho, this is a no go"), Collections.emptyList(), null);


		ResourceLocation cobblestone = createIdentifier("textures/block/cobblestone.png");
		ConfigListWidget<String> listWidget = new ConfigListWidget<>(Minecraft.getInstance(), Arrays.asList(
				new ConfigListEntry<>(new TextConfigInput("a")),
				new ConfigListEntry<>(new TextConfigInput("bcdef"))
		), cobblestone, widget2, Component.literal("A list"), new GenericEntryHandler<>(
				Arrays.asList("Hello", "World"), v -> Collections.emptyList()
		), () -> new ConfigListEntry<>(new TextConfigInput("")));

		widget2.addEntry(new ConfigContainerLinkEntry(listWidget));

		ConfigScreen screen = new ConfigScreen(Minecraft.getInstance().screen, Component.literal("Coat Test Mod"), ImmutableList.of(widget, widget2));

		return screen;
	}

	private static ResourceLocation createIdentifier(String text) {
		//# if MC_VERSION_NUMBER < 12100
		//- return new ResourceLocation(text);
		//# else
		return ResourceLocation.parse(text);
		//# end
	}

	public static class ScreenBinding extends KeyMapping implements PriorityKeyBinding {
		public ScreenBinding(String translationKey, int code, String category) {
			super(translationKey, code, category);
		}

		@Override
		public boolean onPressedPriority() {
			Minecraft client = Minecraft.getInstance();
			if (client.screen instanceof TitleScreen || client.level != null) {
				client.setScreen(createScreen());
			}
			return false;
		}
	}
}
