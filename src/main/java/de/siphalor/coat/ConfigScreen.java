package de.siphalor.coat;

import de.siphalor.coat.list.ConfgListEntry;
import de.siphalor.coat.list.ConfigEntryListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.Collection;

public class ConfigScreen extends Screen {
	private final Screen parent;
	private final String modid;
	private final Collection<ConfgListEntry> entries;
	private Runnable onSave;

	private ConfigEntryListWidget listWidget;

	public ConfigScreen(Screen parent, String modid, Collection<ConfgListEntry> entries) {
		super(new TranslatableText("coat.screen." + modid));
		this.parent = parent;
		this.modid = modid;
		this.entries = entries;
	}

	@Override
	protected void init() {
		listWidget = new ConfigEntryListWidget(client, width, height - 20, 20, height, 260);
		listWidget.setRenderBackground(true);
		children.add(listWidget);

		for (ConfgListEntry entry : entries) {
			listWidget.addEntry(entry);
		}

		super.init();
	}

	public ConfigEntryListWidget getListWidget() {
		return listWidget;
	}

	@Override
	public void onClose() {
		MinecraftClient.getInstance().openScreen(parent);
	}

	public void setOnSave(Runnable onSave) {
		this.onSave = onSave;
	}

	protected void onSave() {
		onSave.run();
	}

	@Override
	public void tick() {
		super.tick();
		listWidget.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);

		listWidget.render(matrices, mouseX, mouseY, delta);

		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xffffff);

		super.render(matrices, mouseX, mouseY, delta);
	}

}
