package ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import game.Game;
import player.Player;
import team.GameTeam;

public class StatsDisplay extends JPanel {
	
	private TeamBoxScore battingStats;
	private JLabel inbetweenInfo;
	private TeamBoxScore pitchingStats;
	
	public StatsDisplay (GameTeam team) {
		
		Player [] toSend = {team.getCurrentPitcher()};
		
		battingStats = new TeamBoxScore (Game.battingStatsDisplayed, team.initBattingBoxScore(), team.getLineup());
		pitchingStats = new TeamBoxScore (Game.pitchingStatsDisplayed, team.initPitchingBoxScore(), toSend);
		
		JScrollPane batting = battingStats.getScrollView();
		batting.setVisible(true);
		add(batting);
		
		JScrollPane pitching = pitchingStats.getScrollView();
		pitching.setVisible(true);
		add(pitching);
		
		setVisible(true);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
	}
	
}
