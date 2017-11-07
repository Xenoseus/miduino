package com.xenoseus.miduino.arduino;

/**
 * Генератор дефайнов с частотами
 */
public class Frequencies implements ICoder {
	private static final String[] NOTES = {"C", "CS", "D", "DS", "E", "F", "FS", "G", "GS", "A", "AS", "B"};
	private static final int OCTAVES_NUM = 11;
	/**
	 * Массив частот. Внешний - массив нот,
	 * внутренние - массивы частот этих нот по октавам
	 */
	private static final double[][] frequencies = {
			{16.35, 32.70, 65.41, 130.82, 261.63, 523.25, 1046.5, 2093, 4186, 8372, 16744},
			{17.32, 34.65, 69.30, 138.59, 277.18, 554.36, 1108.7, 2217.4, 4434.8, 8869.6, 17739.2},
			{18.35, 36.95, 73.91, 147.83, 293.33, 587.32, 1174.6, 2349.2, 4698.4, 9396.8, 18793.6},
			{19.40, 38.88, 77.78, 155.56, 311.13, 622.26, 1244.5, 2489, 4978, 9956, 19912},
			{20.60, 41.21, 82.41, 164.81, 329.63, 659.26, 1318.5, 2637, 5274, 10548, 21096},
			{21.83, 43.65, 87.31, 174.62, 349.23, 698.46, 1396.9, 2793.8, 5587.6, 11175.2, 22350.4},
			{23.12, 46.25, 92.50, 185, 369.99, 739.98, 1480, 2960, 5920, 11840, 23680},
			{24.50, 49, 98, 196, 392, 784, 1568, 3136, 6272, 12544, 25088},
			{25.96, 51.90, 103.80, 207, 415.30, 830.60, 1661.2, 3322.4, 6644.8, 13289.6, 26579.2},
			{27.50, 55, 110, 220, 440, 880, 1760, 3520, 7040, 14080, 28160},
			{29.14, 58.26, 116.54, 233.08, 466.16, 932.32, 1864.6, 3729.2, 7458.4, 14916.8, 29833.6},
			{30.87, 61.74, 123.48, 246.96, 493.88, 987.75, 1975.5, 3951, 7902, 15804, 31608}
	};
	private int octaveDecreasing;

	/**
	 * @param octaveDecreasing уменьшение октавы. например, при 1 ноты D5 превратятся в D4
	 */
	public Frequencies(int octaveDecreasing) {
		this.octaveDecreasing = octaveDecreasing;
		for (int i = 0; i < frequencies.length; i++) {
			double[] arr = frequencies[i];
			for (int j = 0; j < arr.length; j++) {
				arr[j] = Math.round(arr[j]);
			}
		}
	}

	/**
	 * Получить строку дефайна для ноты
	 */
	private String getNoteDefine(int noteNum, int octave, double frequency) {
		return String.format("#define %s%d %d", NOTES[noteNum], octave, (int) frequency);
	}

	@Override
	public String getCode() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int octave = octaveDecreasing; octave < OCTAVES_NUM; octave++) {
			for (int noteNum = 0; noteNum < NOTES.length; noteNum++) {
				stringBuilder
						.append(getNoteDefine(noteNum, octave, frequencies[noteNum][octave - octaveDecreasing]))
						.append("\n");
			}
		}
		return stringBuilder.toString();
	}

}
