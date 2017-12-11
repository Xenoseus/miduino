package com.xenoseus.miduino;

import com.xenoseus.miduino.arduino.Frequencies;
import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.NoteUtils;
import com.xenoseus.miduino.notes.TimeLine;
import com.xenoseus.miduino.raw.RawTimeLine;
import org.apache.log4j.Logger;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	//установка bpm. обозначает количество микросекунд на бит
	//по умолчанию 500.000, в минуте 60.000.000 микросекунд [MetaMessage]
	private static final int SET_TEMPO = 0x51;
	//событие конца трека [MetaMessage]
	private static final int END_OF_TRACK = 0x2F;
	//событие названия трека [MetaMessage]
	private static final int TRACK_NAME = 0x03;
	//событие начала ноты (в некоторых *.mid обозначает окончание, если громкость = 0)
	private static final int NOTE_ON = 0x90;
	//событие окончания ноты
	private static final int NOTE_OFF = 0x80;

	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		//входные параметры
		String fileName = "test.mid";
		int[] tracksToParse = new int[] {0};


		log.info("starting");
		Sequence sequence = MidiSystem.getSequence(new File(fileName));

		int minVelocity = Integer.MAX_VALUE;
		int maxVelocity = Integer.MIN_VALUE;

		ArrayList<TimeLine> timeLines = new ArrayList<>();

		int trackNumber = 0;
		for (Track track : sequence.getTracks()) {
			boolean doParse = false;
			for (int x : tracksToParse) {
				if (x == trackNumber) {
					doParse = true;
					break;
				}
			}

			if (doParse) {
				float currentTimeCoefficient = 1.0f;
				Note[] notes = new Note[300];
				TimeLine timeLine = new TimeLine();

				log.info("track " + trackNumber + "; size = " + track.size());

				for (int i = 0; i < track.size(); i++) {
					MidiEvent midiEvent = track.get(i);
					MidiMessage midiMessage = midiEvent.getMessage();
					if (midiMessage instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) midiMessage;
						if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
							int key = sm.getData1();
							int octave = key / 12;
							int note = key % 12;
							int velocity = sm.getData2();
							if (velocity < minVelocity) minVelocity = velocity;
							if (velocity > maxVelocity) maxVelocity = velocity;
							if ((velocity != 0) && (sm.getCommand() != NOTE_OFF)) {
								//note start
								if (notes[key] != null) {
									log.warn("notes[key] not null, but new started: " + notes[key]);
								}
								notes[key] = new Note(note, octave, midiEvent.getTick(), velocity, key);
							} else {
								//note end
								if (notes[key] == null) {
									log.warn("notes[key] is null, but note ends, key = " + key);
								} else {
									notes[key].setDuration((int) ((midiEvent.getTick() - notes[key].getTick())
											* currentTimeCoefficient));

									timeLine.addNote(notes[key]);
									notes[key] = null;
								}
							}
						} else {
							log.warn(String.format("unknown command: %s at tick %d",
									sm.getCommand(),
									midiEvent.getTick()
							));
						}
					} else if (midiMessage instanceof MetaMessage) {
						MetaMessage metaMessage = (MetaMessage) midiMessage;
						if (metaMessage.getType() == SET_TEMPO) {
							long tempo = getLongFromBytes(metaMessage.getMessage(), 3);
							currentTimeCoefficient = NoteUtils.BPMToTimeCoefficient(NoteUtils.TempoToBPM(tempo));
						} else if (metaMessage.getType() == END_OF_TRACK) {
							log.info(String.format("end of track in meta message at tick %d", midiEvent.getTick()));
						} else if (metaMessage.getType() == TRACK_NAME) {
							log.info(String.format("track name: %s", new String(metaMessage.getMessage())));
						} else {
							log.warn(String.format(
									"unknown meta message %d at tick: %d",
									metaMessage.getType(),
									midiEvent.getTick()
							));
						}
					} else if (midiMessage.getClass().getName().contains("Track$ImmutableEndOfTrack")) {
						log.info(String.format("end of track message at tick %d", midiEvent.getTick()));
					} else {
						log.warn(String.format("unknown message: %s at tick: %d",
								midiMessage.getClass(),
								midiEvent.getTick()
						));
					}
				}
				timeLines.add(timeLine);
			}
			trackNumber++;
		}

		RawTimeLine rawTimeLine = RawTimeLine.fromTimeLines(timeLines);
		rawTimeLine.adjustVelocity(minVelocity, maxVelocity);

		StringBuilder finalCode = new StringBuilder();

		Frequencies frequencies = new Frequencies();
		finalCode.append(frequencies.getCode());

		finalCode.append(rawTimeLine.getCode());

		log.info(finalCode.toString());
	}

	/**
	 * Вспомогательная функция для сообщения SET_TEMPO.<br>
	 * Достает из массива байт подмассив: <code>[startByte; data.length)</code>
	 * и превращает его в число
	 * @param data массив данных
	 * @param startByte начальный индекс подмассива в массиве
	 * @return итоговое число
	 */
	private static long getLongFromBytes(byte[] data, int startByte) {
		long value = 0;
		for (int i = data.length - 1; i >= startByte; i--) {
			value |= (value << 8) + (data[data.length - (i - startByte) - 1] & 0xff);
		}
		return value;
	}
}