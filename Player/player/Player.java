package player;
import ID.Serialized;
import numbers.RandomNumber;
import ratings.BattingRatings;
import ratings.FieldingRatings;
import ratings.GeneralRatings;
import ratings.HitBallType;
import ratings.PitchRatings;
import ratings.PitchType;
import ratings.PitchingRatings;
import ratings.SprayType;
import stats.BattingSeasonStatistics;
import stats.BattingStatline;
import stats.PitchingSeasonStatistics;
import stats.PitchingStatline;
import stats.PlateAppearance;
import utility.General;

/* Eric Frye
 * Player represents a baseball player.  A player does not have to be on a team.
 * */

public class Player extends Serialized implements Comparable <Player> {
	
	private Position pos; //Primary Position of player.
	private String firstName; //First name of player.
	private String lastName; //Last name of player.
	
	private BattingRatings bRatings; //Batting ratings.
	private PitchingRatings pRatings; //Pitching ratings.
	private FieldingRatings fRatings; //Fielding ratings.
	private GeneralRatings gRatings; //General Ratings
	
	public Player (Position p, String f, String l, int id) {
		
		super(id);
		firstName = f;
		lastName = l;
		pos = p;
		bRatings = new BattingRatings(RandomNumber.roll(-2, 2), RandomNumber.roll(-1, 2));
		pRatings = new PitchingRatings();
		pRatings.basicAddFastball();
		fRatings = new FieldingRatings();	
		gRatings = new GeneralRatings();	
		
	}
	
	public Player (int id) {
		super(id);
	}
 	
	/*
	 * player - PlayerID,TeamID,LeagueID,FirstName,LastName,Position,Speed
	 */
	public void loadGeneralInfo (String [] player) {
		
		Position pos = Position.getValue(Integer.parseInt(player[5]));
		String fName = player[3];;
		String lName = player[4];
		double speed = Double.parseDouble(player[6]);
		
		this.pos = pos;
		this.firstName = fName;
		this.lastName = lName;
		this.gRatings = new GeneralRatings (speed);
		
	}
	
	//batting - ID,Contact,Power,Discipline,HitBallType,SprayType	
	public void loadBattingRatings (String [] batting) {
		
		double contact = Double.parseDouble(batting[1]);
		double power = Double.parseDouble(batting[2]);
		double discipline = Double.parseDouble(batting[3]);
		HitBallType hitBallType = HitBallType.convert(Integer.parseInt(batting[4]));
		SprayType sprayType = SprayType.convert(Integer.parseInt(batting[5]));
		
		this.bRatings = new BattingRatings (hitBallType, sprayType, contact, power, discipline);
		
	}
	
	//pitching - ID,FBVelo,FBMovement,FBControl
	public void loadPitchingRatings (String [] pitching) {
		
		double fbVelo = Double.parseDouble(pitching[1]);
		double fbMovement = Double.parseDouble(pitching[2]);
		double fbControl = Double.parseDouble(pitching[3]);
		
		this.pRatings = new PitchingRatings ();
		this.pRatings.addPitch(PitchType.FB, fbVelo, fbMovement, fbControl);
		
	}
	

	public boolean isEqual (int id) {
		return getID() == id;
	}
	
	public String toString () {
		return getID() + "," + firstName + "," + lastName;
	}

	public int compareTo(Player comp) {
		return getID() - comp.getID();
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
		return getID();
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
		return getID() + "," + General.ifExistsElseZero(teamID) + "," + General.ifExistsElseZero(leagueID) + "," + firstName + "," + lastName + "," + pos.ordinal() + "," + gRatings.getSpeed() + "\r";
	}
	
	public String getPlayerBattingRatingsDataToSaveInfo () {
		return getID() + "," + bRatings.getContact() + "," + bRatings.getPower() + "," + bRatings.getDiscipline() + "," + bRatings.getHitBallType().num() + "," + bRatings.getSprayType().num() + "\r";
	}
	
	public String getPlayerPitchingRatingsDataToSaveInfo () {
		
		PitchRatings fastball = pRatings.selection.get(PitchType.FB);
		return getID() + "," + fastball.getVelocity() + "," + fastball.getFilth() + "," + fastball.getControl() + "\r";
		
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
