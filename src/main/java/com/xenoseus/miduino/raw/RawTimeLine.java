package com.xenoseus.miduino.raw;

import com.xenoseus.miduino.MathUtils;
import com.xenoseus.miduino.arduino.Frequencies;
import com.xenoseus.miduino.arduino.ICoder;
import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;

import java.util.*;

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
				int velocity = note.getVelocity();
				ret.addEvent(tick, new RawEvent(new RawEventTask(RawEventTask.TASK_ADD_NOTE, key, velocity)));
				ret.addEvent(tick2, new RawEvent(new RawEventTask(RawEventTask.TASK_REMOVE_NOTE, key, 0)));
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
	private Set<Long> usedNoteIntervals = new LinkedHashSet<>();

	/**
	 * Получить строку для команды beep()
	 * @param note название ноты
	 * @param duration длительность ноты
	 */
	private String getBeepCmd(String note, long duration, int velocity) {
		//velocity = bit mask. when bit = 1, sound is bigger. when bit = 0, sound is smaller
		return String.format("\tbeep(%s, %d, %d);", note, duration, velocity);
	}

	/**
	 * Получить строку для команды beep2()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param duration длительность ноты
	 */
	private String getBeep2Cmd(String note, String note2, long duration, int velocity) {
		return String.format("\tbeep2(%s, %s, %d, %d);", note, note2, duration, velocity);
	}

	/**
	 * Получить строку для команды beep3()
	 * @param note название ноты
	 * @param note2 название второй ноты
	 * @param note3 название третье йноты
	 * @param duration длительность ноты
	 */
	private String getBeep3Cmd(String note, String note2, String note3, long duration, int velocity) {
		return String.format("\tbeep3(%s, %s, %s, %d, %d);", note, note2, note3, duration, velocity);
	}

	//sorry, but arduino can't correctly play 4 or 5 sound at same time

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
		usedNoteIntervals.add(Frequencies.getNoteInterval(note, octave));
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
	private void appendCommand(StringBuilder stringBuilder, long eventTick, long currentTick,
	                           ArrayList<Integer> currentNotes, int[] currentVelocities) {
		int prevNotesSize = currentNotes.size();
		if (prevNotesSize == 0) {
			stringBuilder.append(getDelayCmd(eventTick - currentTick)).append("\n");
		} else {
			if (prevNotesSize == 1) {
				int velocity = 0;
				if (currentVelocities[currentNotes.get(0)] == 1) velocity += 1;
				stringBuilder.append(getBeepCmd(getNoteName(currentNotes.get(0)), eventTick - currentTick, velocity)).append("\n");
			} else if (prevNotesSize == 2) {
				int velocity = 0;
				if (currentVelocities[currentNotes.get(0)] == 1) velocity += 1;
				if (currentVelocities[currentNotes.get(1)] == 1) velocity += 2;
				stringBuilder.append(getBeep2Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						eventTick - currentTick,
						velocity)).append("\n");
			} else if (prevNotesSize == 3) {
				int velocity = 0;
				if (currentVelocities[currentNotes.get(0)] == 1) velocity += 1;
				if (currentVelocities[currentNotes.get(1)] == 1) velocity += 2;
				if (currentVelocities[currentNotes.get(2)] == 1) velocity += 4;
				stringBuilder.append(getBeep3Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						eventTick - currentTick,
						velocity)).append("\n");
			} else {
				int velocity = 0;
				if (currentVelocities[currentNotes.get(0)] == 1) velocity += 1;
				if (currentVelocities[currentNotes.get(1)] == 1) velocity += 2;
				if (currentVelocities[currentNotes.get(2)] == 1) velocity += 4;
				stringBuilder.append(getBeep3Cmd(getNoteName(currentNotes.get(0)),
						getNoteName(currentNotes.get(1)),
						getNoteName(currentNotes.get(2)),
						eventTick - currentTick,
						velocity)).append("\n");
			}
		}
	}

	/**
	 * Получить базовый интервал для всех нот (наименьшее общее кратное)
	 */
	public long getBaseInterval() {
		long[] vals = new long[usedNoteIntervals.size()];
		int i = 0;
		for (Long longVal : usedNoteIntervals) {
			vals[i] = longVal.longValue();
			i++;
		}
		return MathUtils.greatestCommonDivisor(vals);
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
		//список громкостей нот на данном этапе
		int[] currentVelocities = new int[300];
		//текущий тик (с прошлого события)
		long currentTick = 0;
		for (Map.Entry<Long, RawEvent> entry : events.entrySet()) {
			long eventTick = entry.getKey().longValue();
			appendCommand(stringBuilder, eventTick, currentTick, currentNotes, currentVelocities);
			currentTick = eventTick;
			RawEvent event = entry.getValue();
			for (RawEventTask task : event.getTasks()) {
				int type = task.getType();
				if (type == RawEventTask.TASK_ADD_NOTE) {
					currentNotes.add(Integer.valueOf(task.getNote()));
					currentVelocities[task.getNote()] = task.getVelocity();
				} else if (type == RawEventTask.TASK_REMOVE_NOTE) {
					currentNotes.remove(Integer.valueOf(task.getNote()));
				}
			}
		}
		//для того, чтобы закрыть последний элемент верно (мы пишем ноты на следующем событии, чтобы знать длительность)
		appendCommand(stringBuilder, currentTick, currentTick, currentNotes, currentVelocities);
		stringBuilder.append("}\n");
		stringBuilder.append("#define BASE_INTERVAL ").append(getBaseInterval());
		return stringBuilder.toString();
	}

	/**
	 * Округлить все значения громкостей нот до минимального и максимального
	 * @param minVelocity минимальное
	 * @param maxVelocity максимальное
	 */
	public void adjustVelocity(int minVelocity, int maxVelocity) {
		int midVelocity = (minVelocity + maxVelocity) / 2;
		Set<Map.Entry<Long, RawEvent>> entrySet = events.entrySet();
		for (Map.Entry<Long, RawEvent> entry : entrySet) {
			RawEvent event = entry.getValue();
			List<RawEventTask> tasks = event.getTasks();
			for (RawEventTask task : tasks) {
				if (task.getType() == RawEventTask.TASK_ADD_NOTE) {
					task.setVelocity((task.getVelocity() >= midVelocity) ? 1 : 0);
				}
			}
		}
	}
}