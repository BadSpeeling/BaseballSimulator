package stats;

import java.util.HashMap;
import java.util.Map;

import ui.StatsTable;

public class Scorecard {

	private final int HITS_COL = 2;
	private final int AB_COL = 1;
	private final int R_COL = 3;
	private final int RBI_COL = 4;
	private final int K_COL = 6;
	private final int BB_COL = 5;

	private final int tID;
	private final int gID;

	private Map <Integer, BattingStatline> battingStats = new HashMap <Integer,BattingStatline> ();
	private Map <Integer, PitchingStatline> pitchingStats = new HashMap <Integer, PitchingStatline> ();
	private StatsTable battingStatsView;

	public Scorecard(int tID, int gID, StatsTable battingStatsView) {
		super();
		this.tID = tID;
		this.gID = gID;
		this.battingStatsView = battingStatsView;
	}

	public StatsTable getBattingStatsView() {
		return battingStatsView;
	}

	//creates a statline for a player
	public void addPlayer (int pID) {
		battingStats.put(pID,new BattingStatline(pID));
		pitchingStats.put(pID,new PitchingStatline(pID));
	}

	public Map<Integer, BattingStatline> getBattingStats() {
		return battingStats;
	}

	public Map<Integer, PitchingStatline> getPitchingStats() {
		return pitchingStats;
	}

	public void addPitchingStats (int id, PlateAppearance toAdd) {
		pitchingStats.get(id).addPA(toAdd);
	}

	public void addBattingStats (int id, PlateAppearance toAdd) {

		int row = battingStatsView.whichRow(id);
		
		if (toAdd.getOutcome().equals(Result.BB)) {
			battingStatsView.incCell(row, BB_COL);
			return;
		}
		
		else if (toAdd.getOutcome().equals(Result.K)) {
			battingStatsView.incCell(row, K_COL);
		}
		
		else if (!toAdd.getOutcome().equals(Result.OUT)) {
			battingStatsView.incCell(row, HITS_COL);
		}

		battingStats.get(id).addPA(toAdd);
		battingStatsView.incCell(row, AB_COL);


	}

	public void scoredRun (int id) {
		
		battingStats.get(id).incRuns();
		int row = battingStatsView.whichRow(id);
		battingStatsView.incCell(row, R_COL);

	}

	public void droveInRun (int id) {
		
		battingStats.get(id).incRBI();
		int row = battingStatsView.whichRow(id);
		battingStatsView.incCell(row, RBI_COL);

	}

	public void allowedRun (int id) {
		pitchingStats.get(id).incERA();
	}

	public String toString () {

		String ret = "";

		for (Integer key: battingStats.keySet()) {
			ret += battingStats.get(key).toString() + "\n";
		}

		return ret;

	}

}
