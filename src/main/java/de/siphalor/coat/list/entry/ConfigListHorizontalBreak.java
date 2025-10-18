package de.siphalor.coat.list.entry;

//- import com.mojang.blaze3d.systems.RenderSystem;
//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.gui.GuiGraphics;
//- import org.lwjgl.opengl.GL32;

import java.util.Collection;
import java.util.Collections;

/**
 * A horizontal break to be used in config lists.
 */
public class ConfigListHorizontalBreak extends ConfigContainerEntry {
	private static final int PADDING = 10;

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		//# if MC_VERSION_NUMBER < 12108
		//- RenderSystem.enableDepthTest();
		//- RenderSystem.depthFunc(GL32.GL_LESS);
		//# end
		//# if RENDERING == "GUI_GRAPHICS"
		graphics.fill(x, y + PADDING, x + entryWidth, y + PADDING + 1, CoatUtil.SECONDARY_TEXT_COLOR.getArgb());
		//# elif RENDERING == "POSE_STACK"
		//- fill(graphics, x, y + PADDING, x + entryWidth, y + PADDING + 1, CoatUtil.SECONDARY_TEXT_COLOR.getArgb());
		//# end
		//# if MC_VERSION_NUMBER < 12108
		//- RenderSystem.disableDepthTest();
		//# end
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return PADDING + 1 + PADDING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {

	}

	//# if MC_VERSION_NUMBER >= 11904
	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}
	//# end
}
