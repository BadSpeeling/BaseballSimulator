package stats;

import java.util.HashMap;
import java.util.Map;

public class Scorecard {
	
	private final int tID;
	private final int gID;
	
	private Map <Integer, BattingStatline> battingStats = new HashMap <Integer,BattingStatline> ();
	private Map <Integer, PitchingStatline> pitchingStats = new HashMap <Integer, PitchingStatline> ();
	
	public Scorecard(int tID, int gID) {
		super();
		this.tID = tID;
		this.gID = gID;
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
		battingStats.get(id).addPA(toAdd);
	}
	
	public void scoredRun (int id) {
		battingStats.get(id).incRuns();
	}
	
	public void droveInRun (int id) {
		battingStats.get(id).incRBI();
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
