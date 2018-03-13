import java.util.Iterator;

/* Eric Frye
 * BasicGame test runs with two random teams.  This is a test for basic functionality of the game control functionality.
 * */

public class BasicGameTest {
	
	public static void main (String [] args) {
		
		int playersOnTeam = 9;
		int [] rules = {9,0,0,25};
		
		Team home = new Team ();
		home.addFakePlayers(playersOnTeam);
		
		Team away = new Team ();
		away.addFakePlayers(playersOnTeam);
		
		home.printTeam();
		System.out.println();
		away.printTeam();
		
		GameTeam homeTeam = home.makeInGameTeam(true);
		GameTeam awayTeam = away.makeInGameTeam(true);
		
		RuleSet ruleSet = new RuleSet (rules);
		ruleSet.numInnings = 3;
		
		new Game (ruleSet, 1, homeTeam, awayTeam, null);
		
		System.out.println("\n"+awayTeam.score);
		System.out.println(homeTeam.score);
		
	}
	
}
