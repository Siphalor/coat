package de.siphalor.coat.list;

import de.siphalor.coat.handler.Message;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collection;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public abstract class ConfgListEntry extends DrawableHelper implements Element, TickableElement {
	protected ConfigEntryListWidget parentList;

	protected void setParentList(ConfigEntryListWidget parentList) {
		this.parentList = parentList;
		widthChanged(parentList.getEntryWidth());
	}

	/**
	 * Renders an entry in a list.
	 *
	 * @param matrices    the matrix stack used for rendering
	 * @param x           the X coordinate of the entry
	 * @param y           the Y coordinate of the entry
	 * @param entryWidth  The width of the entry
	 * @param entryHeight The height of the entry
	 * @param mouseX      the X coordinate of the mouse
	 * @param mouseY      the Y coordinate of the mouse
	 * @param hovered     whether the mouse is hovering over the entry
	 */
	public abstract void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

	public boolean isMouseOver(double mouseX, double mouseY) {
		return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
	}

	public abstract int getHeight();

	public void widthChanged(int newWidth) {

	}

	public void focusLost() {

	}

	public abstract Collection<Message> getMessages();
}
