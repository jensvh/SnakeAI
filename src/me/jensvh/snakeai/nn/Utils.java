package me.jensvh.snakeai.nn;

import java.util.Random;

public class Utils {

	/**
	 * This function returns a random number between -1 and 1.
	 * @return random float
	 */
	public static float random() {
		float val = (float) (new Random().nextFloat() * 2) - (float)1.0f;
		//System.out.println("Random: " + val);
		return val;
	}
	
	/**
	 * Returns a random between 0 and a given number
	 * @param max The maximum number to be returned
	 * @return random float between 0 and a given number
	 */
	public static float random(float max) {
		float val = (float) new Random().nextFloat() * max;
		//System.out.println("RandomFloat(" + max + "): " + val);
		return val;
	}
	
	/**
	 * Returns a random between 0 and a given number
	 * @param max The maximum number to be returned
	 * @return random int between 0 and a given number
	 */
	public static int random(int max) {
		int val = (int) new Random().nextInt(max);
		//System.out.println("RandomInt(" + max + "): " + val);
		return val;
	}
	
	/**
	 * Generates a small number that can vary.
	 * @return small random number
	 */
	public static double randomGaussian() {
		double val = (double) new Random().nextGaussian();
		//System.out.println("RandomGaussian: " + val);
		return val;
	}
	
}
