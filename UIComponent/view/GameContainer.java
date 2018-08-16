package view;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import game.Game;
import ui.BaseballGameDisplay;
import ui.GameBoxScores;
import ui.TeamBoxScore;
import ui.StatsDisplay;

public class GameContainer extends JFrame {
	
	public GameContainer (int width, int height) {
		
		//initialize game view
		setSize(width,height);
		setVisible(true);
		setLayout(new FlowLayout());
		
	}
	
	public void addBaseballGame (Game game) {
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		
		BaseballGameDisplay disp = game.getFullGameView();
		add(disp);
		
		//GameStatsTable awayBattingStats = new GameStatsTable (Game.battingStatsDisplayed, game.getAwayTeam().initBattingBoxScore());
		//GameStatsTable homeBattingStats = new GameStatsTable (Game.battingStatsDisplayed, game.getHomeTeam().initBattingBoxScore());
		
		StatsDisplay homeBoxDisp = new StatsDisplay (game.getHomeTeam());
		StatsDisplay awayBoxDisp = new StatsDisplay (game.getAwayTeam());
	
		GameBoxScores boxScore = new GameBoxScores (homeBoxDisp, awayBoxDisp);
		add(boxScore);
		
		revalidate();
		repaint();
		
	}
		
	public void addScroll (JScrollPane toAdd) {
		
		add(toAdd);
		revalidate();
		repaint();
		
	}
	
}
