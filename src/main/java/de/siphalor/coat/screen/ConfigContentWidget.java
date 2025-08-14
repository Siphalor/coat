package de.siphalor.coat.screen;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface ConfigContentWidget extends GuiEventListener, Renderable, NarratableEntry {
	Component getName();
	ResourceLocation getBackground();
	ConfigTreeEntry getTreeEntry();
	Collection<Message> getMessages();
	void save();
	void setPosition(int left, int top);
	void setRowWidth(int rowWidth);
	void resize(int width, int height);
	void tick();

	/**
	 * @deprecated Override and use {@link #renderWidget} instead.
	 */
	//# if RENDERING == "POSE_STACK"
	//- @Deprecated
	//- default void render(PoseStack graphics, int mouseX, int mouseY, float delta) {
		//- renderWidget(graphics, mouseX, mouseY, delta);
	//- }
	//- default void renderWidget(PoseStack graphics, int mouseX, int mouseY, float delta) {}
	//# elif RENDERING == "GUI_GRAPHICS"
	@Deprecated
	default void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		renderWidget(graphics, mouseX, mouseY, delta);
	}
	default void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {}
	//# end

	@Override
	default NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}
}
