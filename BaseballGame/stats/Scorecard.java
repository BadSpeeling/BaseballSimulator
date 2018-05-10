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
	
	public void addPitchingStats (int id, PlateAppearance toAdd) {
		pitchingStats.get(id).addPA(toAdd);
	}
	
	public void addBattingStats (int id, PlateAppearance toAdd) {
		battingStats.get(id).addPA(toAdd);
	}
	
}
