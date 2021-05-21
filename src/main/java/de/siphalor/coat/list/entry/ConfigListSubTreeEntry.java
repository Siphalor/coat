package de.siphalor.coat.list.entry;

import de.siphalor.coat.Coat;
import de.siphalor.coat.ConfigScreen;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.list.ConfigListCompoundEntry;
import de.siphalor.coat.list.ConfigListWidget;
import de.siphalor.coat.util.CoatUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ConfigListSubTreeEntry extends ConfigListCompoundEntry {
	private static final TranslatableText OPEN_TEXT = new TranslatableText(Coat.MOD_ID + ".tree.open");

	private final ConfigListWidget configWidget;
	private final ButtonWidget button;
	private Text nameText;

	public ConfigListSubTreeEntry(ConfigListWidget configWidget) {
		this.configWidget = configWidget;
		button = new ButtonWidget(0, 0, 50, 20, OPEN_TEXT,
				button -> ((ConfigScreen) MinecraftClient.getInstance().currentScreen).openCategory(configWidget.getTreeEntry())
		);
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		button.x = newWidth - button.getWidth();
		nameText = CoatUtil.intelliTrim(MinecraftClient.getInstance().textRenderer, configWidget.getName(), newWidth);
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		button.x = x + getEntryWidth() - button.getWidth();
		button.y = y;
		MinecraftClient.getInstance().textRenderer.draw(matrices, nameText, x, y + 6, CoatUtil.TEXT_COLOR);
		button.render(matrices, mouseX, mouseY, tickDelta);
	}

	@Override
	public int getHeight() {
		return 20 + CoatUtil.MARGIN;
	}

	@Override
	public Collection<Message> getMessages() {
		return Collections.emptyList();
	}

	@Override
	public void tick() {

	}

	@Override
	public int getEntryWidth() {
		return parent.getEntryWidth();
	}

	@Override
	public List<? extends Element> children() {
		return Collections.singletonList(button);
	}
}
