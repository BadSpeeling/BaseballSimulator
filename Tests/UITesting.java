import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import game.Game;
import ui.TeamBoxScore;
import view.GameContainer;
import view.StatsTable;

public class UITesting {
	
	public static void main (String [] args) {
		
		
		final int width = 1600;
		final int height = 1000;
		
		Game baseballGame = Game.basicGame();
				
		JFrame frame = new JFrame ("Baseball Game");
		frame.setSize(width, height);
		frame.getContentPane().add(baseballGame.getGameView());
		frame.setVisible(true);
		
		baseballGame.playGame();
		
	}
	
	
}
