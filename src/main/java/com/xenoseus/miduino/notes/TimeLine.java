package com.xenoseus.miduino.notes;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Временная линия, объединяющая в себе ноты
 */
public class TimeLine {
	private static final int NOTE_NOT_EXISTS = 0;
	private static final int NOTE_EXISTS = 1;
	private static final int NOTE_EXISTS_AND_INTERSECTS = 2;
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
	 *
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
	 * Вывести список нот по порядку их нахождения в списке
	 */
	public void rawPrint() {
		for (Note note : notes) {
			log.info(note);
		}
	}

	/**
	 * Вывести временную линию в красивом виде
	 */
	public void print() {
		ArrayList<ArrayList<Note>> list = parseChannels();
		StringBuilder stringBuilder = new StringBuilder();
		for (ArrayList<Note> line : list) {
			for (Note note : line) {
				stringBuilder.append(note.getNoteMainInfo()).append(' ');
			}
			log.info(stringBuilder.toString());
			stringBuilder.setLength(0);
		}
	}

	/**
	 * Пропарсить таймлайн в несколько каналов
	 * на основании пересечения нот
	 * <i>чтобы распараллелить ноты</i><br>
	 */
	public ArrayList<ArrayList<Note>> parseChannels() {
		//Таймлайн
		ArrayList<ArrayList<Note>> ret = new ArrayList<>();
		ArrayList<Note> line = new ArrayList<>();
		for (Note note : notes) {
			int existance = checkNoteExistance(line, note);
			if (existance == NOTE_NOT_EXISTS) {
				if (line.size() == 0 || listIntersection(line, note)) {
					//нота не присутствует в списке и пересекается с текущими/список пуст
					//добавляем как отдельный "канал"
					line.add(note);
				} else {
					//нота не присутствует в списке, но и не пересекается с текущими
					//следовательно, это уже новый этап по временной линии (по Х). пересоздаем "этап"
					ret.add(line);
					line = new ArrayList<>();
					line.add(note);
				}
			} else if (existance == NOTE_EXISTS) {
				//нота присутствует в текущем этапе, но НЕ пересекается с другой такой же
				//на всякий случай нужно проверить, не переесекается ли она с другими нотами
				if (listIntersection(line, note)) {
					//нота пересекается по времени. значит, добавляем как новый канал этого "этапа"
					line.add(note);
				} else {
					//не пересекается.. создаем новый "этап", все нормально
					ret.add(line);
					line = new ArrayList<>();
					line.add(note);
				}
			} else if (existance == NOTE_EXISTS_AND_INTERSECTS) {
				//ох, самый запущенный случай. нота присутствует в текущем этапе и пересекается с какой-то другой
				//но ведь фактически это очень просто решить. удлиняем левую ноту
				Note otherNote = getSameNoteWithIntersection(line, note);
				if (note.getTick() < otherNote.getTick()) {
					note.setDuration(otherNote.getTick() + otherNote.getDuration() - note.getTick());
				} else {
					otherNote.setDuration(note.getTick() + note.getDuration() - otherNote.getTick());
				}
			}
		}
		return ret;
	}

	/**
	 * Это расплата за красивость вывода в <b>checkNoteExistance</b>
	 * Да, мы точно знаем, что такая нота есть в списке (пересекается, равна по значению ноты)
	 * И эта функция позволит получить её. Все просто
	 */
	private Note getSameNoteWithIntersection(ArrayList<Note> list, Note note) {
		int key = note.getKey();
		for (Note otherNote : list) {
			if (otherNote.getKey() == key && intersects(otherNote, note)) return otherNote;
		}
		log.warn("TimeLine::getSameNoteWithIntersection returns null.. как так?");
		return null;
	}

	/**
	 * Проверить наличие ноты в данном списке
	 * @return <ul>
	 *          <li>{@link #NOTE_EXISTS_AND_INTERSECTS} если нота присутствует и пересекается с другой такой же</li>
	 *          <li>{@link #NOTE_EXISTS} если нота только присутствует</li>
	 *          <li>{@link #NOTE_NOT_EXISTS} если нота отсутствует</li>
	 *         </ul>
	 */
	private int checkNoteExistance(ArrayList<Note> list, Note note) {
		int key = note.getKey();
		for (Note otherNote : list) {
			if (otherNote.getKey() == key) {
				return intersects(otherNote, note) ? NOTE_EXISTS_AND_INTERSECTS : NOTE_EXISTS;
			}
		}
		return NOTE_NOT_EXISTS;
	}

	/**
	 * Проверка на пересечение ноты с любой из данного списка
	 * @return true если нота пересекается по времени и false в ином случае
	 */
	private boolean listIntersection(ArrayList<Note> list, Note note) {
		for (Note otherNode : list) {
			if (intersects(otherNode, note)) return true;
		}
		return false;
	}

	/**
	 * Пересекаются ли ноты во временном интервале
	 */
	private boolean intersects(Note first, Note second) {
		long firstStart = first.getTick();
		long secondStart = second.getTick();
		long firstEnd = firstStart + first.getDuration();
		long secondEnd = secondStart + second.getDuration();
		return Math.max(firstEnd, secondEnd) - Math.min(firstStart, secondStart) <
				(firstEnd - firstStart) + (secondEnd - secondStart) - 1;
	}

}