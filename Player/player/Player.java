package player;
import numbers.RandomNumber;
import ratings.BattingRatings;
import ratings.FieldingRatings;
import ratings.GeneralRatings;
import ratings.PitchRatings;
import ratings.PitchType;
import ratings.PitchingRatings;
import stats.BattingSeasonStatistics;
import stats.BattingStatline;
import stats.PitchingSeasonStatistics;
import stats.PitchingStatline;
import stats.PlateAppearance;
import utility.General;

/* Eric Frye
 * Player represents a baseball player.  A player does not have to be on a team.
 * */

public class Player implements Comparable <Player>{
	
	private int pID; //ID of player.
	private Position pos; //Primary Position of player.
	private String firstName; //First name of player.
	private String lastName; //Last name of player.
	
	private BattingRatings bRatings; //Batting ratings.
	private PitchingRatings pRatings; //Pitching ratings.
	private FieldingRatings fRatings; //Fielding ratings.
	private GeneralRatings gRatings; //General Ratings
	
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
		
	}
	
	public boolean isEqual (int id) {
		return pID == id;
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
	
	public void generateSimpleStats () {
		bRatings.simpleGenerateBattingStats();
		pRatings.simpleGeneratePitchingRatings();
		gRatings.simpleGenerateGeneralRatings();
	}

	
	public boolean isPitcher () {
		return pos.equals(Position.PITCHER);
	}

	public int getpID() {
		return pID;
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

	public String getPlayerDataToSaveInfo (Integer teamID, Integer leagueID) {
		return pID + "," + General.ifExistsElseZero(teamID) + "," + General.ifExistsElseZero(leagueID) + "," + firstName + "," + lastName + "," + pos.ordinal() + "," + gRatings.getSpeed() + "\r";
	}
	
	public String getPlayerBattingRatingsDataToSaveInfo () {
		return pID + "," + bRatings.getContact() + "," + bRatings.getPower() + "," + bRatings.getDiscipline() + "," + bRatings.getHitBallType().num() + "," + bRatings.getSprayType().num() + "\r";
	}
	
	public String getPlayerPitchingRatingsDataToSaveInfo () {
		
		PitchRatings fastball = pRatings.selection.get(PitchType.FB);
		
		return pID + "," + fastball.getVelocity() + "," + fastball.getFilth() + "," + fastball.getControl() + "\r";
	}
	
	//returns array to be added to a row in PlayerStatsTable
	public String [] generateGameBattingStatsDisp () {
		String [] ret = {pos.shorthand(),fullName(),"0","0","0","0","0","0"};
		return ret;
	}
	
	public String [] generateGamePitchingStatsDisp () {
		String [] ret = {fullName(), "0","0","0","0","0","0"};
		return ret;
	}
	
}
