import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import atbat.HitTypeCalculator;
import ball.BallInPlay;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.FieldEvent;
import game.Game;
import game.RuleSet;
import helpers.DebuggingBuddy;
import objects.Base;
import objects.BaseType;
import objects.Baserunner;
import objects.Fielder;
import physics.Physics;
import player.Generators;
import player.Player;
import player.Position;
import stadium.Stadium;
import team.GameTeam;
import team.Team;
import ui.GameDisplay;

public class FielderTest {
	
	final static int WHITE = 0xFFFFFF;
	
	public static void main (String [] args) {
				
		Team home = new Team ();
		home.addFakePlayers();

		Team away = new Team ();
		away.addFakePlayers();

		Scanner input = null;
		try {
			input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Stadium stadium = new Stadium ();
		stadium.loadDimensions(input);
		
		
		GameDisplay gameView = new GameDisplay (500,500, 10, stadium, 1, away.tID, home.tID);

		List <Fielder> fielders = new LinkedList <Fielder> ();
		
		for (Player curPlayer: home.playersOnTeam) {
			Fielder newFielder = new Fielder (curPlayer, 0xFFFFFF);
			fielders.add(newFielder);
		}
		
		String name = "a";
		//int times = 1000000;
		int times = 1;
		
		HitTypeCalculator calc = new HitTypeCalculator ();
		calc.init();

		FieldEvent event = new FieldEvent (1,gameView,stadium);
		
		event.placeTestRunner(BaseType.FIRST);
		event.placeTestRunner(BaseType.SECOND);
		
		for (int i = 0; i < times; i++) {
				
			Player batter = new Player (Position.FIRST,name,name,times);
			batter.generateSimpleStats();
			
			event.batterPitcherInteraction(batter,away.playersOnTeam.get(0), fielders, calc);
			
			//DebuggingBuddy.wait(gameView);
			
			int num = (i%26) + 65;
			name = (char)(num) + "";
						
		}
		
		gameView.writeText("done");
		System.exit(0);
		
	}
	
}