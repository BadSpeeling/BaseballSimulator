package ui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import game.Game;
import objects.GameTeam;
import player.Player;

/* 
 * The container that holds a teams batting and pitching box scores along with info in between i.e. doubles, homers
 * */
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
	
	public void updateBattingDisp (int id, String [] line) {
		battingStats.updateBox(id, line, 2);
	}
	
	public void updatePitchingDIsp (int id, String [] line) {
		pitchingStats.updateBox(id, line, 1);
	}
	
}
