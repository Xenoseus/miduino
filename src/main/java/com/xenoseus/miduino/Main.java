package com.xenoseus.miduino;

import com.xenoseus.miduino.arduino.ArduinoTimeLine;
import com.xenoseus.miduino.arduino.Frequencies;
import com.xenoseus.miduino.notes.Note;
import com.xenoseus.miduino.notes.TimeLine;
import org.apache.log4j.Logger;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	private static final Logger log = Logger.getLogger(Main.class);
	private static final int NOTE_ON = 0x90;
	private static final int NOTE_OFF = 0x80;

	public static void main(String[] args) throws InvalidMidiDataException, IOException {
		log.info("starting");
		Sequence sequence = MidiSystem.getSequence(new File("intro.mid"));

		int trackNumber = 9;
		Note[] notes = new Note[300];
		TimeLine timeLine = new TimeLine();
		Track track = sequence.getTracks()[trackNumber];

			log.info("track " + trackNumber + "; size = " + track.size());

			for (int i = 0; i < track.size(); i++) {
				MidiEvent midiEvent = track.get(i);
				MidiMessage midiMessage = midiEvent.getMessage();
				if (midiMessage instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) midiMessage;
					//log.info("Channel: " + sm.getChannel() + " ");
					if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
						int key = sm.getData1();
						int octave = key / 12;
						int note = key % 12;
						int velocity = sm.getData2();
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
								notes[key].setDuration(midiEvent.getTick() - notes[key].getTick());
								//log.info("note ends: " + notes[key]);

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
				} else {
					log.warn(String.format("unknown message: %s at tick: %d",
							midiMessage.getClass(),
							midiEvent.getTick()
					));
				}
			}

		//сортируем и парсим линию на каналы
		timeLine.sortLine();
		ArrayList<TimeLine> parsedTimeLines = timeLine.channelsToTimeLine(timeLine.parseChannels());

		//конструируем код для первого канала
		StringBuilder finalCode = new StringBuilder();

		Frequencies frequencies = new Frequencies(0);
		finalCode.append(frequencies.getCode());

		ArduinoTimeLine arduinoTimeLine = new ArduinoTimeLine(parsedTimeLines.get(0), "song", 1.0f);
		finalCode.append(arduinoTimeLine.getCode());

		log.info(finalCode.toString());
	}
}