package team;

import java.util.*;

import player.Player;
import stats.BattingSeasonStatistics;
import stats.BattingStatline;

public class PlayerBattingStatistics {
	
	private Map <Integer, BattingSeasonStatistics> stats;
	
	public PlayerBattingStatistics () {
		stats = new HashMap <Integer, BattingSeasonStatistics> ();
	}
	
	public void addEmptyRecords (List <Player> playersOnTeam, int year, int teamID, int leagueID) {
		
		for (Player curPlayer: playersOnTeam) {
			
			if (!stats.containsKey(curPlayer.getpID())) {
				stats.put(curPlayer.getpID(), new BattingSeasonStatistics(year,curPlayer.getpID(),teamID,leagueID));
			}
			
		}
		
	}
	
	public void addGameStats (int id, BattingStatline line) throws IllegalArgumentException {
		
		BattingSeasonStatistics curSeasonStats = stats.get(id);
		
		if (curSeasonStats != null) {
			curSeasonStats.addGameStats(line);
		}
		
		else {
			throw new IllegalArgumentException ("Attempted to add game batting stats for player ID "+ id +" that does not have records");
		}
		
	}
	
	public BattingSeasonStatistics [] getAllBattingSeasonStatistics () {
		
		BattingSeasonStatistics [] ret = new BattingSeasonStatistics [stats.keySet().size()];
		
		int ctr = 0;
		
		for (Integer curBattingStatsID: stats.keySet()) {
			ret[ctr] = stats.get(curBattingStatsID);
			ctr++;
		}
		
		return ret;
		
	}
	
}
