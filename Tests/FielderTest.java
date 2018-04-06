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
		
		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();
		fielders.add(new Fielder(FieldConstants.stdLeft(), homeTeam.inTheField.get(6), allVals));
		fielders.add(new Fielder(FieldConstants.stdCenter(), homeTeam.inTheField.get(7), allVals));
		fielders.add(new Fielder(FieldConstants.stdRight(), homeTeam.inTheField.get(8), allVals));
		fielders.add(new Fielder(FieldConstants.stdCatcher(), homeTeam.inTheField.get(1), allVals));
		fielders.add(new Fielder(FieldConstants.stdFirst(), homeTeam.inTheField.get(2), allVals));
		fielders.add(new Fielder(FieldConstants.stdThird(), homeTeam.inTheField.get(4), allVals));
		fielders.add(new Fielder(FieldConstants.stdSecond(), homeTeam.inTheField.get(3), allVals));
		fielders.add(new Fielder(FieldConstants.stdShort(), homeTeam.inTheField.get(5), allVals));
		
		BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(21),Physics.degreesToRads(30),130.0,stadium);
		
		Baserunner runner = new Baserunner (awayTeam.lineup.next().gRatings, "Name");
		Baserunner runner1 = new Baserunner (awayTeam.lineup.next().gRatings, "Name2");
		runner1.setBaseOn(Base.FIRST);
		List <Baserunner> runners = new LinkedList <Baserunner> ();
		runners.add(runner);
		runners.add(runner1);
		
		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, stadium);
		g.fieldEvent(fielders, hitBall, runners, awayTeam.lineup.next());
		
	}
	
}