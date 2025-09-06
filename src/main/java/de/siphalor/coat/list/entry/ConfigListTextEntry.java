package de.siphalor.coat.list.entry;

//- import com.mojang.blaze3d.vertex.PoseStack;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A textual entry for config list widgets.
 */
public class ConfigListTextEntry extends ConfigContainerEntry {
	private final Font font;
	private final Component text;
	private List<FormattedCharSequence> multilineText;
	private int height;

	public ConfigListTextEntry(Component text) {
		super();
		this.text = text;
		font = Minecraft.getInstance().font;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		multilineText = font.split(text, newWidth);
		height = multilineText.size() * 9 + CoatUtil.MARGIN + CoatUtil.MARGIN;
		parent.entryHeightChanged(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	//# if RENDERING == "GUI_GRAPHICS"
	public void render(GuiGraphics graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# elif RENDERING == "POSE_STACK"
	//- public void render(PoseStack graphics, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
	//# end
		for (int i = 0; i < multilineText.size(); i++) {
			//# if RENDERING == "GUI_GRAPHICS"
			graphics.drawString(font, multilineText.get(i), x, y + i * 9, CoatUtil.TEXT_COLOR.getArgb(), false);
			//# elif RENDERING == "POSE_STACK"
			//- font.draw(graphics, multilineText.get(i), x, y + i * 9, CoatUtil.TEXT_COLOR.getArgb());
			//# end
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
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
