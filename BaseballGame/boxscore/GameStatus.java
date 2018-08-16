package boxscore;

import javax.swing.JLabel;

import game.InningCounters;

public class GameStatus extends JLabel {
	
	private int homeRuns = 0;
	private int awayRuns = 0;
	private int outs = 0;
	private int inning = 1;
	private boolean top = true;
	
	public GameStatus () {
		super();
	}
	
	public void upDate (InningCounters ctr, int homeRuns, int awayRuns) {
		outs = ctr.getOuts();
		inning = ctr.getInning();
		top = ctr.isTop();
		this.homeRuns = homeRuns;
		this.awayRuns = awayRuns;
	}
	
	public String toString () {
		
		String topStr = "";
		
		if (top) {
			topStr = "Top of";
		}
		
		else {
			topStr = "Bottom of";
		}
		
		return "Away: " + awayRuns + " Home: " + homeRuns + " Outs: " + outs + " " + topStr + " " + inning;  
	}
	
}
