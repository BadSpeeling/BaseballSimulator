package game;
/* Eric Frye
 * InningCounters represents the balls strikes and outs of an inning
 * */

public class InningCounters {
	
	private int outs = 0;
	private int inning = 1;
	private boolean top = true;
	
	public void incOuts () {
		outs++;
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
	 * Moves on to next half inning.  Reset outs.s
	 * */
	public void nextHalfInning () {
			
		outs = 0;
		
		if (!top) {
			inning++;
		}
		
		top = !top;
		
	}
	
}
