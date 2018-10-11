package game;

public class GameMetadata {
	
	private int yearPlayedIn;
	private int leagueID;
	private int homeTeamID;
	private int awayTeamID;
	private int gameID;
	
	public GameMetadata(int yearPlayedIn, int leagueID, int homeTeamID, int awayTeamID, int gameID) {
		this.yearPlayedIn = yearPlayedIn;
		this.leagueID = leagueID;
		this.homeTeamID = homeTeamID;
		this.awayTeamID = awayTeamID;
		this.gameID = gameID;
	}

	public int getYearPlayedIn() {
		return yearPlayedIn;
	}

	public int getLeagueID() {
		return leagueID;
	}

	public int getHomeTeamID() {
		return homeTeamID;
	}

	public int getAwayTeamID() {
		return awayTeamID;
	}
	
	public int getGameID() {
		return gameID;
	}
	
}
