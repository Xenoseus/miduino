package com.xenoseus.miduino;

public class MathUtils {

	private static long gcdSub(long a, long b) {
		while (b > 0) {
			long temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	/**
	 * Получить наибольший общий делитель для массива чисел
	 */
	public static long greatestCommonDivisor(long[] input) {
		long result = input[0];
		for (int i = 1; i < input.length; i++) result = gcdSub(result, input[i]);
		return result;
	}

}