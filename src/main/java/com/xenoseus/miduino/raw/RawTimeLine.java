package com.xenoseus.miduino.raw;

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