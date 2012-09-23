/*
* @ Author - Digistr
* @ Info - Faster to obtain random values and more safer then the normal Random().nextInt()
*/

package com.util;

public class Random {

	private static long seed;

	public Random() {

	}

	public static int random(int value) {
		return (int)(nextDouble() * value + 1);
	}

	private static double nextDouble() {
		seed = (System.nanoTime() ^ 0x5deece66dL) & ((1L << 48) - 1);
		return ((((long) nextSeed(26) << 27) + nextSeed(27)) / (double) (1L << 53));
	}

	private static int nextSeed(int bits) {
		seed = (seed * 0x5deece66dL + 0xbL) & ((1L << 48) - 1);
		return (int) (seed >>> (48 - bits));
	}
}