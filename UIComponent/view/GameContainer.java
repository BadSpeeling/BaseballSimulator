package view;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import game.Game;
import objects.GamePlayer;
import objects.GameTeam;
import ui.BaseballGameDisplay;
import ui.GameBoxScores;
import ui.TeamBoxScore;
import ui.StatsDisplay;

public class GameContainer extends JFrame {
	
	private GameTeamContainer onOffense = new GameTeamContainer ();
	private GameTeamContainer onDefense = new GameTeamContainer ();
	private Game game;
	
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
		
		onDefense.teamBox = new StatsDisplay (game.getHomeTeam());
		onOffense.teamBox = new StatsDisplay (game.getAwayTeam());
		
		onDefense.team = game.getHomeTeam();
		onOffense.team = game.getAwayTeam();
		
		GameBoxScores boxScore = new GameBoxScores (onDefense.teamBox, onOffense.teamBox);
		add(boxScore);
		
		this.game = game;
		
		revalidate();
		repaint();
		
	}
	
	public void playGame () {
		
		while (!game.isGameOver()) {
			
			game.playPlateAppearance();
			//updateBoxScores();
			
			//if (game.didTeamsSwitch()) {
				
			//	GameTeamContainer temp = onOffense;
			//	onOffense = onDefense;
			//	onDefense = temp;
				
			//}
			
		}
		
	}
	
	public void updateBoxScores () {
		
		updateTeamBoxDisp(onOffense.team, onOffense.teamBox);
		updateTeamBoxDisp(onDefense.team, onDefense.teamBox);
		
	}
	
	//updates a whole teams box, regardless if there is a change to be made
	public void updateTeamBoxDisp (GameTeam team, StatsDisplay toUpdate) {
		
		for (GamePlayer curPlayer: team.getLineup()) {
			toUpdate.updateBattingDisp(curPlayer);
		}
		
		toUpdate.updatePitchingDIsp(team.getCurrentPitcher());
		
		revalidate();
		repaint();
		
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
