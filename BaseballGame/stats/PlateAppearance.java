package stats;

import game.InningCounters;

public class PlateAppearance {
	
	private final int abID; //unique ab id
	private final int pID; //id of pitcher faced
	private final int hID; //id of batter
	
	private final int inning; //inning of at bat
	private int strikes = 0; //num of strikes at end
	private int balls = 0; //num of balls at end
	private int pitchesSeen = 0; //amount of pitches seen
	private final int numOuts; //number of outs
	private int runsScored;
	
	private Result outcome;

	public PlateAppearance(int abID, int pID, int hID, InningCounters ctr) {
		super();
		this.abID = abID;
		this.inning = ctr.getInning();
		this.pID = pID;
		this.hID = hID;
		this.numOuts = ctr.getInning();
	}

	public void setOutcome(Result outcome) {
		this.outcome = outcome;
	}

	public int getRunsScored() {
		return runsScored;
	}

	public void setRunsScored(int runsScored) {
		this.runsScored = runsScored;
	}

	public int getAbID() {
		return abID;
	}

	public int getInning() {
		return inning;
	}

	public int getStrikes() {
		return strikes;
	}

	public int getBalls() {
		return balls;
	}

	public Result getOutcome() {
		return outcome;
	}
	
	public void incStrikes () {
		strikes++;
	}
	
	public void incBalls () {
		balls++;
	}
	
	public void incPitches () {
		pitchesSeen++;
	}
	
	public void foul () {
		
		if (strikes < 2) {
			strikes++;
		}
		
	}
	
	public boolean isWalk () {
		return balls >= 4;
	}
	
	public boolean isStrikeout () {
		return strikes >= 3;
	}

	public String toString() {
		return "PlateAppearance [abID=" + abID + ", pID=" + pID + ", hID=" + hID + ", inning=" + inning + ", strikes="
				+ strikes + ", balls=" + balls + ", pitchesSeen=" + pitchesSeen + ", numOuts=" + numOuts
				+ ", runsScored=" + runsScored + ", outcome=" + outcome + "]";
	}
	
}
