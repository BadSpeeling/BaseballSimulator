package ball;

import datatype.Coordinate3D;

public class LocationTracker {
	
	public Coordinate3D loc; //location of the ball
	public double time; //time 
	public boolean inAir = true;

	public LocationTracker (Coordinate3D loc, double time, boolean inAir) {
		this.loc = loc;
		this.time = time;
		this.inAir = inAir;
	}
	
}
