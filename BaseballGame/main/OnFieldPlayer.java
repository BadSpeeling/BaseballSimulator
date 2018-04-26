package main;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ball.LocationTracker;
import datatype.Coordinate3D;
import ratings.GeneralRatings;

public class OnFieldPlayer extends OnFieldObject{

	public GeneralRatings gRats;
	public String fName;
	private double height = 6; //ft
	private double wingspan = 2;
	
	private List <LocationTracker> tracker = new LinkedList <LocationTracker> ();
	
	public OnFieldPlayer(Coordinate3D loc, GeneralRatings gRats, String fName) {
		super(loc, loc.copy());
		this.gRats = gRats;
		this.fName = fName;
	}
	
	public double getReach () {
		return height+wingspan;
	}
	
}
