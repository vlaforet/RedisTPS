package com.vivi7865.RedisTPS;

public class TPS implements Runnable {
	public static int tick_count= 0;
	public static long[] TICKS= new long[600];

	public static double getTPS() {
		return getTPS(100);
	}

	public static double getTPS(int ticks) {
		if (tick_count< ticks) {
			return 20.0D;
		}
		int target = (tick_count- 1 - ticks) % TICKS.length;
		long elapsed = System.currentTimeMillis() - TICKS[target];

		return ticks / (elapsed / 1000.0D);
	}

	public static long getElapsed(int tickID) {

		long time = TICKS[(tickID % TICKS.length)];
		return System.currentTimeMillis() - time;
	}

	public void run() {
		TICKS[(tick_count% TICKS.length)] = System.currentTimeMillis();
		tick_count+= 1;
	}
}