package de.siphalor.coat.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.handler.Message;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.TickableElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is mostly a copy of {@link net.minecraft.client.gui.widget.EntryListWidget} to enable variable item heights.
 */
@Environment(EnvType.CLIENT)
public class DynamicEntryListWidget extends ConfigListCompoundEntry implements Drawable, Selectable, TickableElement {
	private static final int TOP_PADDING = 8;
	private static final int BOTTOM_PADDING = 6;
	protected final MinecraftClient client;
	private final Entries entries = new Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	private int rowWidth;
	private double scrollAmount;
	private boolean renderBackground;
	private Identifier background = DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;
	private boolean scrolling;

	public DynamicEntryListWidget(MinecraftClient client, int width, int height, int top, int rowWidth) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = top + height;
		this.left = 0;
		this.right = width;
		this.rowWidth = rowWidth;
	}

	public DynamicEntryListWidget(MinecraftClient client, Collection<ConfigListEntry> entries, Identifier background) {
		this.client = client;
		top = 20;
		addEntries(entries);
		this.background = background;
		renderBackground = true;
	}

	public int getHorizontalPadding() {
		return 4;
	}

	public Identifier getBackground() {
		return background;
	}

	public void setBackground(Identifier background) {
		this.background = background;
	}

	public void setRenderBackground(boolean renderBackground) {
		this.renderBackground = renderBackground;
	}

	public int getEntryWidth() {
		return Math.min(rowWidth, width) - getHorizontalPadding() * 2;
	}

	@Nullable
	public ConfigListEntry getFocused() {
		return (ConfigListEntry) super.getFocused();
	}

	public List<ConfigListEntry> children() {
		return entries;
	}

	public ConfigListEntry getEntry(int index) {
		return entries.get(index);
	}

	public void addEntry(ConfigListEntry entry) {
		entry.setParent(this);
		entries.add(entry);
	}

	public void addEntry(int position, ConfigListEntry entry) {
		entry.setParent(this);
		entries.add(position, entry);
	}

	public void addEntries(Collection<ConfigListEntry> newEntries) {
		for (ConfigListEntry newEntry : newEntries) {
			newEntry.setParent(this);
		}
		entries.addAll(newEntries);
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	@Nullable
	protected final ConfigListEntry getEntryAtPosition(double x, double y) {
		int halfRowWidth = this.getEntryWidth() / 2;
		int screenCenter = this.left + this.width / 2;
		int rowLeft = screenCenter - halfRowWidth;
		int rowRight = screenCenter + halfRowWidth;
		if (x >= getScrollbarPositionX() || x < rowLeft || x > rowRight) {
			return null;
		}
		y -= getEntryAreaTop();
		if (y < 0 || y > getMaxEntryPosition()) {
			return null;
		}
		IntListIterator iterator = entries.bottoms.iterator();
		while (iterator.hasNext()) {
			if (y < iterator.nextInt()) {
				return getEntry(iterator.nextIndex() - 1);
			}
		}
		return null;
	}

	public void entryHeightChanged(Element element) {
		//noinspection SuspiciousMethodCalls
		int index = entries.indexOf(element);
		int bottom = index == 0 ? 0 : entries.bottoms.getInt(index - 1);
		for (int i = index, l = entries.size(); i < l; i++) {
			bottom += entries.get(i).getHeight();
			entries.bottoms.set(i, bottom);
		}
	}

	public void resize(int newWidth, int newHeight) {
		height = newHeight;
		bottom = top + height;
		widthChanged(newWidth);
	}

	@Override
	public void widthChanged(int newWidth) {
		super.widthChanged(newWidth);
		width = newWidth;
		right = left + newWidth;

		for (ConfigListEntry entry : entries) {
			entry.widthChanged(getEntryWidth());
		}
	}

	public void setPosition(int left, int top) {
		this.left   = left;
		this.top    = top;
		this.right  = left + width;
		this.bottom = top + height;
	}

	public void setRowWidth(int rowWidth) {
		this.rowWidth = rowWidth;
		widthChanged(width);
	}

	protected int getMaxEntryPosition() {
		if (entries.isEmpty()) {
			return 0;
		}
		return entries.bottoms.getInt(entries.bottoms.size() - 1);
	}

	protected int getMaxPosition() {
		return getMaxEntryPosition() + BOTTOM_PADDING;
	}

	protected void renderBackground(Tessellator tessellator, BufferBuilder bufferBuilder) {
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0,background);
		RenderSystem.setShaderColor(0.27F, 0.27F, 0.27F, 1F);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(left,  bottom, -100D).texture(left / 32F,  (bottom + (int) getScrollAmount()) / 32F).next();
		bufferBuilder.vertex(right, bottom, -100D).texture(right / 32F, (bottom + (int) getScrollAmount()) / 32F).next();
		bufferBuilder.vertex(right, top,    -100D).texture(right / 32F, (top + (int) getScrollAmount()) / 32F).next();
		bufferBuilder.vertex(left,  top,    -100D).texture(left / 32F,  (top + (int) getScrollAmount()) / 32F).next();
		tessellator.draw();
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int scrollbarXBegin = this.getScrollbarPositionX();
		int scrollbarXEnd = scrollbarXBegin + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		if (renderBackground) {
			renderBackground(tessellator, bufferBuilder);
		}

		int maxScroll = this.getMaxScroll();
		if (maxScroll > 0) {
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			int p = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
			p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
			int q = (int) this.getScrollAmount() * (this.bottom - this.top - p) / maxScroll + this.top;
			if (q < this.top) {
				q = this.top;
			}

			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, top, scrollbarXEnd, bottom, 0, 0, 0, 255);
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, q, scrollbarXEnd, q + p, 128, 128, 128, 255);
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, q, scrollbarXEnd - 1, q + p - 1, 192, 192, 192, 255);
			tessellator.draw();
			RenderSystem.enableTexture();
		}

		this.renderList(matrices, mouseX, mouseY, delta);

		// render top shadow
		fillGradient(matrices, left, top, right, top + TOP_PADDING, 0xcc000000, 0x00000000);

	}

	public void centerScrollOn(ConfigListEntry entry) {
		int index = entries.indexOf(entry);
		setScrollAmount(entries.bottoms.getInt(index) - entry.getHeight() / 2D - (bottom - top) / 2D);
	}

	public void ensureVisible(ConfigListEntry entry) {
		int index = entries.indexOf(entry);
		int entryBottom = entries.bottoms.getInt(index);
		if (getEntryAreaTop() + entryBottom > bottom) {
			setScrollAmount(entryBottom - height);
		}

		int entryTop = index == 0 ? 0 : entries.bottoms.getInt(index - 1);

		if (getEntryAreaTop() + entryTop < top) {
			setScrollAmount(entryTop);
		}
	}

	private void scroll(int amount) {
		this.setScrollAmount(this.getScrollAmount() + amount);
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0D, this.getMaxScroll());
	}

	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - height + TOP_PADDING);
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() && mouseX < (double) (this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return getEntryRight() + getHorizontalPadding();
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			ConfigListEntry entry = getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					setFocused(entry);
					setDragging(true);
					return true;
				}
			}

			return scrolling;
		}
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (button == 0 && this.scrolling) {
			if (mouseY < (double) this.top) {
				this.setScrollAmount(0.0D);
			} else if (mouseY > (double) this.bottom) {
				this.setScrollAmount(this.getMaxScroll());
			} else {
				double d = Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int) ((float) (i * i) / (float) this.getMaxPosition()), 32, i - 8);
				double e = Math.max(1.0D, d / (double) (i - j));
				this.setScrollAmount(this.getScrollAmount() + deltaY * e);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		this.setScrollAmount(this.getScrollAmount() - amount * 10.0D);
		return true;
	}

	@Override
	public final void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.left = x;
		this.top = y;
		render(matrices, mouseX, mouseY, tickDelta);
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double) this.top && mouseY <= (double) this.bottom && mouseX >= (double) this.left && mouseX <= (double) this.right;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Collection<Message> getMessages() {
		return entries.stream().flatMap(entry -> entry.getMessages().stream()).collect(Collectors.toList());
	}

	protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		IntListIterator bottomIter = entries.bottoms.iterator();
		Iterator<ConfigListEntry> entryIter = entries.iterator();
		int relBottom = 0, relTop = 0;
		ConfigListEntry entry = null;

		while (bottomIter.hasNext()) {
			relTop = relBottom;
			relBottom = bottomIter.nextInt();
			entry = entryIter.next();
			if (relBottom > scrollAmount) {
				break;
			}
		}

		ConfigListEntry hoveredEntry = getEntryAtPosition(mouseX, mouseY);

		int rowWidth = getEntryWidth();
		int rowLeft = getEntryLeft();
		while (true) {
			if (entry == null) {
				break;
			}

			int rowTop = relTop + getEntryAreaTop();

			entry.render(matrices, rowLeft, rowTop, rowWidth, relBottom - relTop, mouseX, mouseY, hoveredEntry == entry, delta);

			if (bottomIter.hasNext()) {
				relTop = relBottom;
				relBottom = bottomIter.nextInt();
				entry = entryIter.next();
			} else {
				break;
			}
		}
	}

	public int getEntryLeft() {
		return this.left + this.width / 2 - this.getEntryWidth() / 2;
	}

	public int getEntryRight() {
		return this.right - this.width / 2 + this.getEntryWidth() / 2;
	}

	protected int getEntryAreaTop() {
		return top + TOP_PADDING - (int) scrollAmount;
	}

	protected int getEntryTop(int index) {
		if (index == 0) {
			return getEntryAreaTop();
		}
		return getEntryAreaTop() + entries.bottoms.getInt(index - 1);
	}

	private int getEntryBottom(int index) {
		return getEntryAreaTop() + entries.bottoms.getInt(index);
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		ConfigListEntry old = getFocused();
		if (old != null && old != focused) {
			old.focusLost();
		}
		if (focused != null) {
			ensureVisible((ConfigListEntry) focused);
		}
		super.setFocused(focused);
	}

	protected ConfigListEntry removeEntry(ConfigListEntry entry) {
		return removeEntry(entries.indexOf(entry));
	}

	protected ConfigListEntry removeEntry(int index) {
		return entries.remove(index);
	}

	@Override
	public void tick() {
		for (ConfigListEntry child : entries) {
			child.tick();
		}
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		// TODO: narrations
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<ConfigListEntry> {
		private final List<ConfigListEntry> entries;
		protected final IntList bottoms = new IntArrayList();

		private Entries() {
			this.entries = Lists.newArrayList();
		}

		public ConfigListEntry get(int i) {
			return this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		@Override
		public boolean isEmpty() {
			return entries.isEmpty();
		}

		public ConfigListEntry set(int i, ConfigListEntry entry) {
			entry.setParent(DynamicEntryListWidget.this);
			return this.entries.set(i, entry);
		}

		public void add(int i, ConfigListEntry entry) {
			bottoms.add(0);
			entry.setParent(DynamicEntryListWidget.this);
			entries.add(i, entry);
			entryHeightChanged(entry);
			entry.widthChanged(getEntryWidth());
		}

		@Override
		public boolean addAll(@NotNull Collection<? extends ConfigListEntry> newEntries) {
			int oldSize = entries.size();
			int bottom = bottoms.size() == 0 ? 0 : bottoms.getInt(0);
			entries.addAll(newEntries);
			for (int i = oldSize, l = entries.size(); i < l; i++) {
				bottom += entries.get(i).getHeight();
				bottoms.add(bottom);
			}
			for (ConfigListEntry newEntry : newEntries) {
				newEntry.setParent(DynamicEntryListWidget.this);
				newEntry.widthChanged(getEntryWidth());
			}
			return true;
		}

		public ConfigListEntry remove(int i) {
			ConfigListEntry entry = entries.remove(i);
			bottoms.removeInt(i);
			if (entry == getFocused()) {
				setFocused(null);
			}
			return entry;
		}

		@Override
		public void clear() {
			entries.clear();
			bottoms.clear();
		}
	}
}
