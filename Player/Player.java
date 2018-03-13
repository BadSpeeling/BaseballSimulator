/* Eric Frye
 * Player represents a baseball player.  A player does not have to be on a team.
 * */

public class Player implements Comparable <Player>{
	
	static int nextID = 1;
	int pID; //ID of player.
	int teamID; //ID of the team the player is on. 0 means that the player is a free agent.
	int leagueID; //ID of the league that the player is in. 0 means that the player is not in a league.
	Position pos; //Primary Position of player.
	String firstName; //First name of player.
	String lastName; //Last name of player.
	BattingRatings bRatings; //Batting ratings.
	PitchingRatings pRatings; //Pitching ratings.
	FieldingRatings fRatings; //Fielding ratings.
	GeneralRatings gRatings; //General Ratings
	
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
