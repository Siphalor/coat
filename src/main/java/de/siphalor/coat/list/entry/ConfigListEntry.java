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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigListEntry<V> extends ConfigContainerCompoundEntry {
	//# if MC_VERSION_NUMBER < 12000
	//- private static final ResourceLocation HANDLE_TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	//# else
	private static final ResourceLocation HANDLE_TEXTURE = new ResourceLocation("container/creative_inventory/scroller");
	//# end

	private final ConfigInput<V> input;
	private final Button deleteWidget;
	@Setter
	@Getter
	private boolean dragFollow;

	public ConfigListEntry(ConfigInput<V> input) {
		this.input = input;
		deleteWidget = Button.builder(Component.literal("x"), button -> {
			if (parent instanceof ConfigListWidget) {
				//noinspection unchecked
				((ConfigListWidget<V>) parent).removeEntry(this);
			}
		}).size(20, 20).build();
	}

	@Override
	//# if RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		if (isDragFollow()) {
			y = mouseY - entryHeight / 2;
		}

		//# if RENDERING == "POSE_STACK"
		//- RenderSystem.setShaderTexture(0, HANDLE_TEXTURE);
		//- blit(graphics, x, y + 2, 232, 0, 12, 15);
		//# elif RENDERING == "GUI_GRAPHICS"
		graphics.blitSprite(HANDLE_TEXTURE, x, y + 2, 12, 15);
		//# end
		input.render(graphics, x + 12 + CoatUtil.MARGIN, y, entryWidth - 32 - CoatUtil.DOUBLE_MARGIN, entryHeight, mouseX, mouseY, hovered, tickDelta);
		deleteWidget.setPosition(x + entryWidth - 20, y);
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
