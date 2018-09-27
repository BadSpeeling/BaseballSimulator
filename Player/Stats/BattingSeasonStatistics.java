package Stats;

import stats.BattingStatline;

public class BattingSeasonStatistics extends SeasonStatistics{
	
	private int runs;
	private int rbi;
	private int sacbunts;
	private int sacflies;
	private int pa;
	private int ab;
	
	public BattingSeasonStatistics (int year, int playerID, int teamID, int leagueID) {
		
		super(year,playerID,teamID,leagueID);
		this.runs = 0;
		this.rbi = 0;
		this.sacbunts = 0;
		this.sacflies = 0;
		this.pa = 0;
		this.ab = 0;
		
	}
	
	public void addGameStats (BattingStatline line) {
		
		
		
	}
	
}
