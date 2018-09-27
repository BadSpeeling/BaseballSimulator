package Stats;

public class PitchingSeasonStatistics extends SeasonStatistics{
	
	private int bf;
	private int outsRec;
	private int ra;
	private int er;
	
	public PitchingSeasonStatistics (int year, int playerID, int teamID, int leagueID) {
		
		super(year,playerID,teamID,leagueID);
		this.bf = 0;
		this.outsRec = 0;
		this.ra = 0;
		this.er = 0;
		
	}
	
}
