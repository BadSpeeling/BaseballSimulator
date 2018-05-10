package main;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import datatype.RandomNumber;
import game.FieldConstants;
import game.Game;
import game.GameLogger;
import messages.FlyballCaughtMsg;
import messages.ForceOutMsg;
import physics.Physics;
import player.GamePlayer;
import player.Position;
import ratings.FieldingRatings;
import stadium.Wall;

//a player that trys to record outs
public class Fielder extends OnFieldPlayer {

	public FieldingRatings fRats;
	public Coordinate3D lastLoc;
	public Position position; //number from 1-9, standard baseball numbering
	public Coordinate3D destination = null; //the coordinate the fielder wants to get to. should be null if the decision needs to be made
	public FielderDecision action = FielderDecision.UNKNOWN;
	public String fullName;
	public GameLogger log;
	public Base baseGuard = null;
	private boolean hasBall = false;
	private BallInPlay ball = null;
	private Coordinate3D throwingDestination = null;
	
	public Fielder (GameLogger log, Coordinate3D loc, GamePlayer player, int color, int id) {
		super(Coordinate3D.standardPos(player.pos), player.gRatings, player.fullName(), color, id);
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = player.pos;
		fullName = player.fullName();
		this.log = log;
	}
	
	public Fielder (GamePlayer cur, int color) {
		super(Coordinate3D.standardPos(cur.pos), cur.gRatings, cur.fullName(), color, cur.pID);
		fRats = cur.fRatings;
		gRats = cur.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = cur.pos;
		fullName = cur.fullName();
	}
	
	public int getMarkerSize () {
		return 1;
	}

	public Coordinate3D getThrowingDestination() {
		return throwingDestination;
	}
	
	public void resetLoc () {
		loc = Coordinate3D.standardPos(position);
	}

	public void setThrowingDestination(Coordinate3D throwingDestination) {
		this.throwingDestination = throwingDestination;
	}
	
	public void flipHasBall () {
		hasBall = !hasBall;
	}

	public boolean hasBall () {
		return hasBall;
	}

	//determine what the fielder should do for a hitball
	public void decideInitAction (BallInPlay curBall) {

	}
	
	//post condition: throwingDestination set to null
	public void throwBall (BallInPlay toThrow, Base [] bases) {

		double throwSpeed = gRats.throwSpeed();
		Coordinate3D spot = throwingDestination.diff(this.loc);
		double angle = Physics.angleFromXAxis(spot);
		toThrow.velocity = new Coordinate3D(throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);
		toThrow.thrown = true;
		ball = null;
		hasBall = false;
		throwingDestination = null;
		toThrow.state = BallStatus.THROWN;
		
		//run to nearest base after throwing ball, set base
		destination = loc.closest(Coordinate3D.basesInOrder());
		
		//dont update if outfielder
		if (!position.isOutField())
			baseGuard = bases[destination.equivBase().num()];
		
	}
	
	//decides who to throw the ball to, or set a new destination to run to
	//kill the play if there is no throw or action needed to be made
	public void throwingBrain (Base [] bases, List <Baserunner> runners, BallInPlay curBall, List <Fielder> fielders) {

		//only do something if there are runners
		if (!runners.isEmpty()) {
			
			Baserunner firstAdvancing = null;
			
			//check if there are any runners attempting a base
			for (Baserunner curRunner: runners) {
								
				if (curRunner.attempt != null) {
					firstAdvancing = curRunner;
				}
				
			}
			
			if (firstAdvancing == null) {
				curBall.state = BallStatus.DEAD;
			}
			
			//no one is advancing, the play should end
			if (firstAdvancing == null) {
				//curBall.state = BallStatus.DEAD;
				return;
			}
			
			Baserunner leadRunner = runners.get(0);
			Base targetBase = leadRunner.attempt;
			
			//check which fielder is covering the intended base
			for (Fielder curFielder: fielders) {

				if (curFielder != this && curFielder.baseGuard == targetBase) {
					throwingDestination = curFielder.loc;
				}

			}

			//if the target is not found, run to the base
			if (throwingDestination == null) {

				baseGuard = targetBase;
				setDestination(targetBase.loc,bases);

			}
			
			//add time for transfer
			else {
				setActionTimer(gRats.windUpTime());
			}

		}
		
		else {
			curBall.state = BallStatus.DEAD;
		}

	}

	//sets the fielders destination equals to a copy of param.  sets baseGuard val
	public void setDestination (Coordinate3D dest, Base [] bases) {
		destination = dest.copy();
		
		int val = destination.equivBase().num();
		
		//set guard value
		if (val != -1)
			baseGuard = bases[val];

		
	}

