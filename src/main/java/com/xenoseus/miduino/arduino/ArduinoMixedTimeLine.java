package com.xenoseus.miduino.arduino;

import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;

import java.util.ArrayList;

/**
 * Генератор функции для соединенных таймлайнов
 * Учитывает все пересечения таймлайнов и генерирует функцию
 */
public class ArduinoMixedTimeLine implements ICoder {
	private final ArrayList<TimeLine> timeLines;
	private final String functionName;
	private final float delayCoeff;

	/**
	 * @param timeLines таймлайн для генерации
	 * @param functionName название функции для сгенерированного кода
	 * @param delayCoeff коэффициент длительности нот и задержек
	 */
	public ArduinoMixedTimeLine(ArrayList<TimeLine> timeLines, String functionName, float delayCoeff) {
		this.timeLines = timeLines;
		this.functionName = functionName;
		this.delayCoeff = delayCoeff;
	}

	/**
	 * Получить строку для команды beep()
	 * @param note название ноты
	 * @param duration длительность ноты
	 */
	private String getBeepCmd(String note, long duration) {
		return String.format("\tbeep(%s, %d);", note, (long) (duration * delayCoeff));
	}

	/**
	 * Получить строку для команды beep2()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param duration длительность ноты
	 */
	private String getBeep2Cmd(String note, String note2, long duration) {
		return String.format("\tbeep2(%s, %s, %d);", note, note2, (long) (duration * delayCoeff));
	}

	/**
	 * Получить строку для команды beep3()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param note3 название третье йноты
	 * @param duration длительность ноты
	 */
	private String getBeep3Cmd(String note, String note2, String note3, long duration) {
		return String.format("\tbeep3(%s, %s, %s, %d);", note, note2, note3, (long) (duration * delayCoeff));
	}

	/**
	 * Получить строку для команды beep4()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param note3 название третьей ноты
	 * @param note4 название четвертой ноты
	 * @param duration длительность ноты
	 */
	private String getBeep4Cmd(String note, String note2, String note3, String note4, long duration) {
		return String.format("\tbeep3(%s, %s, %s, %s, %d);", note, note2, note3, note4, (long) (duration * delayCoeff));
	}

	/**
	 * Получить строчку для команды delay()
	 * @param delay задержка
	 */
	private String getDelayCmd(long delay) {
		return String.format("\tdelay(%d);", (long) (delay * delayCoeff));
	}

	/**
	 * Получить код мелодии таймлайна
	 */
	@Override
	public String getCode() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("\nvoid %s() {\n", this.functionName));
		ArrayList<Note> notes = timeLines.getNotes();
		long currentTick = 0;
		for (Note note : notes) {
			long noteTick = note.getTick();
			long noteDuration = note.getDuration();
			if (noteTick > currentTick) {
				stringBuilder.append(getDelayCmd(noteTick - currentTick)).append("\n");
			}
			stringBuilder.append(getBeepCmd(note.getNoteName(), noteDuration)).append("\n");
			currentTick = noteTick + noteDuration;
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}