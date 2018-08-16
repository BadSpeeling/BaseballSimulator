package ui;

import java.awt.FlowLayout;

import javax.swing.JPanel;

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
	
}
