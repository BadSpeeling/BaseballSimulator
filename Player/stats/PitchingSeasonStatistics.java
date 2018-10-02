package stats;

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
	
	public void addGameStats (PitchingStatline line, boolean wasStartingPitcher) {
		
		incGamesPlayed();
		
		if (wasStartingPitcher) {
			incGamesStarted();
		}
		
		bf += line.getBf();
		outsRec += line.getOutsRec();
		ra += line.getRa();
		er += line.getEra();
		
		incHitsBy(line.getHits());
		incDoublesBy(line.getDoubles());
		incTriplesBy(line.getTriples());
		incHomersBy(line.getHomeruns());
		incStrikeoutsBy(line.getStrikeouts());
		incWalksBy(line.getWalks());
		
	}
	
}
