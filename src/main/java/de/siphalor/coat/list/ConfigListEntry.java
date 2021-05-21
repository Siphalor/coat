package de.siphalor.coat.list;

import de.siphalor.coat.handler.Message;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collection;

@Environment(EnvType.CLIENT)
public abstract class ConfigListEntry extends DrawableHelper implements Element, TickableElement {
	protected ConfigListCompoundEntry parent;

	public ConfigListCompoundEntry getParent() {
		return parent;
	}

	protected void setParent(ConfigListCompoundEntry parent) {
		this.parent = parent;
	}

	/**
	 * Renders an entry in a list.
	 *  @param matrices    the matrix stack used for rendering
	 * @param x           the X coordinate of the entry
	 * @param y           the Y coordinate of the entry
	 * @param entryHeight The height of the entry
	 * @param mouseX      the X coordinate of the mouse
	 * @param mouseY      the Y coordinate of the mouse
	 * @param hovered     whether the mouse is hovering over the entry
	 */
	public abstract void render(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

	public abstract int getHeight();

	public void widthChanged(int newWidth) {

	}

	public void focusLost() {

	}

	public abstract Collection<Message> getMessages();
}
