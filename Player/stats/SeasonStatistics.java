package stats;

import stats.Statline;

public class SeasonStatistics {
	
	private int year;
	private int gamesPlayed;
	private int gamesStarted;
	private int teamID;
	private int playerID;
	private int leagueID;
	
	private int hits;
	private int doubles;
	private int triples;
	private int homers;
	private int strikeouts;
	private int walks;
	
	public SeasonStatistics (int year, int playerID, int teamID, int leagueID) {
		
		this.year = year;
		this.playerID = playerID;
		this.teamID = teamID;
		this.leagueID = leagueID;
		
		this.hits = 0;
		this.doubles = 0;
		this.triples = 0;
		this.homers = 0;
		this.strikeouts = 0;
		this.walks = 0;
		
	}

	
	
	public SeasonStatistics(int year, int gamesPlayed, int gamesStarted, int teamID, int playerID, int leagueID,
			int hits, int doubles, int triples, int homers, int strikeouts, int walks) {
		this.year = year;
		this.gamesPlayed = gamesPlayed;
		this.gamesStarted = gamesStarted;
		this.teamID = teamID;
		this.playerID = playerID;
		this.leagueID = leagueID;
		this.hits = hits;
		this.doubles = doubles;
		this.triples = triples;
		this.homers = homers;
		this.strikeouts = strikeouts;
		this.walks = walks;
	}


	public int getYear() {
		return year;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public int getGamesStarted() {
		return gamesStarted;
	}

	public int getTeamID() {
		return teamID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getLeagueID() {
		return leagueID;
	}

	public int getHits() {
		return hits;
	}

	public int getDoubles() {
		return doubles;
	}

	public int getTriples() {
		return triples;
	}

	public int getHomers() {
		return homers;
	}

	public int getStrikeouts() {
		return strikeouts;
	}

	public int getWalks() {
		return walks;
	}
	
	public void incGamesPlayed () {
		this.gamesPlayed++;
	}
	
	public void incGamesStarted () {
		this.gamesStarted++;
	}
	
	public void incHitsBy (int by) {
		this.hits += by;
	}
	
	public void incDoublesBy (int by) {
		this.doubles += by;
	}
	
	public void incTriplesBy (int by) {
		this.triples += by;
	}
	
	public void incHomersBy (int by) {
		this.homers += by;
	}
	
	public void incStrikeoutsBy (int by) {
		this.strikeouts += by;
	}
	
	public void incWalksBy (int by) {
		this.walks += by;
	}
	
	
	
}
