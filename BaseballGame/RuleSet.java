/* Eric Frye
 * RuleSet is a representation of the rules that govern a Game.  If the object is created with no parameters then the default rules are used.    
 * */

public class RuleSet {
	
	int numInnings = 9; //Number of innings in a game. 
	int allowExtras = 0;  //Allow extras innings.
	int allowDH = 0; //Allow designated hitter (boolean).
	int maxPlayersOnRoster = 25; //Max amount of players on a team.  There is no minimum.
	int outsPerInning = 3; //Number of outs to end an innning.
	int strikesPerAtBat = 3; //Number of strikes in an at bat needed to strikeout.
	int ballsPerAtBat = 4; //Number of balls in an at batt needed to walk.
	
	/*
	 * Constructor of RuleSet that takes in array of int for value of each rule.
	 * rules: Array of ints to set to params. Should be in same order as above.
	 * */
	public RuleSet (int [] rules) {
		
		numInnings = rules[0];
		allowExtras = rules[1];
		allowDH = rules[2];
		maxPlayersOnRoster = rules[3];
		
	}
	
	public boolean teamIsValid (Team toCheck) {
		return maxPlayersOnRoster >= toCheck.playersOnTeam.size();
	}
	
}
