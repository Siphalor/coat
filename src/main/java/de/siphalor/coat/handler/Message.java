package de.siphalor.coat.handler;

import de.siphalor.coat.Coat;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class Message {
	private final Level level;
	private final BaseText text;

	public Message(Level level, BaseText text) {
		this.level = level;
		text.setStyle(level.getTextStyle());
		this.text = text;
	}

	public Level getLevel() {
		return level;
	}

	public BaseText getText() {
		return text;
	}

	public static class Level {
		public static final Level NOTE = new Level(0, Coat.MOD_ID + ".message.level.note", Style.EMPTY);
		public static final Level INFO = new Level(100, Coat.MOD_ID + ".message.level.info", Style.EMPTY.withColor(TextColor.fromRgb(0xaaaaff)));
		public static final Level WARNING = new Level(200, Coat.MOD_ID + ".message.level.warning", Style.EMPTY.withColor(Formatting.YELLOW));
		public static final Level ERROR = new Level(300, Coat.MOD_ID + ".message.level.error", Style.EMPTY.withColor(Formatting.RED));

		public static final int DISPLAY_THRESHOLD = 150;

		private final int severity;
		private final String translationKey;
		private final Style formatting;

		public Level(int severity, String translationKey, Style formatting) {
			this.severity = severity;
			this.translationKey = translationKey;
			this.formatting = formatting;
		}

		public int getSeverity() {
			return severity;
		}

		public String getTranslationKey() {
			return translationKey;
		}

		public Style getTextStyle() {
			return formatting;
		}
	}
}
