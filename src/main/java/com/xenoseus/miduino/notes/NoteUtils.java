package com.xenoseus.miduino.notes;

public class NoteUtils {
	//bpm, приравнивающийся к коэффициенту 1.0f в длительности нот
	private static final float NORMAL_BPM = 120f;
	//количество микросекунд в одной минуте
	private static final long MINUTE_MICROSECONDS = 60_000_000;

	/**
	 * Получить временной коэффициент нот на основании BPM
	 */
	public static float BPMToTimeCoefficient(int bpm) {
		//120 bpm == 1.0f
		//обратная пропорциональная зависимость
		float timeCoefficient = NORMAL_BPM / bpm;
		if (timeCoefficient < 0.0f) return 0.0f;
		return timeCoefficient;
	}

	/**
	 * Получить BPM на основании числа микросекунд на бит из midi
	 */
	public static int TempoToBPM(long tempo) {
		return (int) (MINUTE_MICROSECONDS / tempo);
	}

}