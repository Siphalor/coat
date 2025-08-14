package de.siphalor.coat.handler;

import de.siphalor.coat.Coat;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.Nullable;

/**
 * Defines an information message to be shown to the user.
 */
@Getter
public class Message {
	/**
	 * The severity level of this message.
	 */
	private final Level level;
	/**
	 * The text of this message.
	 */
	private final MutableComponent text;
	/**
	 * The origin where this message originates from.
	 * Typically a {@link net.minecraft.client.gui.components.events.GuiEventListener}.
	 * <code>null</code> if unknown or not applicable.
	 */
	@Setter
	private @Nullable Object origin;

	public Message(Level level, MutableComponent text) {
		this.level = level;
		text.setStyle(level.getTextStyle());
		this.text = text;
	}

	/**
	 * Definition of a message severity level.
	 */
	public static class Level {
		/**
		 * Defines a low priority informational message level.
		 */
		public static final Level NOTE = new Level(0, Coat.MOD_ID + ".message.level.note", Style.EMPTY);
		/**
		 * Defines an informational message level.
		 */
		public static final Level INFO = new Level(100, Coat.MOD_ID + ".message.level.info", Style.EMPTY.withColor(TextColor.fromRgb(0xaaaaff)));
		/**
		 * Defines a warning message level.
		 */
		public static final Level WARNING = new Level(200, Coat.MOD_ID + ".message.level.warning", Style.EMPTY.withColor(ChatFormatting.YELLOW));
		/**
		 * Defines an error message level.
		 */
		public static final Level ERROR = new Level(300, Coat.MOD_ID + ".message.level.error", Style.EMPTY.withColor(ChatFormatting.RED));

		/**
		 * A threshold of levels that should always be shown to the user. To be compared with {@link Level#severity}
		 */
		public static final int DISPLAY_THRESHOLD = 150;

		/**
		 * An internal representation of the severity. Usable for comparison of levels.
		 */
		@Getter
		private final int severity;
		/**
		 * A translation key which refers to a message that describes this level appropriately.
		 */
		@Getter
		private final String translationKey;
		/**
		 * Describes how messages of this level should be formatted.
		 */
		private final Style formatting;

		public Level(int severity, String translationKey, Style formatting) {
			this.severity = severity;
			this.translationKey = translationKey;
			this.formatting = formatting;
		}

		/**
		 * Gets how messages of this level should be formatted.
		 *
		 * @return The style to apply
		 */
		public Style getTextStyle() {
			return formatting;
		}
	}
}
