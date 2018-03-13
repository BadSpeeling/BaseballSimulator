/* Eric Frye
 * InningCounters represents the balls strikes and outs of an inning
 * */

public class InningCounters {
	
	int strikes = 0;
	int balls = 0;
	int outs = 0;
	int inning = 1;
	boolean top = true;
	
	/*
	 * Resets the counters.
	 * */
	public void nextAtBat () {
		strikes = 0;
		balls = 0;
	}
	
	/*
	 * Moves on to next half inning.  Reset outs.s
	 * */
	public void nextHalfInning () {
	
		nextAtBat();
		
		outs = 0;
		
		if (!top) {
			inning++;
		}
		
		top = !top;
		
		if (top) {
			System.out.println("Entering the top of the " + inning);
		}
		
		else {
			System.out.println("Entering the bottom of the " + inning);
		}
		
	}
	
}
