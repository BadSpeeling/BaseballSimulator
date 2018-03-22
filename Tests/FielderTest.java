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

		LinkedList <Coordinate3D> allVals = new LinkedList <Coordinate3D> ();
		allVals.add(cur.dimCoors.get("l"));
		allVals.add(cur.dimCoors.get("lc"));
		allVals.add(cur.dimCoors.get("c"));
		allVals.add(cur.dimCoors.get("rc"));
		allVals.add(cur.dimCoors.get("r"));
		
		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();
		fielders.add(new Fielder(250,250,0, homeTeam.pitcher, 7,"Eric", allVals));
		fielders.add(new Fielder(50,300,0, homeTeam.pitcher, 8, "John", allVals));
		fielders.add(new Fielder(300,50,0, homeTeam.pitcher, 9, "Frye", allVals));
		fielders.add(new Fielder(-5,-5,0, homeTeam.pitcher, 2, "catcher", allVals));
		fielders.add(new Fielder(100,15,0, homeTeam.pitcher, 3, "first", allVals));
		fielders.add(new Fielder(15,100,0, homeTeam.pitcher, 5, "third", allVals));
		fielders.add(new Fielder(110,70,0, homeTeam.pitcher, 4, "second", allVals));
		
		BallInPlay hitBall = new BallInPlay (0,0,3,.4,Math.PI/5-.1,170.0,cur);
		
		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, cur);
		g.liveBallDriver(fielders, hitBall);
		
	}
	
}