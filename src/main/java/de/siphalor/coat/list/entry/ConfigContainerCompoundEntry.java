package de.siphalor.coat.list.entry;

import de.siphalor.coat.input.ConfigInput;
import de.siphalor.coat.list.EntryContainer;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract entry that consists of several sub elements.
 */
public abstract class ConfigContainerCompoundEntry extends ConfigContainerEntry implements ContainerEventHandler, EntryContainer {
	private GuiEventListener focused;
	private boolean dragging;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDragging() {
		return dragging;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return focused;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocused(GuiEventListener focused) {
		if (this.focused instanceof ContainerEventHandler) {
			((ContainerEventHandler) this.focused).setFocused(null);
		} else if (this.focused instanceof ConfigInput) {
			((ConfigInput<?>) this.focused).setFocused(false);
		}
		this.focused = focused;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusLost() {
		setFocused(null);
		super.focusLost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entryHeightChanged(GuiEventListener element) {
		parent.entryHeightChanged(this);
	}
}
