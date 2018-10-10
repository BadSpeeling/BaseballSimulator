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
	
	public boolean addGameStats (int id, BattingStatline line, boolean wasStarter) {
		
		BattingSeasonStatistics curSeasonStats = stats.get(id);
		
		if (curSeasonStats != null) {
			curSeasonStats.addGameStats(line, wasStarter);
			return true;
		}
		
		else {
			return false;
		}
		
	}
	
}
