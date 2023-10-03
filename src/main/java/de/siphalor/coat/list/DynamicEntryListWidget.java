package de.siphalor.coat.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import de.siphalor.coat.util.CoatColor;
import de.siphalor.coat.util.CoatUtil;
import de.siphalor.coat.util.TickableElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is a reimplementation of {@link net.minecraft.client.gui.widget.EntryListWidget} to enable variable item heights.
 */
@Environment(EnvType.CLIENT)
public class DynamicEntryListWidget<E extends DynamicEntryListWidget.Entry> extends AbstractParentElement implements Drawable, Selectable, EntryContainer, TickableElement {
	private static final int TOP_PADDING = 8;
	private static final int BOTTOM_PADDING = 6;
	private static final CoatColor SCROLLBAR_BACKGROUND_COLOR = CoatColor.rgb(0x000000);
	private static final CoatColor SCROLLBAR_HANDLE_SHADOW_COLOR = CoatColor.rgb(0x808080);
	private static final CoatColor SCROLLBAR_HANDLE_COLOR = CoatColor.rgb(0xC0C0C0);

	protected final MinecraftClient client;
	private final Entries entries = new Entries();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	private int rowWidth;
	/**
	 * The current scroll position - aka the vertical offset.
	 */
	@Getter
	private double scrollAmount;
	private float backgroundBrightness = 0.27F;
	/**
	 * The identifier for the background texture to use for this widget.
	 */
	@Getter
	private Identifier background = DrawableHelper.OPTIONS_BACKGROUND_TEXTURE;
	private boolean scrolling;

	/**
	 * Constructs a new instance. You can ignore this constructor safely under most circumstances.
	 *
	 * @param client   The {@link MinecraftClient} instance
	 * @param width    The width to take up
	 * @param height   The height to take up
	 * @param top      The top position
	 * @param rowWidth The maximum width of the contained entries
	 */
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

	/**
	 * Constructs a new instance. Typically used for config screens.
	 *
	 * @param client     The {@link MinecraftClient} instance
	 * @param entries    A collection of entries to be immediately added to this widget
	 * @param background An identifier referring to a background texture for this widget
	 */
	public DynamicEntryListWidget(MinecraftClient client, Collection<E> entries, Identifier background) {
		this.client = client;
		top = 20;
		addEntries(entries);
		this.background = background;
	}

	/**
	 * Gets the padding that's used on the left and right side of the entries.
	 *
	 * @return The padding for one side
	 */
	public int getHorizontalPadding() {
		return 4;
	}

	/**
	 * Sets the background texture of this widget.
	 *
	 * @param background An identifier pointing to the new background texture
	 */
	public void setBackground(Identifier background) {
		this.background = background;
	}

	/**
	 * Sets the brightness with which to render the background.
	 * The value must be between 0 (completely black) and 1 (normal image).
	 * The default value is <code>0.27F</code>
	 *
	 * @param backgroundBrightness The new background brightness
	 */
	public void setBackgroundBrightness(float backgroundBrightness) {
		this.backgroundBrightness = backgroundBrightness;
	}

