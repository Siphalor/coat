package de.siphalor.coat.testmod;

import com.google.common.collect.ImmutableList;
import de.siphalor.amecs.api.PriorityKeyBinding;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.CheckBoxConfigInput;
import de.siphalor.coat.input.CycleButtonConfigInput;
import de.siphalor.coat.input.SliderConfigInput;
import de.siphalor.coat.input.TextConfigInput;
import de.siphalor.coat.list.complex.ConfigCategoryWidget;
import de.siphalor.coat.list.complex.ConfigListWidget;
import de.siphalor.coat.list.entry.*;
import de.siphalor.coat.screen.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//- import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.Identifier;
//- import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class CoatTestmod implements ClientModInitializer {
	private static final String MOD_ID = "coat_testmod";

	private static MutableComponent text(String text) {
		//# if MC_VERSION_NUMBER >= 11900
		return Component.literal(text);
		//# else
		//- return new TextComponent(text);
		//# end
	}

	@Override
	public void onInitializeClient() {
		//# if MC_VERSION_NUMBER >= 12110
		var category = KeyMapping.Category.register(
				//# if MC_VERSION_NUMBER >= 12111
				Identifier.fromNamespaceAndPath(MOD_ID, "main")
				//# else
				//- ResourceLocation.fromNamespaceAndPath(MOD_ID, "main")
				//# end
		);
		KeyBindingHelper.registerKeyBinding(new ScreenBinding(MOD_ID, 84, category));
		//# else
		//- KeyBindingHelper.registerKeyBinding(new ScreenBinding(MOD_ID, 84, MOD_ID));
		//# end
	}

	public static ConfigScreen createScreen() {
		LinkedList<ConfigContainerEntry> list = new LinkedList<>();
		list.add(new ConfigListTextEntry(text("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(text("This is some stupidly long text! this is even fucking hell longer.")));
		list.add(new ConfigListTextEntry(text("This is some stupidly long text!")));
		list.add(new ConfigListTextEntry(text("I'd probably just kill all humans if it came to it - profjb")));
		list.add(new ConfigCategoryConfigEntry<>(
				text("A boolean with a pretty long name to demonstrate the spacing improvements"),
				text("The checkbox needs less space, so more space for the name :)"),
				new GenericEntryHandler<>(true, s -> Collections.emptyList()),
				new CheckBoxConfigInput(true)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("Blub"),
				text("This is a fine description"),
				new GenericEntryHandler<>("default", s ->
						Collections.singleton(new Message(Message.Level.WARNING, text("Requires restart!")))
				),
				new TextConfigInput("Some value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("This is a really long title for a config entry"),
				text("This is a fine description"),
				new GenericEntryHandler<>("test", s ->
						StringUtils.isAllLowerCase(s)
								? Collections.emptyList()
								: ImmutableList.of(
										new Message(Message.Level.INFO, text("Some information about why this config aught to be lowercase letters only")),
										new Message(Message.Level.ERROR, text("Must be all lowercase!"))
								)
				),
				new TextConfigInput("Another value")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("Blub"),
				text("This is a fine description"),
				new GenericEntryHandler<>("default", s -> Collections.emptyList()),
				new TextConfigInput("Blub")
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("Integer Slider"),
				text("Some rounding slider"),
				new GenericEntryHandler<>(23, v -> Collections.emptyList()),
				new SliderConfigInput<>(23, -50, 50)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("Double Slider"),
				text(""),
				new GenericEntryHandler<>(3.14D, v -> Collections.emptyList()),
				new SliderConfigInput<>(0D, -10D, 90D)
		));
		list.add(new ConfigCategoryConfigEntry<>(
				text("Enum selector"),
				text(""),
				new GenericEntryHandler<>(EnvType.CLIENT, v -> Collections.emptyList()),
				new CycleButtonConfigInput<>(new EnumMaterial<>(EnvType.class), true, null)
		));

		ConfigCategoryWidget widget = new ConfigCategoryWidget(Minecraft.getInstance(), text("Hi, my name is Fry"), list, null);

		LinkedList<ConfigContainerEntry> list1 = new LinkedList<>();
		list1.add(new ConfigListTextEntry(text("You know the rules and so do I")));
		list1.add(new ConfigListTextEntry(text("A full commitment's what I'm thinking of")));

		widget.addSubTree(new ConfigCategoryWidget(
				Minecraft.getInstance(),
				text("Abc Def"),
				list1,
				createIdentifier("textures/block/acacia_planks.png")
		));


		LinkedList<ConfigContainerEntry> list2 = new LinkedList<>();
		list2.add(new ConfigListTextEntry(text("Heyho")));

		widget.addSubTree(new ConfigCategoryWidget(
				Minecraft.getInstance(),
				text("This is a kinda long category name"),
				list2,
				createIdentifier("textures/block/end_stone.png")
		));

		ConfigCategoryWidget widget2 = new ConfigCategoryWidget(Minecraft.getInstance(), text("Ho, this is a no go"), Collections.emptyList(), null);


		ConfigListWidget<String> listWidget = new ConfigListWidget<>(
				Minecraft.getInstance(),
				Arrays.asList(
						new ConfigListEntry<>(new TextConfigInput("a")),
						new ConfigListEntry<>(new TextConfigInput("bcdef"))
				),
				createIdentifier("textures/block/cobblestone.png"),
				widget2,
				text("A list"),
				new GenericEntryHandler<>(
						Arrays.asList("Hello", "World"), v -> Collections.emptyList()
				),
				() -> new ConfigListEntry<>(new TextConfigInput(""))
		);

		widget2.addEntry(new ConfigContainerLinkEntry(listWidget));

		ConfigScreen screen = new ConfigScreen(Minecraft.getInstance().screen, text("Coat Test Mod"), ImmutableList.of(widget, widget2));

		return screen;
	}

	//# if MC_VERSION_NUMBER >= 12111
	private static Identifier createIdentifier(String text) {
		return Identifier.parse(text);
	}
	//# else
	//- private static ResourceLocation createIdentifier(String text) {
	//- 	//# if MC_VERSION_NUMBER < 12100
	//- 	return new ResourceLocation(text);
	//- 	//# else
	//- 	return ResourceLocation.parse(text);
	//- 	//# end
	//- }
	//# end

	public static class ScreenBinding extends KeyMapping implements PriorityKeyBinding {
		public ScreenBinding(
				String translationKey,
				int code,
				//# if MC_VERSION_NUMBER >= 12110
				Category category
				//# else
				//- String category
				//# end
		) {
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
