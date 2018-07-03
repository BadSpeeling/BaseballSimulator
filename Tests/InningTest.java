
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


import game.Game;
import game.RuleSet;

import stadium.Stadium;
import team.Team;

public class InningTest {

	public static void main (String [] args) {

		int [] rules = {9,0,0,25};
		
		final int times = 5000;
		
		double tot = 0;
		double errors = 0;
		
		for (int i = 0; i < times; i++) {
			Team home = new Team ();
			home.addFakePlayers();
	
			Team away = new Team ();
			away.addFakePlayers();
	
			RuleSet ruleSet = new RuleSet (rules);
			ruleSet.numInnings = 9;
	
			Scanner input = null;
			try {
				input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	
			Stadium stadium = new Stadium ();
			stadium.loadDimensions(input);
	
			Game g = new Game (ruleSet, 1, home, away, stadium,6);
			
			try {
				g.playGame();		
				
				//System.out.println("Home count: " + g.getHomeStatline().getTotalBattingStats().getRuns());
				//System.out.println("Away count: " + g.getAwayStatline().getTotalBattingStats().getRuns());
				tot += g.getHomeStatline().getTotalBattingStats().getRuns() + g.getAwayStatline().getTotalBattingStats().getRuns();
			}
			
			catch (Exception e) {
				errors++;
			}
			
		}
		
		System.out.println(errors/times);
		System.out.println("done");
		
		System.out.println(tot/(2*times));
		
			
	}

}


