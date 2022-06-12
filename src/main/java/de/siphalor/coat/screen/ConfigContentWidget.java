package de.siphalor.coat.screen;

import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.category.ConfigTreeEntry;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface ConfigContentWidget extends Element {
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
	 * @deprecated Override and use {@link #renderWidget(int, int, float)} instead.
	 */
	@Deprecated
	default void render(int mouseX, int mouseY, float delta) {
		renderWidget(mouseX, mouseY, delta);
	}
	default void renderWidget(int mouseX, int mouseY, float delta) {}
}
