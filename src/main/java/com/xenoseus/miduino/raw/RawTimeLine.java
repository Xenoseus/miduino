package com.xenoseus.miduino.raw;

import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;

import java.util.List;
import java.util.TreeMap;

/**
 * Линия времени для сырых событий (RawEvent)
 */
public class RawTimeLine {
	private TreeMap<Long, RawEvent> events;

	public RawTimeLine() {
		this.events = new TreeMap<Long, RawEvent>();
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

}