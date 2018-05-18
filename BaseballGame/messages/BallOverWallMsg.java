package messages;

import objects.Baserunner;

public class BallOverWallMsg extends Message {
	
	public boolean homeRun;
	public Baserunner batter;
	
	public BallOverWallMsg(boolean homeRun, Baserunner batter) {
		super();
		this.homeRun = homeRun;
		this.batter = batter;
	}
	
	public String toString () {
		
		if (homeRun) {
			return batter + " has hit a homerun";
		}
		
		else {
			return batter + " has hit a ground-rule double";
		}
		
	}
	
}
