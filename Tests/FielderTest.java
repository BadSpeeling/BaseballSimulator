import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
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
		
		Stadium stadium = new Stadium ();
		stadium.loadDimensions(input);

		LinkedList <Coordinate3D> allVals = new LinkedList <Coordinate3D> ();
		allVals.add(stadium.dimCoors.get("l"));
		allVals.add(stadium.dimCoors.get("lc"));
		allVals.add(stadium.dimCoors.get("c"));
		allVals.add(stadium.dimCoors.get("rc"));
		allVals.add(stadium.dimCoors.get("r"));
		
		BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(00),Physics.degreesToRads(80),100,stadium);
		
		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, stadium,2);
		
		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();
		fielders.add(new Fielder(g.log,FieldConstants.stdLeft(), homeTeam.inTheField.get(6), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdCenter(), homeTeam.inTheField.get(7), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdRight(), homeTeam.inTheField.get(8), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdCatcher(), homeTeam.inTheField.get(1), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdFirst(), homeTeam.inTheField.get(2), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdThird(), homeTeam.inTheField.get(4), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdSecond(), homeTeam.inTheField.get(3), allVals));
		fielders.add(new Fielder(g.log,FieldConstants.stdShort(), homeTeam.inTheField.get(5), allVals));
		
		List <Baserunner> runners = new LinkedList <Baserunner> ();
		
		//runners.add(new Baserunner(g.curBatter, g.log, BaseType.THIRD));
		runners.add(new Baserunner(g.curBatter, g.log, BaseType.SECOND));
		
		g.fieldEvent(fielders, hitBall, runners, awayTeam.lineup.next());
		
	}
	
}