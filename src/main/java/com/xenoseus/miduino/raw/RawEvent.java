package com.xenoseus.miduino.raw;

import java.util.ArrayList;
import java.util.List;

/**
 * Событие в теле сырой временной линии
 */
public class RawEvent {
	private List<RawEventTask> tasks;

	public RawEvent(RawEventTask task) {
		this.tasks = new ArrayList<>();
		this.tasks.add(task);
	}

	/**
	 * Добавить задачу
	 * @param task
	 */
	public void addTask(RawEventTask task) {
		this.tasks.add(task);
	}

	/**
	 * Получить список задач события
	 * @return
	 */
	public List<RawEventTask> getTasks() {
		return this.tasks;
	}

	/**
	 * Добавить все задачи другого события в это
	 * @param otherEvent другое событие
	 */
	public void append(RawEvent otherEvent) {
		tasks.addAll(otherEvent.getTasks());
	}

}
