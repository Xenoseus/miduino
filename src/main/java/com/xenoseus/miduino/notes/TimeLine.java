package com.xenoseus.miduino.notes;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Временная линия, объединяющая в себе ноты
 */
public class TimeLine {
    private static final Logger log = Logger.getLogger(TimeLine.class);
    private ArrayList<Note> notes;

    /**
     * Временная линия
     */
    public TimeLine() {
        notes = new ArrayList<>();
    }

    /**
     * Добавить ноту во временную линию
     * её позиция в ней будет равна величине tick
     * @param note нота
     */
    public void addNote(Note note) {
        notes.add(note);
    }

    /**
     * Отсортировать все ноты по их времени старта
     */
    public void sortLine() {
        notes.sort((first, second) -> {
            long firstTick = first.getTick();
            long secondTick = second.getTick();
            return Long.compare(firstTick, secondTick);
        });
    }

    /**
     * Вывести временную линию в красивом виде
     */
    public void print() {
        StringBuilder line = new StringBuilder();
        Note previousNote = notes.get(0);
        line.append(previousNote.getNoteMainInfo()).append(' ');
        for (int i = 1; i < notes.size(); i++) {
            Note note = notes.get(i);
            long previousStart = previousNote.getTick();
            long previousEnd = previousStart + previousNote.getDuration();
            long currentStart = note.getTick();
            if (currentStart < previousStart || currentStart >= previousEnd) {
                log.info(line.toString());
                line.setLength(0);
            }
            line.append(note.getNoteMainInfo()).append(' ');
            previousNote = note;
        }
    }

}