	/*controls all decisions regarding movement the fielder need to make.  flags actions that need to be taken
	 *curBall - the actual ball that was hit
	 *model - models of the ending position of balls that were hit
	 *returns whether the player is moving
	 */
	public boolean movementBrain (BallInPlay model, Base [] bases, List <Fielder> allFielders, Fielder ballChaser, List <Wall> walls) {

		//set destination to stay in same place
		destination = loc;

		Fielder playerChasingBall = ballChaser;
		boolean hitToRightSide = Physics.radsToDegrees(model.launchDir) < 45; //check which side of the field the ball is hit to

		//run to position
		if (playerChasingBall.position.isOutField()) {

			if (position.equals(Position.FIRST)) {
				destination = FieldConstants.firstBase();
			}

			else if (position.equals(Position.SECOND)) {

				if (hitToRightSide) {
					destination = FieldConstants.std2BCutoff();
				}

				else  {
					destination = FieldConstants.secondBase();
				}

			}

			else if (position.equals(Position.THIRD)) {
				destination = FieldConstants.thirdBase();
			}

			else if (position.equals(Position.SHORT)) {

				if (hitToRightSide) {
					destination = FieldConstants.secondBase();
				}

				else  {
					destination = FieldConstants.stdSSCutoff();
				}

			}

			else if (position.equals(Position.CATCHER)) {
				destination = FieldConstants.homePlate();
			}

			else if (position.equals(Position.PITCHER)) {
				destination = FieldConstants.pitchersMound();
			}

			//OF
			else {

				return false;

			}

		}

		//the ball is not to be fielded by an outfielder
		else {

			if (!position.isOutField()) {
				destination = loc.closest(Coordinate3D.basesInOrder());
			}

		}
		
		//leave base if they were on one
		if (baseGuard != null) {
			baseGuard.leaveBase(this);
		}
		
		setDestination(destination,bases);
		
		return destination != null;
		
		/*
		//update the pointer to targetBase if fielder is running to a base
		if (destination.equals(FieldConstants.firstBase())) {
			baseGuard = bases[(BaseType.FIRST.num())];
		}

		else if (destination.equals(FieldConstants.secondBase())) {
			baseGuard = bases[(BaseType.SECOND.num())];
		}

		else if (destination.equals(FieldConstants.thirdBase())) {
			baseGuard = bases[(BaseType.THIRD.num())];
		}

		else if (destination.equals(FieldConstants.homePlate())) {
			baseGuard = bases[(BaseType.HOME.num())];
		}
		*/

	}

	//updates the player location
	public boolean run (Base [] bases, List <Wall> walls) {
		
		//only move if theres somewhere to go
		if (destination != null) {

			Coordinate3D toGo = this.destination.diff(this.loc);
			double runSpeed = gRats.runSpeed();

			//we are on the base
			if (toGo.mag() < 1) {
				
				destination = null; //remove destination

				//update the base
				if (baseGuard != null) {
					baseGuard.arriveAtBase(this);
				}
				
			}

			//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
			else if (Physics.handleCollision(walls, this.loc) == 0) {

				move(toGo);

				if (hasBall) {
					ball.move(toGo, gRats.runSpeed());
				}

			}
			
			return true;

		}
		
		return false;

	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up. flags that the ball is possessed by a fielder. changes the balls state
	public boolean receiveBall (BallInPlay ball, GameLogger log, List <Baserunner> runners) {

		//check for fly out
		if (ball.canRecordOut) {
			ball.state = BallStatus.DEAD;
			Game.messages.add(new FlyballCaughtMsg(this,runners.get(runners.size()-1)));
			ball.canRecordOut = false;
		}

		//force out is recorded
		if (baseGuard != null && baseGuard.isForceOut()) {

			for (Baserunner runner: runners) {

				if (runner.attempt == baseGuard) {
					Game.messages.add(new ForceOutMsg(this, runner.attempt));
				}

			}

		}
		
		destination = null;
		ball.state = BallStatus.CARRIED;
		pickUpBall(ball);
		ball.velocity.x = 0;
		ball.velocity.y = 0;
		ball.velocity.z = 0;
		return true;

	}
	
	public void resetHasBall () {
		hasBall = false;
	}

	//return true if picking up ball was successful
	public boolean pickUpBall (BallInPlay ball) {
		setActionTimer(gRats.gloveToHandTime());
		this.ball = ball;
		this.hasBall = true;
		return true;
	}

	//return the LocationTracker of the first reachable spot.  returns the final resting spot if nowhere is reachable
	public LocationTracker firstReachableSpot (BallInPlay fullFlightModel) {

		final int SLACK = 15;

		List <LocationTracker> flyBallModel = fullFlightModel.getTracker();

		LocationTracker toReturn = flyBallModel.get(flyBallModel.size()-1);

		for (LocationTracker curLoc: flyBallModel) {

			Coordinate3D cur = curLoc.loc;
			double physicalDistance = Physics.groundDistanceBetween(cur, loc);
			double timeGiven = curLoc.time;

			if ((physicalDistance+SLACK) <= timeGiven * gRats.runSpeed() && cur.z < getReach()) {
				return curLoc;
			}

		}

		return toReturn;

	}

	//returns the closest spot the fielder can get to given location, time and speed
	//ball is a completed model of a ball
	private LocationTracker closestSpot (BallInPlay ball, double airTime) {

		List <LocationTracker> locs = ball.getTracker();

		double speed = gRats.runSpeed();
		LocationTracker ret = locs.get(locs.size()-1); //if no ball is reachable in time, return the last one
		double bestSpaceTimeDist = 0;

		for (LocationTracker cur: locs) {

			double physicalDistance = Physics.distanceBetween(cur.loc,this.loc);

			//the player can reach this ball in an appropriate amount of time
			if (speed * (cur.time + airTime) >= physicalDistance) {

				//finds the space time distance
				double spaceTimeDist = Physics.spaceTimeDistance(cur, this.loc, 0);

				if (spaceTimeDist > bestSpaceTimeDist) {
					bestSpaceTimeDist = spaceTimeDist;
					ret = cur;
				}

			}

		}

		return ret;

	}

	//returns the amount of time taken for the fielder to get to the ball.  looks at the closest route
	//uses the final ball model
	public double timeToBall (Map <String, BallInPlay> model) {

		double time = model.get("fM").airTime;
		LocationTracker closestSpot = closestSpot(model.get("fM"), time);

		return closestSpot.time + time;

	}

	public String toString () {
		return fullName + "" + getID();
	}

}
