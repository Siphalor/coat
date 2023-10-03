package de.siphalor.coat.list.entry;

import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.opengl.GL32;

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
	public void render(DrawContext drawContext, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL32.GL_LESS);
		drawContext.fill(x, y + PADDING, x + entryWidth, y + PADDING + 1, CoatUtil.SECONDARY_TEXT_COLOR.getArgb());
		RenderSystem.disableDepthTest();
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

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}
}
