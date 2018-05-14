package main;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ball.BallInPlay;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.Game;
import game.GameLogger;
import messages.AdvancingNumberOfBases;
import physics.Physics;
import player.Player;
import ratings.GeneralRatings;
import stadium.Wall;

public class Baserunner extends OnFieldPlayer {

	public Queue <Coordinate3D> destinations;
	public Coordinate3D destination = null;
	public Coordinate3D lastLoc = new Coordinate3D (0,0,0);
	public Base baseOn = null;
	public GameLogger log;
	public Base attempt = null;
	private Base homeBase;
	private boolean advancing = true;

	public Baserunner (GameLogger log, GeneralRatings gRatings, String fName, int color, int id) {
		super (FieldConstants.homePlate(), gRatings, fName, color, id);
		destinations = new LinkedList <Coordinate3D> ();
		this.fName = fName;
		this.log = log;
	}

	public Baserunner (Player other, GameLogger log, int color) {
		super(FieldConstants.homePlate(), other.gRatings, other.fullName(), color, other.pID);
		destinations = new LinkedList <Coordinate3D> ();
		this.log = log;
	}
	
	public int getMarkerSize () {
		return 1;
	}

	public void advancing () {
		advancing = true;
	}

	public void setHomeBase (Base home) {
		homeBase = home;
	}

	public Base getHomeBase () {
		return homeBase;
	}

	//adds the bases to run to to the queue
	//returns whether of not the player will be moving
	public boolean baserunnerBrain (int basesTake) {

		BaseType temp = baseOn.getBase();
		
		for (int i = 0; i < basesTake; i++) {
			destinations.add(temp.nextDestination());
			temp = temp.nextBase();
		}
		
		return !destinations.isEmpty();
		
	}
	
	public boolean run (Base [] bases, List <Wall> walls) {
		
		if (destination == null) {
			
			//we are done running for now
			if (destinations.isEmpty()) {
				attempt = null;
				return false;
			}
			
			else {
				
				//set destination
				destination = destinations.poll();
				
				if (baseOn != null) {
					baseOn.leaveBase(this);
					baseOn = null;
				}
				
				int baseNum = destination.equivBase().num();
				
				if (baseNum != -1)
					attempt = bases[baseNum];
				
			}
			
		}
		
		else {
			
			move(destination.diff(loc));
			
			if (Physics.within(destination.diff(loc), 2.0)) {
				if (attempt.arriveAtBase(this))
					baseOn = attempt;
				destination = null;
			}
			
		}
		
		return true;
		
	}

	public void setBaseOn (Base set) {
		baseOn = set;
		loc = baseOn.getBase().equiv();
	}

	//determines which base the batter can get to
	public void batterBaseBrain (Map <String, BallInPlay> models, List <Fielder> fielders, BallInPlay curBall, Base [] bases) {
		
		baseOn = bases[3];

		int basesTake = 0;
		//find the time it will take for the ball to be fielded
		double timeTaken = Double.MAX_VALUE;

		for (int i = 0; i < Game.RIGHTNUM; i++) {
			double ret = fielders.get(i).timeToBall(models);
			timeTaken = Math.min(timeTaken, ret);
		}

		double distanceRunnerCanCover = timeTaken * gRats.runSpeed();

		//determine what base to run to
		if (distanceRunnerCanCover < 75) {
			basesTake = 1;
		}

		else if (distanceRunnerCanCover < 140) {
			basesTake = 2;
		}

		else {
			basesTake = 3;
		}

		Game.messages.add(new AdvancingNumberOfBases(basesTake));

	}
	
	public boolean isAttempting () {
		return attempt != null;
	}
	
	//run back to the home base.  set advancing to false
	public void returnToHomeBase () {
		
		if (homeBase == null) {
			return;
		}
		
		advancing = false;
		destination = null;
		destinations.clear();
		BaseType on = attempt.getBase();

		while (on != homeBase.getBase()) {
			on = on.prevBase();
			destinations.add(on.equiv());
			System.out.println(on);
		}

		homeBase.setForceOut(true);

	}

	//gives the next base in order
	public Coordinate3D nextBase (Coordinate3D at) {

		if (at.equals(FieldConstants.firstBase())) {
			return FieldConstants.secondBase();
		}

		else if (at.equals(FieldConstants.secondBase())) {
			return FieldConstants.thirdBase();
		}

		else {
			return FieldConstants.homePlate();
		}

	}

	public String toString () {
		return this.fName + " " + getID();
	}

}
