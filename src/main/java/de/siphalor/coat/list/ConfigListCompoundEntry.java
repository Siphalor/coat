package de.siphalor.coat.list;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigListCompoundEntry extends ConfigListEntry implements ParentElement, Selectable {
	private Element focused;

	@Override
	public boolean isDragging() {
		return false;
	}

	@Override
	public void setDragging(boolean dragging) {

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

	public abstract int getEntryWidth();

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		// TODO: narrations
	}

	@Override
	public SelectionType getType() {
		return focused != null ? SelectionType.FOCUSED : SelectionType.NONE;
	}
}
