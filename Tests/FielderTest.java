import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ball.BallInPlay;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.Game;
import game.RuleSet;
import main.BaseType;
import main.Baserunner;
import main.Fielder;
import physics.Physics;
import stadium.Stadium;
import team.GameTeam;
import team.Team;

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
		
		BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(30),Physics.degreesToRads(80),130,stadium);
		
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
		
		Baserunner runner1 = new Baserunner(g.curBatter, g.log, BaseType.SECOND);
		Baserunner runner2 = new Baserunner(g.curBatter, g.log, BaseType.FIRST);
		
		runners.add(runner1);
		runners.add(runner2);
		
		g.fieldEvent(fielders, hitBall, runners, awayTeam.lineup.next());
		
	}
	
}