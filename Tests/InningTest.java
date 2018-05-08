
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

public class InningTest {

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
		ruleSet.numInnings = 9;

		Scanner input = null;
		try {
			input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Stadium stadium = new Stadium ();
		stadium.loadDimensions(input);


		BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(45),Physics.degreesToRads(80),105,stadium, 0x000000);

		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, stadium,2);

		g.playGame();
		
		System.out.println(g.awayScore.runs);
		System.out.println(g.homeScore.runs);
		
	}

}


