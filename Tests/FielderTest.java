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
import ui.FieldEventDisplay;

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
		
		
		FieldEventDisplay gameView = new FieldEventDisplay (500,500,10,stadium);

		List <Fielder> fielders = new LinkedList <Fielder> ();
		
		for (Player curPlayer: home.playersOnTeam) {
			Fielder newFielder = new Fielder (curPlayer, 0xFFFFFF);
			fielders.add(newFielder);
		}
		
		int times;
		
		String name = "a";
		times = 1000000;
		//times = 1;
		
		HitTypeCalculator calc = new HitTypeCalculator ();
		calc.init();
		
		GameTeam awayTeam = away.makeInGameTeam(false); 
		
		//FieldEvent event = new FieldEvent (1,gameView,stadium);
		
		//event.placeTestRunner(BaseType.THIRD);
		//event.placeTestRunner(BaseType.SECOND);
		//event.placeTestRunner(BaseType.FIRST);
		
		//event.pitcher = away.playersOnTeam.get(0);
		
		for (int i = 0; i < times; i++) {
				
			Player batter = new Player (Position.FIRST,name,name,times);
			//event.batter = batter;
			batter.generateSimpleStats();
										
			int num = (i%26) + 65;
			name = (char)(num) + "";
			
			System.out.println(i);
			
			//DebuggingBuddy.wait(gameView);
			
		}
		
		System.exit(0);
		
	}
	
}