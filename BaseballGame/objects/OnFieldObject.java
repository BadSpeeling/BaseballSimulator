package objects;
import java.util.LinkedList;
import java.util.List;

import ball.LocationTracker;
import datatype.Coordinate3D;
import physics.Physics;

//An on field object is anything moving entity apparent on a baseball field

public abstract class OnFieldObject {

	private Coordinate3D loc;
	private Coordinate3D lastDrawnLoc;
	public Coordinate3D lastLoc;

	private int color;
	
	private List <LocationTracker> tracker;

	
	public OnFieldObject (Coordinate3D loc, Coordinate3D lastLoc, int color) {
		this.loc = new Coordinate3D (loc.x,loc.y,loc.z);
		this.lastLoc = new Coordinate3D (lastLoc.x,lastLoc.y,lastLoc.z);
		this.tracker = new LinkedList <LocationTracker> ();
		this.color = color;
	}
	
	public abstract int getMarkerSize ();
	
	public void track (LocationTracker toAdd) {
		tracker.add(toAdd);
	}

	public Coordinate3D getLastDrawnLoc() {
		return lastDrawnLoc;
	}

	public void setLastDrawnLoc(Coordinate3D lastDrawnLoc) {
		this.lastDrawnLoc = lastDrawnLoc;
	}

	public List <LocationTracker> getTracker () {
		return tracker;
	}
	
	public int getColor () {
		return color;
	}
	
	public Coordinate3D getLoc () {
		return loc;
	}
	
	public void setLoc (Coordinate3D toSet) {
		lastLoc = loc.copy();
		this.loc = toSet.copy();
	}

	public boolean move (Coordinate3D toGo, double runSpeed) {

		double angleToSpot = Physics.angleFromXAxis(toGo);
		double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
		double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;

		if (Double.isFinite(xDisplacement) && Double.isFinite(yDisplacement)) {
			//move the player
			lastLoc = loc.copy();
			loc.add(xDisplacement, yDisplacement, 0);
			return true;
			
		}
		
		else {
			return false;
		}
			
	}

	public boolean move (Coordinate3D disp) {
		loc.add(disp.x, disp.y, disp.z);
		return true;
	}

}
