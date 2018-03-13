import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class FielderTest {
	
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

		Scanner input = null;
		try {
			input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Stadium cur = new Stadium ();
		cur.loadDimensions(input);

		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();
		fielders.add(new Fielder(250,250,0, homeTeam.pitcher, 1));
		fielders.add(new Fielder(50,300,0, homeTeam.pitcher, 1));
		fielders.add(new Fielder(300,50,0, homeTeam.pitcher, 1));
		
		BallInPlay hitBall = new BallInPlay (0,0,3,.3,Math.PI/4+.02,159.23);
		
		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, cur);
		g.liveBallDriver(fielders, hitBall);
		
	}
	
}
