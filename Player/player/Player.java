package player;
import numbers.RandomNumber;
import ratings.BattingRatings;
import ratings.FieldingRatings;
import ratings.GeneralRatings;
import ratings.PitchingRatings;
import stats.BattingStatline;
import stats.PitchingStatline;

/* Eric Frye
 * Player represents a baseball player.  A player does not have to be on a team.
 * */

public class Player implements Comparable <Player>{
	
	private int pID; //ID of player.
	private int teamID = 0; //ID of the team the player is on. 0 means that the player is a free agent.
	private int leagueID = 0; //ID of the league that the player is in. 0 means that the player is not in a league.
	private Position pos; //Primary Position of player.
	private String firstName; //First name of player.
	private String lastName; //Last name of player.
	private BattingRatings bRatings; //Batting ratings.
	private PitchingRatings pRatings; //Pitching ratings.
	private FieldingRatings fRatings; //Fielding ratings.
	private GeneralRatings gRatings; //General Ratings
	private SeasonStats cumStats;
	private BattingStatline curGameBatting;
	private PitchingStatline curGamePitching;
	
	/* 
	 * Basic constructor for Player.  Only uses first and last name, position and a unique ID
	 * */
	public Player (Position p, String f, String l, int id) {
		
		pID = id;
		firstName = f;
		lastName = l;
		pos = p;
		bRatings = new BattingRatings(RandomNumber.roll(-2, 2), RandomNumber.roll(-1, 2));
		pRatings = new PitchingRatings();
		pRatings.basicAddFastball();
		fRatings = new FieldingRatings();	
		gRatings = new GeneralRatings();
		cumStats = new SeasonStats(id);
		curGameBatting = new BattingStatline (id);
		curGamePitching = new PitchingStatline (id);		
		
	}
	
	public BattingStatline getCurGameBatting() {
		return curGameBatting;
	}

	public PitchingStatline getCurGamePitching() {
		return curGamePitching;
	}

	public String toString () {
		return pID + "," + firstName + "," + lastName;
	}

	public int compareTo(Player comp) {
		return pID - comp.pID;
	}
	
	public String fullName () {
		return firstName + " " + lastName; 
	}
	
	public void generatePlayer () {
		bRatings.simpleGenerateBattingStats();
		pRatings.simpleGeneratePitchRatings();
		gRatings.simpleGenerateGeneralRatings();
	}
	
	public void resetGameStats () {
		curGameBatting = new BattingStatline(pID);
		curGamePitching = new PitchingStatline(pID);
	}
	
	public boolean isPitcher () {
		return pos.equals(Position.PITCHER);
	}

	public int getpID() {
		return pID;
	}

	public int getTeamID() {
		return teamID;
	}

	public int getLeagueID() {
		return leagueID;
	}

	public Position getPos() {
		return pos;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public BattingRatings getbRatings() {
		return bRatings;
	}

	public PitchingRatings getpRatings() {
		return pRatings;
	}

	public FieldingRatings getfRatings() {
		return fRatings;
	}

	public GeneralRatings getgRatings() {
		return gRatings;
	}
	
	public String toWriter () {
		return pID +","+ teamID + "," + leagueID + "," + pos.ordinal()+1 + "," + firstName + "," + lastName + ",";
	}
	
	public String basicToWriter () {
		String ret = toWriter() + getbRatings().toWriter() + getpRatings().toWriter() + getgRatings().toWriter();
		return (ret).substring(0, ret.length()-1);
	}
	
	public void add (BattingStatline b, PitchingStatline p) {
		cumStats.add(b, p);
	}
		
}
