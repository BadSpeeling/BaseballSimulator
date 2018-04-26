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
import player.GamePlayer;
import ratings.GeneralRatings;

public class Baserunner extends OnFieldPlayer {

	public Queue <Coordinate3D> destinations;
	public Coordinate3D destination = null;
	public Coordinate3D lastLoc = new Coordinate3D (0,0,0);
	public BaseType baseOn = BaseType.NONE;
	public GameLogger log;
	public Base attempt;
	private Base homeBase;
	private boolean advancing = true;
	
	Baserunner (GameLogger log, GeneralRatings gRatings, String fName, BaseType on) {
		super (on.equiv(), gRatings, fName);
		destinations = new LinkedList <Coordinate3D> ();
		baseOn = on;
		this.fName = fName;
		this.log = log;
	}

	public Baserunner (GamePlayer other, GameLogger log, BaseType on) {
		super(on.equiv(), other.gRatings, other.fullName());
		destinations = new LinkedList <Coordinate3D> ();
		this.log = log;
		baseOn = on;
	}
	
	public void setHomeBase (Base home) {
		homeBase = home;
	}
	
	public Base getHomeBase () {
		return homeBase;
	}

	public void baserunnerBrain (int basesTake) {

		BaseType temp = baseOn;

		for (int i = 0; i < basesTake; i++) {
			destinations.add(temp.nextDestination());
			temp = temp.nextBase();
		}

	}

	public void setBaseOn (BaseType set) {
		baseOn = set;
		loc = baseOn.equiv();
	}

	//determines which base the batter can get to
	public void batterBaseBrain (Map <String, BallInPlay> models, List <Fielder> fielders, BallInPlay curBall) {

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
	
	public void returnToHomeBase () {
		
		advancing = false;
		destination = null;
		destinations.clear();
		BaseType on = attempt.getBase();
		
		while (on != homeBase.getBase()) {
			on = on.prevBase();
			destinations.add(on.equiv());
		}
		
		destination = destinations.poll();
		
	}
	
	//run to the destination, clear for next destination if reached
	public void run (Base [] bases) {

		//get a new destination
		if (destination == null) {

			//we have a new base to go to
			if (!destinations.isEmpty()) {

				destination = destinations.poll();
				
				int baseGoingTo = -1;
				
				//get pointer to base attemping
				if (advancing) {
					baseGoingTo = baseOn.nextBase().num();
				}
				
				else {
					baseGoingTo = baseOn.prevBase().num();
				}
				
				if (attempt != null) {
					attempt.leaveBase(this);
				}

				attempt = bases[baseGoingTo];

			}
			
			//we are done advancing bases
			else {
				attempt = null;
			}

		}

		//run to the new destination
		else {
			
			Coordinate3D toGo = this.destination.diff(this.loc);

			//not close enough to destination, keep on going
			if (toGo.mag() > 1) {

				double angle = Physics.angleFromXAxis(toGo);
				double speed = gRats.runSpeed();

				this.lastLoc.x = this.loc.x;
				this.lastLoc.y = this.loc.y;

				this.loc.x += Physics.tick*speed*Math.cos(angle);
				this.loc.y += Physics.tick*speed*Math.sin(angle);
			}

			//arrived at base
			else {

				attempt.arriveAtBase(this);

				if (destination.equals(FieldConstants.firstBase())) {baseOn = BaseType.FIRST;}
				else if (destination.equals(FieldConstants.secondBase())) {baseOn = BaseType.SECOND;}
				else if (destination.equals(FieldConstants.thirdBase())) {baseOn = BaseType.THIRD;} 
				else {baseOn = BaseType.HOME;}
				destination = null; 
				
			}

		}

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
		return this.fName;
	}

}
