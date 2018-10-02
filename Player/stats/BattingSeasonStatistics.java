package stats;

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
	
	public void addGameStats (BattingStatline line, boolean wasStarter) {
		
		incGamesPlayed();
		
		runs += line.getRuns();
		rbi += line.getRbi();
		pa += line.getPA();
		ab += line.getAB();
		
		if (wasStarter) {
			incGamesStarted();
		}
		
		incHitsBy(line.getHits());
		incDoublesBy(line.getDoubles());
		incTriplesBy(line.getTriples());
		incHomersBy(line.getHomeruns());
		incStrikeoutsBy(line.getStrikeouts());
		incWalksBy(line.getWalks());
		
	}
	
}
