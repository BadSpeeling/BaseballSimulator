package view;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import game.Game;
import objects.GameTeam;
import player.Player;
import ui.BaseballGameDisplay;
import ui.FieldEventDisplay;
import ui.GameBoxScores;
import ui.TeamBoxScore;
import ui.StatsDisplay;

public class GameContainer extends JLabel {
	
	private GameTeamContainer onOffense = new GameTeamContainer ();
	private GameTeamContainer onDefense = new GameTeamContainer ();
	private FieldEventDisplay fieldDisplay;
	private GameBoxScores boxScore;

	public GameContainer (int width, int height) {
		
		//initialize game view
		setSize(width,height);
		setVisible(true);
		setLayout(new FlowLayout());
		
	}
	
	public void addBaseballGame (Game game) {
		
		fieldDisplay = game.getFieldEventDisp();
		add(fieldDisplay.getFieldImage());
		
		boxScore = new GameBoxScores (new StatsDisplay(game.getHomeTeam()), new StatsDisplay(game.getAwayTeam()));
		add(boxScore);
		
		revalidate();
		repaint();
		
	}
	
	//updates a whole teams box, regardless if there is a change to be made
	public void updateTeamBoxDisp (GameTeam homeTeam, GameTeam awayTeam) {
		
		boxScore.updateHomeBox(homeTeam);
		boxScore.updateAwayBox(awayTeam);
		
	}
		
	public void addScroll (JScrollPane toAdd) {
		
		add(toAdd);
		revalidate();
		repaint();
		
	}
	
	private class GameTeamContainer {
		
		public GameTeam team;
		public StatsDisplay teamBox;
		
	}

	
}
