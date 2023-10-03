package de.siphalor.coat.list.entry;

import com.mojang.blaze3d.platform.GlStateManager;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import org.lwjgl.opengl.GL11;

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
	public void render(int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		fill(x, y + PADDING, x + entryWidth, y + PADDING + 1, CoatUtil.SECONDARY_TEXT_COLOR.getArgb());
		GlStateManager.disableDepthTest();
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
}
