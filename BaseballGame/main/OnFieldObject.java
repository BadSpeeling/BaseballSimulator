package main;
import java.util.LinkedList;
import java.util.List;

import ball.LocationTracker;
import datatype.Coordinate3D;

//An on field object is anything moving entity apparent on a baseball field

public class OnFieldObject {
	
	public Coordinate3D loc;
	public Coordinate3D lastLoc;
	
	private List <LocationTracker> tracker;
	
	public OnFieldObject (Coordinate3D loc, Coordinate3D lastLoc) {
		this.loc = new Coordinate3D (loc.x,loc.y,loc.z);
		this.lastLoc = new Coordinate3D (lastLoc.x,lastLoc.y,lastLoc.z);
		this.tracker = new LinkedList <LocationTracker> ();
	}
	
	public void track (LocationTracker toAdd) {
		tracker.add(toAdd);
	}
	
	public List <LocationTracker> getTracker () {
		return tracker;
	}
	
	public void move (Coordinate3D disp) {
		lastLoc.x = loc.x;
		lastLoc.y = loc.y;
		lastLoc.z = loc.z;
		loc.add(disp.x, disp.y, disp.z);
	}
	
}
