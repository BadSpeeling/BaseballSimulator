package main;
import java.util.LinkedList;
import java.util.List;

import ball.LocationTracker;
import datatype.Coordinate3D;
import physics.Physics;

//An on field object is anything moving entity apparent on a baseball field

public class OnFieldObject {

	public Coordinate3D loc;
	public Coordinate3D lastLoc;

	private int color;
	
	private List <LocationTracker> tracker;

	public OnFieldObject (Coordinate3D loc, Coordinate3D lastLoc, int color) {
		this.loc = new Coordinate3D (loc.x,loc.y,loc.z);
		this.lastLoc = new Coordinate3D (lastLoc.x,lastLoc.y,lastLoc.z);
		this.tracker = new LinkedList <LocationTracker> ();
		this.color = color;
	}

	public void track (LocationTracker toAdd) {
		tracker.add(toAdd);
	}

	public List <LocationTracker> getTracker () {
		return tracker;
	}
	
	public int getColor () {
		return color;
	}

	public void move (Coordinate3D toGo, double runSpeed) {

		double angleToSpot = Physics.angleFromXAxis(toGo);
		double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
		double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;

		//move the player
		lastLoc.x = loc.x;
		lastLoc.y = loc.y;
		loc.add(xDisplacement, yDisplacement, 0);

	}

	public void move (Coordinate3D disp) {
		lastLoc.x = loc.x;
		lastLoc.y = loc.y;
		lastLoc.z = loc.z;
		loc.add(disp.x, disp.y, disp.z);
	}

}
