package de.siphalor.coat.list.entry;

import com.google.common.collect.ImmutableList;
//- import com.mojang.blaze3d.systems.RenderSystem;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.list.complex.ConfigListWidget;
import de.siphalor.coat.util.CoatUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
//- import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
//- import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigListEntry<V> extends ConfigContainerCompoundEntry {
	//# if MC_VERSION_NUMBER < 12002
	//- private static final ResourceLocation HANDLE_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	//# elif MC_VERSION_NUMBER < 12100
	//- private static final ResourceLocation HANDLE_TEXTURE = new ResourceLocation("container/creative_inventory/scroller");
	//#else
	private static final ResourceLocation HANDLE_TEXTURE = ResourceLocation.parse("container/creative_inventory/scroller");
	//# end
	//# if MC_VERSION_NUMBER >= 11900
	private static final Component DELETE_BUTTON_TEXT = Component.literal("x");
	//# else
	//- private static final Component DELETE_BUTTON_TEXT = new TextComponent("x");
	//# end

	private final ConfigInput<V> input;
	private final Button deleteWidget;
	@Setter
	@Getter
	private boolean dragFollow;

	public ConfigListEntry(ConfigInput<V> input) {
		this.input = input;
		//# if MC_VERSION_NUMBER >= 11903
		deleteWidget = Button.builder(DELETE_BUTTON_TEXT, button -> this.deleteClicked()).size(20, 20).build();
		//# else
		//- deleteWidget = new Button(0, 0, 20, 20, DELETE_BUTTON_TEXT, button -> this.deleteClicked());
		//# end
	}

	private void deleteClicked() {
		if (parent instanceof ConfigListWidget) {
			//noinspection unchecked
			((ConfigListWidget<V>) parent).removeEntry(this);
		}
	}

	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		if (isDragFollow()) {
			y = mouseY - entryHeight / 2;
		}

		//# if RENDERING == "GUI_GRAPHICS"
		//# if MC_VERSION_NUMBER < 12002
		//- graphics.blit(HANDLE_TEXTURE, x, y + 2, 232, 0, 12, 15);
		//# elif MC_VERSION_NUMBER < 12100
		//- graphics.blitSprite(HANDLE_TEXTURE, x, y + 2, 12, 15);
		//# else
		graphics.blitSprite(RenderType::guiTextured, HANDLE_TEXTURE, x, y + 2, 12, 15);
		//# end
		//# elif RENDERING == "POSE_STACK"
		//- CoatUtil.setShaderTexture(HANDLE_TEXTURE);
		//- blit(graphics, x, y + 2, 232, 0, 12, 15);
		//# end
		input.render(graphics, x + 12 + CoatUtil.MARGIN, y, entryWidth - 32 - CoatUtil.DOUBLE_MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		//# if MC_VERSION_NUMBER >= 11903
		deleteWidget.setPosition(x + entryWidth - 20, y);
		//# else
		//- deleteWidget.x = x + entryWidth - 20;
		//- deleteWidget.y = y;
		//# end
		deleteWidget.render(graphics, mouseX, mouseY, tickDelta);
	}

	@Override
	public int getHeight() {
		return input.getHeight() + CoatUtil.DOUBLE_MARGIN;
	}

	@Override
	public void tick() {
		input.tickConfigInput();
	}

	public V getValue() {
		return input.getValue();
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth();
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return ImmutableList.of(input, deleteWidget);
	}
}
