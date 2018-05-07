package main;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ball.LocationTracker;
import datatype.Coordinate3D;
import physics.Physics;
import ratings.GeneralRatings;
import stadium.Wall;

public abstract class OnFieldPlayer extends OnFieldObject{

	public GeneralRatings gRats;
	public String fName;
	private double height = 6; //ft
	private double wingspan = 2;
	private double actionTimer = 0;
	
	private List <LocationTracker> tracker = new LinkedList <LocationTracker> ();
	
	public OnFieldPlayer(Coordinate3D loc, GeneralRatings gRats, String fName, int color) {
		super(loc, loc.copy(), color);
		this.gRats = gRats;
		this.fName = fName;
	}
	
	public abstract boolean run (Base [] bases, List <Wall> walls);
	
	//player running
	//toGo is a corrdinate pointing towards destination
	public void move (Coordinate3D toGo) {
		
		double runSpeed = gRats.runSpeed();
		
		double angleToSpot = Physics.angleFromXAxis(toGo);
		double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
		double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;

		//move the player
		lastLoc.x = loc.x;
		lastLoc.y = loc.y;
		loc.add(xDisplacement, yDisplacement, 0);
		
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
