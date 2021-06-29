package de.siphalor.coat.handler;

import de.siphalor.coat.Coat;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

/**
 * Defines an information message to be shown to the user.
 */
public class Message {
	/**
	 * The severity level of this message.
	 */
	private final Level level;
	/**
	 * The text of this message.
	 */
	private final BaseText text;
	/**
	 * An object that identifies where this messages originates from.
	 */
	private Object origin;

	public Message(Level level, BaseText text) {
		this.level = level;
		text.setStyle(level.getTextStyle());
		this.text = text;
	}

	/**
	 * Gets the severity level of this message.
	 *
	 * @return The severity level
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Gets the text associated with this message.
	 *
	 * @return The text
	 */
	public BaseText getText() {
		return text;
	}

	/**
	 * Gets where this message originates from. Typically a GUI {@link net.minecraft.client.gui.Element}.
	 * <code>null</code> if unknown or not applicable.
	 *
	 * @return An arbitrary object
	 */
	public Object getOrigin() {
		return origin;
	}

	/**
	 * Sets the object where this message originates from.
	 *
	 * @param origin The origin to set
	 */
	public void setOrigin(Object origin) {
		this.origin = origin;
	}

	/**
	 * Definition of a message severity level.
	 */
	public static class Level {
		/**
		 * Defines a low priority informational message level.
		 */
		public static final Level NOTE = new Level(0, Coat.MOD_ID + ".message.level.note", new Style());
		/**
		 * Defines an informational message level.
		 */
		public static final Level INFO = new Level(100, Coat.MOD_ID + ".message.level.info", new Style().setColor(Formatting.byColorIndex(0xaaaaff)));
		/**
		 * Defines a warning message level.
		 */
		public static final Level WARNING = new Level(200, Coat.MOD_ID + ".message.level.warning", new Style().setColor(Formatting.YELLOW));
		/**
		 * Defines an error message level.
		 */
		public static final Level ERROR = new Level(300, Coat.MOD_ID + ".message.level.error", new Style().setColor(Formatting.RED));

		/**
		 * A threshold of levels that should always be shown to the user. To be compared with {@link Level#severity}
		 */
		public static final int DISPLAY_THRESHOLD = 150;

		/**
		 * An internal representation of the severity. Usable for comparison of levels.
		 */
		private final int severity;
		/**
		 * A translation key which refers to a message that describes this level appropriately.
		 */
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
		 * The internal representation of the severity.
		 *
		 * @return The severity
		 */
		public int getSeverity() {
			return severity;
		}

		/**
		 * Gets a translation key for an appropriate translation of this level.
		 *
		 * @return The translation key
		 */
		public String getTranslationKey() {
			return translationKey;
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
