import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import game.Game;
import team.Team;
import ui.TeamBoxScore;
import view.GameContainer;
import view.StatsTable;

public class UITesting {
	
	public static void main (String [] args) {
		
		final int width = 1600;
		final int height = 1000;
		
		Team team1 = Team.generateSimpleTeam(1);
		Team team2 = Team.generateSimpleTeam(2);
		//Game baseballGame = Game.basicGame();
		Game baseballGame = new Game (team1, team2, 1, 1, 2018);	
		
		JFrame frame = new JFrame ("Baseball Game");
		frame.setSize(width, height);
		frame.getContentPane().add(baseballGame.getGameView());
		frame.setVisible(true);
		
		baseballGame.playGame();
		
	}
	
	
}
