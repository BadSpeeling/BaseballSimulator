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
		
		for (Player curPlayer: homeTeam.getLineup()) {
			homeBox.updateBattingDisp(curPlayer);
		}
		
		homeBox.updatePitchingDIsp(homeTeam.getCurrentPitcher());
		
	}
	
	public void updateAwayBox (GameTeam awayTeam) {
		
		for (Player curPlayer: awayTeam.getLineup()) {
			awayBox.updateBattingDisp(curPlayer);
		}
		
		awayBox.updatePitchingDIsp(awayTeam.getCurrentPitcher());
		
	}
	
}
