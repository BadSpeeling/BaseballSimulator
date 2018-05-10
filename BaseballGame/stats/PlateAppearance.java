package stats;

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

	public PlateAppearance(int abID, int outs, int inning, int pID, int hID) {
		super();
		this.abID = abID;
		this.inning = inning;
		this.pID = pID;
		this.hID = hID;
		this.numOuts = outs;
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
	
}
