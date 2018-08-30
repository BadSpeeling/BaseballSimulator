import javax.swing.JScrollPane;

import game.Game;
import ui.TeamBoxScore;
import view.GameContainer;
import view.StatsTable;

public class UITesting {
	
	public static void main (String [] args) {
		
		final int width = 1600;
		final int height = 1000;
		
		GameContainer fullGame = new GameContainer (width, height);
		Game baseballGame = Game.basicGame();
		
		fullGame.addBaseballGame(baseballGame);
		fullGame.playGame();
		
	}
	
}
