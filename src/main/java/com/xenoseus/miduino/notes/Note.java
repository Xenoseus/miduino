package com.xenoseus.miduino.notes;

/**
 * Самая обыкновенная нота
 */
public class Note {
	private static final String[] NOTES = {"C", "CS", "D", "DS", "E", "F", "FS", "G", "GS", "A", "AS", "B"};
	private int note;
	private int octave;
	private int key;
	private long tick;
	private int velocity;
	private long duration;

	/**
	 * Нота
	 *
	 * @param note     номер ноты
	 * @param octave   октава ноты
	 * @param tick     стартовая позиция ноты
	 * @param velocity громкость ноты
	 * @param key      полный ключ ноты
	 */
	public Note(int note, int octave, long tick, int velocity, int key) {
		this.note = note;
		this.octave = octave;
		this.tick = tick;
		this.velocity = velocity;
		this.duration = 0;
		this.key = key;
	}

	/**
	 * Установить длину ноты
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}

	/**
	 * Октава ноты
	 */
	public int getOctave() {
		return octave;
	}

	/**
	 * Длина ноты
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Нота
	 */
	public int getNote() {
		return note;
	}

	/**
	 * Стартовая позиция ноты
	 */
	public long getTick() {
		return tick;
	}

	@Override
	public String toString() {
		return String.format("[Note note=%s, octave=%d, tick=%d, duration=%d]",
				this.getNoteName(),
				this.octave,
				this.tick,
				this.duration);
	}

	/**
	 * Получить ноту в форматированном виде
	 */
	public String getNoteName() {
		return String.format("%s%d",
				NOTES[this.note],
				octave
		);
	}

	/**
	 * Получить главную информацию о ноте<br>
	 * Формат: <code>note (tick, duration)</code>
	 */
	public String getNoteMainInfo() {
		return String.format("%s (%d, %d)",
				this.getNoteName(),
				this.tick,
				this.duration
		);
	}

	/**
	 * Громкость ноты
	 */
	public int getVelocity() {
		return velocity;
	}

	/**
	 * Установить громкость ноты
	 */
	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	/**
	 * Получить уникальный ключ <b>ноты</b>
	 * идентифицирующий <b>ноту</b>, а не <b>объект ноты</b>
	 * фактически равно <code>(octave + 1) * 12 + note</code>
	 */
	public int getKey() {
		return key;
	}

}