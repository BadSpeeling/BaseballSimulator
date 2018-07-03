package objects;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ball.LocationTracker;
import datatype.Coordinate3D;
import physics.Physics;
import player.Player;
import ratings.GeneralRatings;
import stadium.Wall;
import stats.BattingStatline;
import stats.PitchingStatline;

public abstract class OnFieldPlayer extends OnFieldObject{

	private double height = 6; //ft
	private double wingspan = 2;
	private double actionTimer = 0;
	private Player backingPlayer;
	private BattingStatline battingStats;
	private PitchingStatline pitchingStats;
	
	private List <LocationTracker> tracker = new LinkedList <LocationTracker> ();
	
	public OnFieldPlayer(Player player, Coordinate3D loc,int color, BattingStatline bS, PitchingStatline pS) {
		super(loc, loc.copy(), color);
		backingPlayer = player;
		this.battingStats = bS;
		this.pitchingStats = pS;
	}
	
	//player running
	//toGo is a corrdinate pointing towards destination
	public boolean move (Coordinate3D toGo) {
		
		double runSpeed = backingPlayer.getgRatings().getSpeed();
		
		double angleToSpot = Physics.angleFromXAxis(toGo);
		double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
		double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;
		
		if (Double.isFinite(xDisplacement) && Double.isFinite(yDisplacement)) {
			//move the player
			getLoc().add(xDisplacement, yDisplacement, 0);
			return true;
			
		}
		
		return false;
		
	}
	
	public Player getPlayer () {
		return backingPlayer;
	}
	
	public String getName () {
		return backingPlayer.fullName();
	}
	
	public double timeToDestination (Coordinate3D target) {
		
		double distToCover = target.diff(getLoc()).mag2D();
		return distToCover/backingPlayer.getgRatings().getSpeed();
		
	}
	
	public int getID () {
		return backingPlayer.getpID();
	}
	
	public void setActionTimer (double val) {
		actionTimer = val;
	}
	
	public void decrementActionTimer () {
		if (actionTimer > 0) 
			actionTimer -= Physics.tick;
	}
	
	public boolean canPerformAction () {
		return actionTimer <= 0;
	}
	
	public double getReach () {
		return height+wingspan;
	}
	
}
