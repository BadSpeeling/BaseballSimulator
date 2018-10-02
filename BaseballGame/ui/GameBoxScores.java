package ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

import objects.GameTeam;
import player.Player;

public class GameBoxScores extends JPanel {
	
	private StatsDisplay homeBox;
	private StatsDisplay awayBox;
	
	public GameBoxScores (StatsDisplay homeBox, StatsDisplay awayBox) {
		
		this.homeBox = homeBox;
		this.awayBox = awayBox;
		
		add(this.homeBox);
		add(this.awayBox);
		setLayout(new FlowLayout());
		setVisible(true);
	
	}

	public StatsDisplay getHomeBox() {
		return homeBox;
	}

	public StatsDisplay getAwayBox() {
		return awayBox;
	}
	
	public void updateHomeBox (GameTeam homeTeam) {
		
		for (Integer id: homeTeam.getBattingsStatsKeys()) {
			homeBox.updateBattingDisp(id, homeTeam.generateCurBattingLineFor(id));
		}
		
		homeBox.updatePitchingDIsp(homeTeam.getCurrentPitcher().getpID(), homeTeam.generateCurPitchingLineFor(homeTeam.getCurrentPitcher().getpID()));
		
	}
	
	public void updateAwayBox (GameTeam awayTeam) {
		
		for (Integer id: awayTeam.getBattingsStatsKeys()) {
			awayBox.updateBattingDisp(id, awayTeam.generateCurBattingLineFor(id));
		}
		
		awayBox.updatePitchingDIsp(awayTeam.getCurrentPitcher().getpID(), awayTeam.generateCurPitchingLineFor(awayTeam.getCurrentPitcher().getpID()));
		
	}
	
}
