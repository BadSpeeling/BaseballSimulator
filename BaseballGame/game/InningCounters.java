package game;
/* Eric Frye
 * InningCounters represents the balls strikes and outs of an inning
 * */

public class InningCounters {
	
	private int strikes = 0;
	private int balls = 0;
	private int outs = 0;
	private int inning = 1;
	private boolean top = true;
	
	public int getStrikes() {
		return strikes;
	}
	
	public void incOuts () {
		outs++;
	}
	
	public void incStrikes () {
		strikes++;
	}
	
	public void incBalls () {
		balls++;
	}
	
	public void setStrikes(int strikes) {
		this.strikes = strikes;
	}

	public int getBalls() {
		return balls;
	}

	public void setBalls(int balls) {
		this.balls = balls;
	}

	public int getOuts() {
		return outs;
	}

	public void setOuts(int outs) {
		this.outs = outs;
	}

	public int getInning() {
		return inning;
	}

	public void setInning(int inning) {
		this.inning = inning;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

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
