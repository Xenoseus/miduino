package com.xenoseus.miduino.raw;

import com.xenoseus.miduino.arduino.ICoder;
import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Линия времени для сырых событий (RawEvent)
 */
public class RawTimeLine implements ICoder {
	private static final String[] NOTES = {"C", "CS", "D", "DS", "E", "F", "FS", "G", "GS", "A", "AS", "B"};
	private TreeMap<Long, RawEvent> events;

	public RawTimeLine() {
		this.events = new TreeMap<>();
	}

	/**
	 * Получить сырую временную линию из обычных
	 * Для этого процесса порядок нот в таймлайнах не важен
	 * Поэтому сортировать таймлайны <b>не обязательно</b>
	 */
	public static RawTimeLine fromTimeLines(List<TimeLine> timeLines) {
		RawTimeLine ret = new RawTimeLine();
		for (TimeLine timeLine : timeLines) {
			for (Note note : timeLine.getNotes()) {
				int key = note.getKey();
				long tick = note.getTick();
				long tick2 = tick + note.getDuration();
				ret.addEvent(tick, new RawEvent(new RawEventTask(RawEventTask.TASK_ADD_NOTE, key)));
				ret.addEvent(tick2, new RawEvent(new RawEventTask(RawEventTask.TASK_REMOVE_NOTE, key)));
			}
		}
		return ret;
	}

	/**
	 * Добавить новое или дополнить уже существующее событие
	 * @param tick тик события
	 * @param event событие
	 */
	public void addEvent(long tick, RawEvent event) {
		Long longVal = Long.valueOf(tick);
		RawEvent founded = events.get(longVal);
		if (founded != null) {
			founded.append(event);
		} else {
			events.put(longVal, event);
		}
	}

	//====================CODER=======================

	/**
	 * Получить строку для команды beep()
	 * @param note название ноты
	 * @param duration длительность ноты
	 */
	private String getBeepCmd(String note, long duration) {
		return String.format("\tbeep(%s, %d);", note, (long) (duration));
	}

	/**
	 * Получить строку для команды beep2()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param duration длительность ноты
	 */
	private String getBeep2Cmd(String note, String note2, long duration) {
		return String.format("\tbeep2(%s, %s, %d);", note, note2, (long) (duration));
	}

	/**
	 * Получить строку для команды beep3()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param note3 название третье йноты
	 * @param duration длительность ноты
	 */
	private String getBeep3Cmd(String note, String note2, String note3, long duration) {
		return String.format("\tbeep3(%s, %s, %s, %d);", note, note2, note3, (long) (duration));
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
		return String.format("\tbeep4(%s, %s, %s, %s, %d);", note, note2, note3, note4, (long) (duration));
	}

	/**
	 * Получить строку для команды beep5()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param note3 название третьей ноты
	 * @param note4 название четвертой ноты
	 * @param note5 название пятой ноты
	 * @param duration длительность ноты
	 */
	private String getBeep5Cmd(String note, String note2, String note3, String note4, String note5, long duration) {
		return String.format("\tbeep5(%s, %s, %s, %s, %s, %d);", note, note2, note3, note4, note5, (long) (duration));
	}

	/**
	 * Получить строчку для команды delay()
	 * @param delay задержка
	 */
	private String getDelayCmd(long delay) {
		return String.format("\tdelay(%d);", (long) (delay));
	}

	/**
	 * Получить полную ноту на основании её полного идентификатора
	 * @param key идентификатор НОТЫ
	 */
	private String getNoteName(Integer key) {
		int val = key.intValue();
		int octave = val / 12;
		int note = val % 12;
		return String.format("%s%d",
				NOTES[note],
				octave
		);
	}

	/**
	 * Добавить команду в текст программы на основе окружения
	 * @param stringBuilder текст программы
	 * @param eventTick тик текущего события
	 * @param currentTick тик предыдущего события (добавляем команды прошлого события после того, как достигли нового)
	 * @param currentNotes текущие включенные ноты (прошлого события)
	 */
	private void appendCommand(StringBuilder stringBuilder, long eventTick, long currentTick, ArrayList<Integer> currentNotes) {
		int prevNotesSize = currentNotes.size();
		if (prevNotesSize == 0) {
			stringBuilder.append(getDelayCmd(eventTick - currentTick)).append("\n");
		} else {
			if (prevNotesSize == 1) {
				stringBuilder.append(getBeepCmd(getNoteName(currentNotes.get(0)), eventTick - currentTick)).append("\n");
			} else if (prevNotesSize == 2) {
				stringBuilder.append(getBeep2Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						eventTick - currentTick)).append("\n");
			} else if (prevNotesSize == 3) {
				stringBuilder.append(getBeep3Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						eventTick - currentTick)).append("\n");
			} else if (prevNotesSize == 4) {
				stringBuilder.append(getBeep4Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						getNoteName(currentNotes.get(3)),
						eventTick - currentTick)).append("\n");
			} else if (prevNotesSize == 5) {
				stringBuilder.append(getBeep5Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						getNoteName(currentNotes.get(3)),
						getNoteName(currentNotes.get(4)),
						eventTick - currentTick)).append("\n");
			} else {
				stringBuilder.append(getBeep5Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						getNoteName(currentNotes.get(3)),
						getNoteName(currentNotes.get(4)),
						eventTick - currentTick)).append("\n");
			}
		}
	}

	/**
	 * Получить код мелодии таймлайна
	 */
	@Override
	public String getCode() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("\nvoid %s() {\n", "song"));

		//список включенных нот на данном этапе
		ArrayList<Integer> currentNotes = new ArrayList<>();
		//текущий тик (с прошлого события)
		long currentTick = 0;
		for (Map.Entry<Long, RawEvent> entry : events.entrySet()) {
			long eventTick = entry.getKey().longValue();
			appendCommand(stringBuilder, eventTick, currentTick, currentNotes);
			currentTick = eventTick;
			RawEvent event = entry.getValue();
			for (RawEventTask task : event.getTasks()) {
				int type = task.getType();
				if (type == RawEventTask.TASK_ADD_NOTE) {
					currentNotes.add(Integer.valueOf(task.getNote()));
				} else if (type == RawEventTask.TASK_REMOVE_NOTE) {
					currentNotes.remove(Integer.valueOf(task.getNote()));
				}
			}
		}
		//для того, чтобы закрыть последний элемент верно (мы пишем ноты на следующем событии, чтобы знать длительность)
		appendCommand(stringBuilder, currentTick, currentTick, currentNotes);
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

}