	/**
	 * Gets actual width of the entries.
	 *
	 * @return The entry width
	 */
	public int getEntryWidth() {
		return Math.min(rowWidth, width) - getHorizontalPadding() * 2;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<E> entries() {
		return entries;
	}

	@Override
	public List<? extends Element> children() {
		return entries;
	}

	/**
	 * Gets an entry by its index.
	 *
	 * @param index The index to lookup the entry by
	 * @return The index at the specified index or <code>null</code> if no such entry exists
	 */
	public E getEntry(int index) {
		return entries.get(index);
	}

	/**
	 * Adds an entry to the end of this widget.
	 *
	 * @param entry A new entry
	 */
	public void addEntry(E entry) {
		entry.setParent(this);
		entries.add(entry);
	}

	/**
	 * Inserts an entry at the specified position in this widget.
	 *
	 * @param position The position to target
	 * @param entry    The entry to add
	 */
	public void addEntry(int position, E entry) {
		entry.setParent(this);
		entries.add(position, entry);
	}

	/**
	 * Adds a collection of entries at once to the end of this widget
	 *
	 * @param newEntries New entries to add
	 */
	public void addEntries(Collection<E> newEntries) {
		for (E newEntry : newEntries) {
			newEntry.setParent(this);
		}
		entries.addAll(newEntries);
	}

	/**
	 * Gets the current amount of entries in this widget.
	 *
	 * @return The count of entries
	 */
	protected int getEntryCount() {
		return this.entries().size();
	}

	/**
	 * Tries to find an entry at the given screen position.
	 *
	 * @param x The x position to target
	 * @param y The y position to target
	 * @return The entry at that position or <code>null</code> if there is no entry at that position
	 */
	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
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

	/**
	 * Finds the matching entry index for the given y coordinate
	 *
	 * @param y The y coordinate to look for
	 * @return The nearest entry index
	 */
	protected int getEntryAtY(int y) {
		y -= getEntryAreaTop();
		if (y < 0) {
			return 0;
		}
		if (y > getMaxEntryPosition()) {
			return getEntryCount();
		}
		IntListIterator iterator = entries.bottoms.iterator();
		while (iterator.hasNext()) {
			int cur = iterator.nextInt();
			if (y < cur) {
				return iterator.previousIndex();
			}
		}
		return getEntryCount() - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entryHeightChanged(Element element) {
		//noinspection SuspiciousMethodCalls
		int index = entries.indexOf(element);
		int bottom = index == 0 ? 0 : entries.bottoms.getInt(index - 1);
		for (int i = index, l = entries.size(); i < l; i++) {
			bottom += entries.get(i).getHeight();
			entries.bottoms.set(i, bottom);
		}
	}

	/**
	 * Should be called to resize this widget.
	 *
	 * @param newWidth  The new width
	 * @param newHeight The new height
	 */
	public void resize(int newWidth, int newHeight) {
		height = newHeight;
		bottom = top + height;
		widthChanged(newWidth);
	}

	/**
	 * Should be called to resize this widget vertically.
	 *
	 * @param newWidth The new width
	 */
	public void widthChanged(int newWidth) {
		width = newWidth;
		right = left + newWidth;

		for (Entry entry : entries) {
			entry.widthChanged(getEntryWidth());
		}
	}

	/**
	 * Repositions this widget to the given position.
	 *
	 * @param left The new x position
	 * @param top  The new y position
	 */
	public void setPosition(int left, int top) {
		this.left = left;
		this.top = top;
		this.right = left + width;
		this.bottom = top + height;
	}

	/**
	 * Sets the new maximum entry width.
	 *
	 * @param rowWidth The new width
	 */
	public void setRowWidth(int rowWidth) {
		this.rowWidth = rowWidth;
		widthChanged(width);
	}

	/**
	 * Gets the combined height of all entries.
	 *
	 * @return The height of all entries combined
	 */
	protected int getMaxEntryPosition() {
		if (entries.isEmpty()) {
			return 0;
		}
		return entries.bottoms.getInt(entries.bottoms.size() - 1);
	}

	/**
	 * Gets the maximum position that can be scrolled to.
	 *
	 * @return The absolute bottom of the scroll space
	 */
	protected int getMaxPosition() {
		return getMaxEntryPosition() + BOTTOM_PADDING;
	}

	/**
	 * Renders the background of this widget.
	 *
	 */
	protected void renderBackground() {
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_LEQUAL);

		int colorPart = (int) (backgroundBrightness * 255F);
		CoatColor color = CoatColor.rgb(colorPart, colorPart, colorPart);
		CoatUtil.drawTintedTexture(left, top, right, bottom, -100, background, 32F, (int) getScrollAmount(), color);
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int scrollbarXBegin = this.getScrollbarPositionX();
		int scrollbarXEnd = scrollbarXBegin + 6;

		renderBackground();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

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
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, top, scrollbarXEnd, bottom, SCROLLBAR_BACKGROUND_COLOR);
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, q, scrollbarXEnd, q + p, SCROLLBAR_HANDLE_SHADOW_COLOR);
			CoatUtil.addRect(bufferBuilder, scrollbarXBegin, q, scrollbarXEnd - 1, q + p - 1, SCROLLBAR_HANDLE_COLOR);
			tessellator.draw();
			RenderSystem.enableTexture();
		}

		this.renderList(matrices, mouseX, mouseY, delta);

		// render top shadow
		fillGradient(matrices, left, top, right, top + TOP_PADDING, 0xcc000000, 0x00000000);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderWidget(matrices, mouseX, mouseY, delta);
	}

	/**
	 * Scroll to the given entry so that it's directly in the center of the screen.
	 *
	 * @param entry The entry to scroll to
	 */
	public void centerScrollOn(E entry) {
		int index = entries.indexOf(entry);
		setScrollAmount(entries.bottoms.getInt(index) - entry.getHeight() / 2D - (bottom - top) / 2D);
	}

	/**
	 * Enforces that the given entry is visible by scrolling to it if necessary.
	 * Does nothing if the entry is already visible.
	 *
	 * @param entry The entry to make visible
	 */
	public void ensureVisible(E entry) {
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

	/**
	 * Scrolls by the given amount.
	 *
	 * @param amount The amount to scroll by
	 */
	private void scroll(int amount) {
		this.setScrollAmount(this.getScrollAmount() + amount);
	}

	/**
	 * Sets the scroll position.
	 *
	 * @param amount The new scroll position
	 */
	public void setScrollAmount(double amount) {
		this.scrollAmount = MathHelper.clamp(amount, 0.0D, this.getMaxScroll());
	}

	/**
	 * Gets the maximum scroll position.
	 *
	 * @return The highest allowed offset for scrolling
	 */
	public int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - height + TOP_PADDING);
	}

	protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() && mouseX < (double) (this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return getEntryRight() + CoatUtil.MARGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.updateScrollingState(mouseX, mouseY, button);
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		} else {
			Entry entry = getEntryAtPosition(mouseX, mouseY);
			if (entry != null) {
				if (entry.mouseClicked(mouseX, mouseY, button) && entry.getParent() == this) {
					setFocused(entry);
					setDragging(true);
					return true;
				}
			} else {
				setFocused(null);
			}

			return scrolling;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(mouseX, mouseY, button);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		Entry entry = getEntryAtPosition(mouseX, mouseY);
		if (entry != null && entry.mouseScrolled(mouseX, mouseY, amount)) {
			return true;
		}
		double prevScroll = getScrollAmount();
		this.setScrollAmount(this.getScrollAmount() - amount * 10.0D);
		return getScrollAmount() != prevScroll;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseY >= (double) this.top && mouseY <= (double) this.bottom && mouseX >= (double) this.left && mouseX <= (double) this.right;
	}

	/**
	 * Renders all visible entries.
	 *
	 * @param matrices The matrix stack to use for rendering
	 * @param mouseX   The current mouse x position
	 * @param mouseY   The current mouse y position
	 * @param delta    The tick delta
	 */
	protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		IntListIterator bottomIter = entries.bottoms.iterator();
		Iterator<E> entryIter = entries.iterator();
		int relBottom = 0, relTop = 0;
		final int entryAreaTop = getEntryAreaTop();
		E entry = null;

		while (bottomIter.hasNext()) {
			relTop = relBottom;
			relBottom = bottomIter.nextInt();
			entry = entryIter.next();
			if (entryAreaTop + relBottom > top) {
				break;
			}
		}

		E hoveredEntry = getEntryAtPosition(mouseX, mouseY);

		int rowWidth = getEntryWidth();
		int rowLeft = getEntryLeft();
		while (true) {
			if (entry == null) {
				break;
			}

			int rowTop = relTop + entryAreaTop;

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

	/**
	 * Gets the left position of the entries.
	 *
	 * @return The left position of the entries
	 */
	public int getEntryLeft() {
		return this.left + this.width / 2 - this.getEntryWidth() / 2;
	}

	/**
	 * Gets the right position of the entries.
	 *
	 * @return The right position of the entries
	 */
	public int getEntryRight() {
		return this.right - this.width / 2 + this.getEntryWidth() / 2;
	}

	/**
	 * Gets the absolute position where the entry area begins.
	 *
	 * @return The top position of the first entry
	 */
	protected int getEntryAreaTop() {
		return top + TOP_PADDING - (int) scrollAmount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocused(@Nullable Element focused) {
		Element old = getFocused();
		if (old != null && old != focused) {
			if (old instanceof Entry) {
				((Entry) old).focusLost();
			}
		}
		if (focused != null) {
			if (focused instanceof Entry) {
				//noinspection unchecked
				ensureVisible((E) focused);
			}
		}
		super.setFocused(focused);
	}

	/**
	 * Remove an entry
	 *
	 * @param entry The entry to remove
	 * @return The removed entry
	 */
	protected E removeEntry(E entry) {
		if (entry == getFocused()) {
			changeFocus(true);
		}
		entries.remove(entry);
		return entry;
	}

	/**
	 * Remove an entry by index
	 *
	 * @param index The index to remove an entry from
	 * @return The removed entry
	 */
	protected E removeEntry(int index) {
		E entry = entries.get(index);
		return removeEntry(entry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void tick() {
		for (E child : entries) {
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

	/**
	 * List class that represents the entries of an entry list widget
	 */
	@Environment(EnvType.CLIENT)
	class Entries extends AbstractList<E> {
		private final List<E> entries;
		/**
		 * A list containing all bottom positions of the entries
		 */
		protected final IntList bottoms = new IntArrayList();

		private Entries() {
			this.entries = Lists.newArrayList();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public E get(int i) {
			return this.entries.get(i);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.entries.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return entries.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public E set(int i, E entry) {
			entry.setParent(DynamicEntryListWidget.this);
			return this.entries.set(i, entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(int i, E entry) {
			bottoms.add(0);
			entry.setParent(DynamicEntryListWidget.this);
			entries.add(i, entry);
			entryHeightChanged(entry);
			entry.widthChanged(getEntryWidth());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(@NotNull Collection<? extends E> newEntries) {
			int oldSize = entries.size();
			int bottom = bottoms.size() == 0 ? 0 : bottoms.getInt(0);
			entries.addAll(newEntries);
			for (int i = oldSize, l = entries.size(); i < l; i++) {
				bottom += entries.get(i).getHeight();
				bottoms.add(bottom);
			}
			for (E newEntry : newEntries) {
				newEntry.setParent(DynamicEntryListWidget.this);
				newEntry.widthChanged(getEntryWidth());
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public E remove(int i) {
			E entry = entries.remove(i);
			entry.setParent(null);
			int height = entry.getHeight();
			bottoms.removeInt(i);
			if (entry == getFocused()) {
				setFocused(null);
			}
			for (int j = i; j < entries.size(); j++) {
				bottoms.set(j, bottoms.getInt(j) - height);
			}
			return entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			entries.clear();
			bottoms.clear();
		}
	}

	/**
	 * A dynamically sized entry in a {@link DynamicEntryListWidget}.
	 */
	@Environment(EnvType.CLIENT)
	public abstract static class Entry extends DrawableHelper implements Element, TickableElement {
		/**
		 * The parent element.
		 */
		protected EntryContainer parent;

		/**
		 * Gets the parent element of this entry.
		 *
		 * @return The current parent element
		 */
		public EntryContainer getParent() {
			return parent;
		}

		/**
		 * Sets the parent of this entry.
		 *
		 * @param parent The new parent element
		 */
		protected void setParent(EntryContainer parent) {
			this.parent = parent;
		}

		/**
		 * Renders an entry in a list.
		 *
		 * @param matrices    the matrix stack used for rendering
		 * @param x           the X coordinate of the entry
		 * @param y           the Y coordinate of the entry
		 * @param entryWidth  the width of the entry.
		 *                    Expensive calculations based on this should be done in {@link Entry#widthChanged(int)}.
		 * @param entryHeight The height of the entry
		 * @param mouseX      the X coordinate of the mouse
		 * @param mouseY      the Y coordinate of the mouse
		 * @param hovered     whether the mouse is hovering over the entry
		 */
		public abstract void render(MatrixStack matrices, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta);

		/**
		 * The current height of this entry. Height updates should be announced via the parent's {@link EntryContainer#entryHeightChanged(Element)}:<br />
		 * <code>getParent().entryHeightChanged(this)</code>
		 *
		 * @return The current height
		 */
		public abstract int getHeight();

		/**
		 * Called whenever the width of the parent changes.
		 *
		 * @param newWidth The new width
		 */
		public void widthChanged(int newWidth) {

		}

		/**
		 * Called when the focus on this element is lost. This can be used to clean up focuses of children.
		 */
		public void focusLost() {

		}
	}
}
