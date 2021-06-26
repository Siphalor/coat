package de.siphalor.coat.list;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigListCompoundEntry extends DynamicEntryListWidget.Entry implements ParentElement, EntryContainer {
	private Element focused;
	private boolean dragging;

	@Override
	public boolean isDragging() {
		return dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Nullable
	@Override
	public Element getFocused() {
		return focused;
	}

	@Override
	public void setFocused(Element focused) {
		this.focused = focused;
	}

	@Override
	public void focusLost() {
		setFocused(null);
		super.focusLost();
	}

	public void entryHeightChanged(Element element) {
		parent.entryHeightChanged(this);
	}
}
