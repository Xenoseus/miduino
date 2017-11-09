package com.xenoseus.miduino.arduino;

import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;

import java.util.ArrayList;

/**
 * Генератор функции для одного отдельного таймлайна.
 * Учитывает все особенности данного таймлайна и генерирует функцию
 */
public class ArduinoTimeLine implements ICoder {
	private final TimeLine timeLine;
	private final String functionName;
	private final float timeCoefficient;

	/**
	 * @param timeLine таймлайн для генерации
	 * @param functionName название функции для сгенерированного кода
	 * @param timeCoefficient коэффициент длительности нот и задержек
	 */
	public ArduinoTimeLine(TimeLine timeLine, String functionName, float timeCoefficient) {
		this.timeLine = timeLine;
		this.functionName = functionName;
		this.timeCoefficient = timeCoefficient;
	}

	/**
	 * Получить строку для команды beep()
	 * @param note название ноты
	 * @param duration длительность ноты
	 */
	private String getBeepCmd(String note, long duration) {
		return String.format("\tbeep(%s, %d);", note, (long) (duration * timeCoefficient));
	}

	/**
	 * Получить строчку для команды delay()
	 * @param delay задержка
	 */
	private String getDelayCmd(long delay) {
		return String.format("\tdelay(%d);", (long) (delay * timeCoefficient));
	}

	/**
	 * Получить код мелодии таймлайна
	 */
	@Override
	public String getCode() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("\nvoid %s() {\n", this.functionName));
		ArrayList<Note> notes = timeLine.getNotes();
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