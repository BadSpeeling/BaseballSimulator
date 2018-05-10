package player;
import ratings.BattingRatings;
import ratings.FieldingRatings;
import ratings.GeneralRatings;
import ratings.PitchingRatings;

/* Eric Frye
 * Player represents a baseball player.  A player does not have to be on a team.
 * */

public class Player implements Comparable <Player>{
	
	public static int nextID = 1;
	public int pID; //ID of player.
	public int teamID; //ID of the team the player is on. 0 means that the player is a free agent.
	public int leagueID; //ID of the league that the player is in. 0 means that the player is not in a league.
	public Position pos; //Primary Position of player.
	public String firstName; //First name of player.
	public String lastName; //Last name of player.
	public BattingRatings bRatings; //Batting ratings.
	public PitchingRatings pRatings; //Pitching ratings.
	public FieldingRatings fRatings; //Fielding ratings.
	public GeneralRatings gRatings; //General Ratings
	
	/* 
	 * Basic constructor for Player.  Only uses first and last name, position and a unique ID
	 * */
	public Player (String f, String l, Position p) {
		
		pID = nextID++;
		firstName = f;
		lastName = l;
		pos = p;
		bRatings = new BattingRatings();
		pRatings = new PitchingRatings("FB,90,95,40,60");
		fRatings = new FieldingRatings();	
		gRatings = new GeneralRatings();
				
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
		
}