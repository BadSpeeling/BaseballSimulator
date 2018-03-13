/* Eric Frye
 * RandomNumber is a basic class that makes generating random numbers simpler
 * */

import java.util.Random;

public class RandomNumber {
	
	//Rolls a number between min and max.
	public static int roll (int min, int max) {
		return new Random().nextInt(max-min+1)+min;
	}
	
	//Rolls a number between 0 and 100.
	public static int roll () {
		return new Random().nextInt(101);
	}
	
	//Returns true if a random number 0-100 is less than val.
	public static boolean boundaryCheck (int val) {
		return roll() < val;
	}
	
	//Returns true if a random number min-max is less than val.
	public static boolean boundaryCheck (int val, int min, int max) {
		return roll(min, max) < val;
	}
	
}
