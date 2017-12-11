package com.xenoseus.miduino.raw;

/**
 * Базовый класс для задач сырых событий
 */
public class RawEventTask {
	/**
	 * Задача добавления ноты в текущий список
	 */
	public static final int TASK_ADD_NOTE =     0;
	/**
	 * Задача удаления ноты из списка текущих
	 */
	public static final int TASK_REMOVE_NOTE =  1;
	private int type;
	private int note;
	private int velocity;

	/**
	 * Задача сырого события описывает либо добавление,
	 * либо удаление ноты из текущих играющих
	 * @param type тип события. См. {@link RawEventTask#TASK_ADD_NOTE} и {@link RawEventTask#TASK_REMOVE_NOTE}
	 * @param note нота
	 * @param velocity громкость ноты
	 */
	public RawEventTask(int type, int note, int velocity) {
		this.type = type;
		this.note = note;
		this.velocity = velocity;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		this.note = note;
	}

	public int getVelocity() {
		return this.velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}
}
