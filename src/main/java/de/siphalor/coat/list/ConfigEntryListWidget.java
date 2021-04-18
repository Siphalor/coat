package de.siphalor.coat.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TickableElement;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * This is mostly a copy of {@link net.minecraft.client.gui.widget.EntryListWidget} to enable variable item heights.
 */
@Environment(EnvType.CLIENT)
public class ConfigEntryListWidget extends AbstractParentElement implements Drawable, TickableElement {
	protected final MinecraftClient client;
	private final List<Entry> children = new Entries();
	private final IntList entryBottoms = new IntArrayList();
	private final int rowWidth;
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	private double scrollAmount;
	private boolean renderSelection = true;
	private boolean renderHeader;
	protected int headerHeight;
	private boolean scrolling;
	private Entry selected;

	public ConfigEntryListWidget(MinecraftClient client, int width, int height, int top, int bottom, int rowWidth) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
		this.rowWidth = rowWidth;
	}

	public void setRenderSelection(boolean renderSelection) {
		this.renderSelection = renderSelection;
	}

	protected void setRenderHeader(boolean renderHeader, int headerHeight) {
		this.renderHeader = renderHeader;
		this.headerHeight = headerHeight;
		if (!renderHeader) {
			this.headerHeight = 0;
		}

	}

	public int getRowWidth() {
		return rowWidth;
	}

	@Nullable
	public Entry getSelected() {
		return this.selected;
	}

	public void setSelected(@Nullable Entry entry) {
		this.selected = entry;
	}

	@Nullable
	public Entry getFocused() {
		return (Entry) super.getFocused();
	}

	public final List<Entry> children() {
		return this.children;
	}

	protected final void clearEntries() {
		children.clear();
		entryBottoms.clear();
	}

	protected void replaceEntries(Collection<Entry> newEntries) {
		clearEntries();
		addEntries(newEntries);
	}

	protected Entry getEntry(int index) {
		return this.children().get(index);
	}

	public int addEntry(Entry entry) {
		children.add(entry);
		entryBottoms.add(getMaxEntryPosition() + entry.getHeight());
		return children.size() - 1;
	}

	public void addEntries(Collection<Entry> newEntries) {
		int oldSize = newEntries.size();
		int bottom = entryBottoms.size() == 0 ? 0 : entryBottoms.getInt(0);
		children.addAll(newEntries);
		for (int i = oldSize, l = children.size(); i < l; i++) {
			bottom += children.get(i).getHeight();
			entryBottoms.add(bottom);
		}
	}

	protected int getEntryCount() {
		return this.children().size();
	}

	protected boolean isSelectedEntry(int index) {
		return Objects.equals(this.getSelected(), this.children().get(index));
	}

	@Nullable
	protected final Entry getEntryAtPosition(double x, double y) {
		int halfRowWidth = this.getRowWidth() / 2;
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
		IntListIterator iterator = entryBottoms.iterator();
		while (iterator.hasNext()) {
			if (y < iterator.nextInt()) {
				return getEntry(iterator.nextIndex() - 1);
			}
		}
		return null;
	}

	public void entryHeightChanged(Entry entry) {
		int index = children.indexOf(entry);
		int bottom = index == 0 ? 0 : entryBottoms.getInt(index - 1);
		for (int i = index, l = children.size(); i < l; i++) {
			bottom += children.get(i).getHeight();
			entryBottoms.set(i, bottom);
		}
	}

	public void updateSize(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setLeftPos(int left) {
		this.left = left;
		this.right = left + this.width;
	}

	protected int getMaxEntryPosition() {
		if (entryBottoms.isEmpty()) {
			return 0;
		}
		return entryBottoms.getInt(entryBottoms.size() - 1);
	}

	protected int getMaxPosition() {
		return headerHeight + getMaxEntryPosition();
	}

	protected void clickedHeader(int x, int y) {
	}

	protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
	}

	protected void renderBackground(Tessellator tessellator, BufferBuilder bufferBuilder) {
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(this.left, this.bottom, 0.0D).texture((float)this.left / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(this.right, this.bottom, 0.0D).texture((float)this.right / 32.0F, (float)(this.bottom + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(this.right, this.top, 0.0D).texture((float)this.right / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
		bufferBuilder.vertex(this.left, this.top, 0.0D).texture((float)this.left / 32.0F, (float)(this.top + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).next();
		tessellator.draw();
	}

	protected void renderShadows(Tessellator tessellator, BufferBuilder bufferBuilder) {
		this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(519);
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(this.left, this.top, -100.0D).texture(0.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left + this.width, this.top, -100.0D).texture((float)this.width / 32.0F, (float)this.top / 32.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left + this.width, 0.0D, -100.0D).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left, 0.0D, -100.0D).texture(0.0F, 0.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left, this.height, -100.0D).texture(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left + this.width, this.height, -100.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left + this.width, this.bottom, -100.0D).texture((float)this.width / 32.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.left, this.bottom, -100.0D).texture(0.0F, (float)this.bottom / 32.0F).color(64, 64, 64, 255).next();
		tessellator.draw();
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
		RenderSystem.disableAlphaTest();
		RenderSystem.shadeModel(7425);
		RenderSystem.disableTexture();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		bufferBuilder.vertex(this.left, this.top + 4, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.right, this.top + 4, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.right, this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.left, this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.left, this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.right, this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.right, this.bottom - 4, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.left, this.bottom - 4, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 0).next();
		tessellator.draw();

	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int i = this.getScrollbarPositionX();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		renderBackground(tessellator, bufferBuilder);

		int k = this.getRowLeft();
		int l = this.top + 4 - (int)this.getScrollAmount();
		if (this.renderHeader) {
			this.renderHeader(matrices, k, l, tessellator);
		}

		this.renderList(matrices, k, l, mouseX, mouseY, delta);

		renderShadows(tessellator, bufferBuilder);

		int o = this.getMaxScroll();
		if (o > 0) {
			RenderSystem.disableTexture();
			int p = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
			p = MathHelper.clamp(p, 32, this.bottom - this.top - 8);
			int q = (int)this.getScrollAmount() * (this.bottom - this.top - p) / o + this.top;
			if (q < this.top) {
				q = this.top;
			}

			bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
			bufferBuilder.vertex(i, this.bottom, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(j, this.bottom, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(j, this.top, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(i, this.top, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).next();
			bufferBuilder.vertex(i, q + p, 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(j, q + p, 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(j, q, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(i, q, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).next();
			bufferBuilder.vertex(i, q + p - 1, 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(j - 1, q + p - 1, 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(j - 1, q, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).next();
			bufferBuilder.vertex(i, q, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).next();
			tessellator.draw();
		}

		RenderSystem.enableTexture();
		RenderSystem.shadeModel(7424);
		RenderSystem.enableAlphaTest();
		RenderSystem.disableBlend();
	}

	protected void centerScrollOn(Entry entry) {
		int index = children.indexOf(entry);
		setScrollAmount(entryBottoms.getInt(index) - entry.getHeight() / 2D - (bottom - top) / 2D);
	}

	protected void ensureVisible(Entry entry) {
		int index = children.indexOf(entry);
		int bottom = entryBottoms.getInt(index);
		if (scrollAmount + height > bottom) {
			setScrollAmount(bottom - height);
		}

		int top = index == 0 ? 0 : entryBottoms.getInt(index - 1);

		if (scrollAmount > top) {
			setScrollAmount(top);
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
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return this.width / 2 + 124;
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			Entry entry = getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button)) {
					setFocused(entry);
					setDragging(true);
					return true;
				}
			} else if (button == 0) {
				clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
				return true;
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
			if (mouseY < (double)this.top) {
				this.setScrollAmount(0.0D);
			} else if (mouseY > (double)this.bottom) {
				this.setScrollAmount(this.getMaxScroll());
			} else {
				double d = Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int)((float)(i * i) / (float)this.getMaxPosition()), 32, i - 8);
				double e = Math.max(1.0D, d / (double)(i - j));
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

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		} else if (keyCode == 264) {
			this.moveSelection(MoveDirection.DOWN);
			return true;
		} else if (keyCode == 265) {
			this.moveSelection(MoveDirection.UP);
			return true;
		} else {
			return false;
		}
	}


	protected void moveSelection(MoveDirection direction) {
		this.moveSelectionIf(direction, (entry) -> true);
	}

	protected void ensureSelectedEntryVisible() {
		Entry entry = this.getSelected();
		if (entry != null) {
			this.setSelected(entry);
			this.ensureVisible(entry);
		}
	}

	protected void moveSelectionIf(MoveDirection direction, Predicate<Entry> predicate) {
		int offset = direction == MoveDirection.UP ? -1 : 1;
		if (!this.children().isEmpty()) {
			int index = this.children().indexOf(this.getSelected());

			while(true) {
				int newIndex = MathHelper.clamp(index + offset, 0, this.getEntryCount() - 1);
				if (index == newIndex) {
					break;
				}

				Entry entry = this.children().get(newIndex);
				if (predicate.test(entry)) {
					this.setSelected(entry);
					this.ensureVisible(entry);
					break;
				}

				index = newIndex;
			}
		}
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double)this.top && mouseY <= (double)this.bottom && mouseX >= (double)this.left && mouseX <= (double)this.right;
	}

	protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		IntListIterator bottomIter = entryBottoms.iterator();
		Iterator<Entry> entryIter = children.iterator();
		int relBottom = 0, relTop = 0;
		Entry entry = null;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		while (bottomIter.hasNext()) {
			relTop = relBottom;
			relBottom = bottomIter.nextInt();
			entry = entryIter.next();
			if (relBottom > scrollAmount) {
				break;
			}
		}

		Entry hoveredEntry = getEntryAtPosition(mouseX, mouseY);

		int rowWidth = getRowWidth();
		int rowLeft = this.left + this.width / 2 - rowWidth / 2;
		int rowRight = this.left + this.width / 2 + rowWidth / 2;
		while(true) {
			if (entry == null) {
				break;
			}

			int rowTop = relTop + getEntryAreaTop();
			if (this.renderSelection && getSelected() == entry) {
				int selectionHeight = (relTop - relBottom) - 4;
				RenderSystem.disableTexture();
				float f = this.isFocused() ? 1.0F : 0.5F;
				RenderSystem.color4f(f, f, f, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex(rowLeft, rowTop + selectionHeight + 2, 0.0D).next();
				bufferBuilder.vertex(rowRight, rowTop + selectionHeight + 2, 0.0D).next();
				bufferBuilder.vertex(rowRight, rowTop - 2, 0.0D).next();
				bufferBuilder.vertex(rowLeft, rowTop - 2, 0.0D).next();
				tessellator.draw();
				RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
				bufferBuilder.begin(7, VertexFormats.POSITION);
				bufferBuilder.vertex(rowLeft + 1, rowTop + selectionHeight + 1, 0.0D).next();
				bufferBuilder.vertex(rowRight - 1, rowTop + selectionHeight + 1, 0.0D).next();
				bufferBuilder.vertex(rowRight - 1, rowTop - 1, 0.0D).next();
				bufferBuilder.vertex(rowLeft + 1, rowTop - 1, 0.0D).next();
				tessellator.draw();
				RenderSystem.enableTexture();
			}

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

	public int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	public int getRowRight() {
		return this.getRowLeft() + this.getRowWidth();
	}

	protected int getEntryAreaTop() {
		return top + 4 - (int) scrollAmount + headerHeight;
	}

	protected int getRowTop(int index) {
		if (index == 0) {
			return getEntryAreaTop();
		}
		return getEntryAreaTop() + entryBottoms.getInt(index - 1);
	}

	private int getRowBottom(int index) {
		return getEntryAreaTop() + entryBottoms.getInt(index);
	}

	protected boolean isFocused() {
		return false;
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		Entry old = getFocused();
		if (old != null && old != focused) {
			old.unfocus();
		}
		super.setFocused(focused);
	}

	protected Entry removeEntry(int index) {
		Entry entry = this.children.get(index);
		return this.removeEntry(index, entry) ? entry : null;
	}

	protected boolean removeEntry(Entry entry) {
		return removeEntry(children.indexOf(entry), entry);
	}

	protected boolean removeEntry(int index, Entry entry) {
		boolean success = this.children.remove(entry);
		if (success) {
			entryBottoms.removeInt(index);
		}
		if (success && entry == this.getSelected()) {
			this.setSelected(null);
		}

		return success;
	}

	private void setEntryParentList(Entry entry) {
		entry.setParentList(this);
	}

	@Override
	public void tick() {
		for (Entry child : children()) {
			child.tick();
		}
	}

	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<Entry> {
		private final List<Entry> entries;

		private Entries() {
			this.entries = Lists.newArrayList();
		}

		public Entry get(int i) {
			return this.entries.get(i);
		}

		public int size() {
			return this.entries.size();
		}

		public Entry set(int i, Entry entry) {
			Entry entry2 = this.entries.set(i, entry);
			ConfigEntryListWidget.this.setEntryParentList(entry);
			return entry2;
		}

		public void add(int i, Entry entry) {
			this.entries.add(i, entry);
			ConfigEntryListWidget.this.setEntryParentList(entry);
		}

		public Entry remove(int i) {
			return this.entries.remove(i);
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends DrawableHelper implements Element, TickableElement {
		protected ConfigEntryListWidget parentList;

		protected void setParentList(ConfigEntryListWidget parentList) {
			this.parentList = parentList;
			widthChanged(parentList.rowWidth);
		}

		/**
		 * Renders an entry in a list.
		 *  @param matrices the matrix stack used for rendering
		 * @param x the X coordinate of the entry
		 * @param y the Y coordinate of the entry
		 * @param entryWidth The width of the entry
		 * @param entryHeight The height of the entry
		 * @param mouseX the X coordinate of the mouse
		 * @param mouseY the Y coordinate of the mouse
		 * @param hovered whether the mouse is hovering over the entry
		 */
		public abstract void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

		public boolean isMouseOver(double mouseX, double mouseY) {
			return Objects.equals(this.parentList.getEntryAtPosition(mouseX, mouseY), this);
		}

		public abstract int getHeight();

		public void widthChanged(int newWidth) {

		}

		public void unfocus() {

		}
	}

	@Environment(EnvType.CLIENT)
	public enum MoveDirection {
		UP, DOWN
	}
}
