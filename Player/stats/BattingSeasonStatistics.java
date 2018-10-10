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
	
	//to be used when reading statline in from a file
	public BattingSeasonStatistics (String [] data) {
		
		super(
				 Integer.parseInt(data[1]),  
				 Integer.parseInt(data[5]), 
				 Integer.parseInt(data[4]),
				 Integer.parseInt(data[2]),  
				 Integer.parseInt(data[0]),  
				 Integer.parseInt(data[3]),
				 Integer.parseInt(data[8]), 
				 Integer.parseInt(data[9]), 
				 Integer.parseInt(data[10]),  
				 Integer.parseInt(data[11]),
				 Integer.parseInt(data[13]), 
				 Integer.parseInt(data[12])
		);
		
		this.runs = Integer.parseInt(data[15]);
		this.rbi = Integer.parseInt(data[14]);
		this.pa = Integer.parseInt(data[6]);
		this.ab = Integer.parseInt(data[7]);
		
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
	
	public String convertToDataFormat () {
		
		return getPlayerID() +
				"," + getYear() +
				"," + getTeamID() +
				"," + getLeagueID() +
				"," + getGamesStarted() +
				"," + getGamesPlayed() +
				"," + pa +
				"," + ab +
				"," + getHits() +
				"," + getDoubles() +
				"," + getTriples() +
				"," + getHomers() +
				"," + getWalks() +
				"," + getStrikeouts() +
				"," + rbi +
				"," + runs;
	
	}
	
}
