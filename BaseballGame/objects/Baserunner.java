package objects;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ball.BallInPlay;
import ball.LocationTracker;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.Game;
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
	public Base attempt = null;
	private Base homeBase;
	private boolean advancing = true;
	private int bestBaseAchieved = -1;

	public Baserunner (GeneralRatings gRatings, String fName, int color, int id) {
		super (FieldConstants.homePlate(), gRatings, fName, color, id);
		destinations = new LinkedList <Coordinate3D> ();
		this.fName = fName;
	}

	public Baserunner (Player other, int color) {
		super(FieldConstants.homePlate(), other.gRatings, other.fullName(), color, other.pID);
		destinations = new LinkedList <Coordinate3D> ();
	}

	//to be used to initialize a baserunner  
	public void init (BaseType base, Base [] bases) {

		int numBase = base.num();
		this.baseOn = bases[numBase];
		advancing = true;
		attempt = null;
		destinations.clear();
		destination = null;
		loc = baseOn.getBase().equiv();
		lastLoc = loc.copy();
		bestBaseAchieved = -1;
		homeBase = this.baseOn;
		//clear and set base
		this.baseOn.leaveBase(this);
		this.baseOn.arriveAtBase(this);

	}

	public int getMarkerSize () {
		return 1;
	}

	public void advancing () {
		advancing = true;
	}

	public int getBestBaseAchieved() {
		return bestBaseAchieved;
	}

	public void setBestBaseAchieved(int bestBaseAchieved) {
		this.bestBaseAchieved = bestBaseAchieved;
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

	public void setBestBase (int num) {
		bestBaseAchieved = num;
	}

	//determines which base the batter can get to
	public int batterBaseBrain (BallInPlay model, List <Fielder> fielders, Fielder chaser, BallInPlay curBall, Base [] bases) {

		baseOn = bases[3];

		int basesTake = 0;
		//find the time it will take for the ball to be fielded
		LocationTracker timeToPickBall = chaser.timeToBall(model);
		double throwDistance = timeToPickBall.loc.diff(FieldConstants.pitchersMound()).mag2D();
		double armStrength = chaser.gRats.throwSpeed();
		
		double throwTime = throwDistance/armStrength;
		throwTime += timeToPickBall.time;
		
		double distanceRunnerCanCover = throwTime * gRats.runSpeed();

		//determine what base to run to
		if (distanceRunnerCanCover > 270) {
			basesTake = 3;
		}
		
		else if (distanceRunnerCanCover > 180) {
			basesTake = 2;
		}
		
		else {
			basesTake = 1;
		}
		
		return basesTake;

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
		
		if (attempt == null) {
			return;
		}
		
		BaseType on = attempt.getBase();

		while (on != homeBase.getBase()) {
			on = on.prevBase();
			destinations.add(on.equiv());
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
