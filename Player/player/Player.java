package player;
import numbers.RandomNumber;
import ratings.BattingRatings;
import ratings.FieldingRatings;
import ratings.GeneralRatings;
import ratings.PitchRatings;
import ratings.PitchType;
import ratings.PitchingRatings;
import stats.BattingStatline;
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
	
	public boolean isEqual (int id) {
		return pID == id;
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
	
	public void generateSimpleStats () {
		bRatings.simpleGenerateBattingStats();
		pRatings.simpleGeneratePitchingRatings();
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
	
	public void scoredRun () {
		curGameBatting.incRuns();
	}
	
	public void droveInRuns (int amt) {
		curGameBatting.incRbi(amt);
	}
	
	public void allowedRuns (int amt) {
		curGamePitching.incERA(amt);
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
	
	public String getPlayerCurGameBatting (Integer year, Integer teamID, Integer leagueID) {
		return pID + "," + General.ifExistsElseZero(year) + "," + General.ifExistsElseZero(teamID) + "," + General.ifExistsElseZero(leagueID) + "," + curGameBatting.getPA() + "," + curGameBatting.getHits() + "," + curGameBatting.getDoubles() + "," + curGameBatting.getTriples() + "," + curGameBatting.getHomeruns() + "," + curGameBatting.getWalks() + "," + curGameBatting.getStrikeouts() + "," + curGameBatting.getRbi() + "," + curGameBatting.getRuns() + "\r"; 
	}
	
	public String getPlayerCurGamePitching (Integer year, Integer teamID, Integer leagueID) {
		return pID + "," + General.ifExistsElseZero(year) + "," + General.ifExistsElseZero(teamID) + "," + General.ifExistsElseZero(leagueID) + "," + curGamePitching.getOutsRec() + "," + curGamePitching.getHits() + "," + curGamePitching.getDoubles() + "," + curGamePitching.getTriples() + "," + curGamePitching.getHomeruns() + "," + curGamePitching.getWalks() + "," + curGamePitching.getStrikeouts() + "," + curGamePitching.getRa() + "\r";
	}
	
	public void add (BattingStatline b, PitchingStatline p) {
		cumStats.add(b, p);
	}
	
	//returns array to be added to a row in PlayerStatsTable
	public String [] generateGameBattingStatsDisp () {
		String [] ret = {pos.shorthand(),fullName(),"0","0","0","0","0","0"};
		return ret;
	}
	
	public String [] generateCurGameBattingStatsDisp () {
		String [] ret = {pos.shorthand(),fullName(),curGameBatting.getAB()+"",curGameBatting.getHits()+"",curGameBatting.getRuns()+"",curGameBatting.getRbi()+"",curGameBatting.getStrikeouts()+"",curGameBatting.getWalks()+""};
		return ret;
	}
	
	public String [] generateCurPitchingBattingStatsDisp () {
		String [] ret = {fullName(),curGamePitching.getOutsRec()+"",curGamePitching.getHits()+"",curGamePitching.getEra()+"",curGamePitching.getWalks()+"",curGamePitching.getStrikeouts()+"",curGamePitching.getHomeruns()+""};
		return ret;
	}
	
	public String [] generateGamePitchingStatsDisp () {
		String [] ret = {fullName(), "0","0","0","0","0","0"};
		return ret;
	}
	
	public void addBattingPA (PlateAppearance toAdd) {
		curGameBatting.addPA(toAdd);
	}
	
	public void addPitchingPA (PlateAppearance toAdd, int outsRec) {
		curGamePitching.addPA(toAdd, outsRec);
	}
		
}
