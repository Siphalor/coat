package de.siphalor.coat.screen;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ConfigContentWidget extends Element, Drawable, Selectable {
	Text getName();
	Identifier getBackground();
	ConfigTreeEntry getTreeEntry();
	Collection<Message> getMessages();
	void save();
	void setPosition(int left, int top);
	void setRowWidth(int rowWidth);
	void resize(int width, int height);
	void tick();

	/**
	 * @deprecated Override and use {@link #renderWidget(MatrixStack, int, int, float)} instead.
	 */
	@Deprecated
	default void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderWidget(matrices, mouseX, mouseY, delta);
	}
	default void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {}

	@Override
	default SelectionType getType() {
		return SelectionType.NONE;
	}
	@Override
	default boolean isNarratable() {
		return false;
	}
}
