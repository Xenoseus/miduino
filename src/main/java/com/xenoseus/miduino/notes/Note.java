package com.xenoseus.miduino.notes;

public class Note {
    private static final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    private int note;
    private int octave;
    private long tick;
    private int velocity;
    private long duration;

    /**
     * Нота
     * @param note номер ноты
     * @param octave октава ноты
     * @param tick стартовая позиция ноты
     * @param velocity громкость ноты
     */
    public Note(int note, int octave, long tick, int velocity) {
        this.note = note;
        this.octave = octave;
        this.tick = tick;
        this.velocity = velocity;
        this.duration = 0;
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
        return String.format("[Note note=%s, octave=%d, tick=%d, duration=%d]", NOTES[note], octave, tick, duration);
    }

    /**
     * Громкость ноты
     */
    public int getVelocity() {
        return velocity;
    }
}