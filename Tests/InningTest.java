import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import game.Game;
import objects.Fielder;
import player.Player;
import stadium.Stadium;
import team.Team;
import ui.FieldEventDisplay;

public class InningTest {
	
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
		
		FieldEventDisplay gameView = new FieldEventDisplay (500,500, 10, stadium, 1, away.tID, home.tID);

		Game newGame = new Game (home, away, stadium, gameView);
		newGame.playInning();
		
		System.out.println("done!");
		
	}
	
}
