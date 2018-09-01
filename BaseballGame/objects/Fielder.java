package objects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.FieldEvent;
import game.Game;
import numbers.RandomNumber;
import physics.Physics;
import player.Position;
import ratings.FieldingRatings;
import stadium.Wall;
import stats.BattingStatline;
import stats.PitchingStatline;

//a player that trys to record outs
public class Fielder extends OnFieldPlayer {

	public Coordinate3D lastLoc;
	public Coordinate3D destination = null; //the coordinate the fielder wants to get to. should be null if the decision needs to be made
	public Base baseGuard = null;
	private boolean hasBall = false;
	private BallInPlay ball = null;
	private Coordinate3D throwingDestination = null;
	private boolean throwingDecisionMade = false;
	public Base baseOn = null;
	
	public Fielder (GamePlayer cur, int color) {
		super(cur, Coordinate3D.standardPos(cur.getPos()), color, cur.getCurGameBatting(), cur.getCurGamePitching());
		lastLoc = new Coordinate3D(0,0,0);
	}
	
	public boolean needToMakeThrowingDecision () {
		return !throwingDecisionMade;
	}
	
	public int getMarkerSize () {
		return 1;
	}

	public Coordinate3D getThrowingDestination() {
		return throwingDestination;
	}
	
	public void resetLoc () {
		setLoc(Coordinate3D.standardPos(getPlayer().getPos()));
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
	
	//post condition: throwingDestination set to null
	public void throwBall (BallInPlay toThrow, Base [] bases) {

		double throwSpeed = getPlayer().getgRatings().throwSpeed();
		Coordinate3D spot = throwingDestination.diff(toThrow.getLoc());
		double angle = Physics.angleFromXAxis(spot);
		toThrow.velocity = new Coordinate3D(throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);
		toThrow.loose = true;
		ball = null;
		hasBall = false;
		throwingDestination = null;
		toThrow.state = BallStatus.THROWN;
		
		//run to nearest base after throwing ball, set base
		destination = getLoc().closest(Coordinate3D.basesInOrder());
		
		//dont update if outfielder
		if (!getPlayer().getPos().isOutField())
			baseGuard = bases[destination.equivBase().num()];
		
	}
	
	public void setThrowingDecisionMade(boolean throwingDecisionMade) {
		this.throwingDecisionMade = throwingDecisionMade;
	}
	
	//true if throwingbrain has been called one.  throwingbrain will always set value to true
	public boolean getThrowingDecisionMade () {
		return throwingDecisionMade;
	}

	//decides who to throw the ball to, or set a new destination to run to
	//kill the play if there is no throw or action needed to be made
	//returns the fielder the ball is being thrown to
	public Fielder throwingBrain (Base [] bases, List <Baserunner> runners, BallInPlay curBall, List <Fielder> fielders) {
		
		Fielder ret = null;
		throwingDecisionMade = true;
		
		//only do something if there are runners
		if (!runners.isEmpty()) {
			
			Base targetBase = runners.get(0).getAttempt();

			//the base we want to throw to is the base we are on
			if (targetBase == baseOn) {
				return null;
			}
			
			//do nothing
			if (targetBase == null) {
				return null;
			}
			
			else {
				
				Fielder throwTo = targetBase.getFielderOn();
				
				//throw the ball to the defended base
				if (throwTo != null) {
					throwingDestination = targetBase.getLoc();
					return throwTo;
				}
				
				//run to base yourself
				else {
					setDestination(targetBase.getLoc(), bases);
					return null;
				}
				
			}
			
		}
		
		else {
			curBall.state = BallStatus.DEAD;
		}
		
		return null;

	}

	//sets the fielders destination equals to a copy of param.  sets baseGuard val
	public void setDestination (Coordinate3D dest, Base [] bases) {
		destination = dest.copy();
		
		int val = destination.equivBase().num();
		
		//set guard value
		if (val != -1)
			baseGuard = bases[val];

		
	}
	
	//of possible baserunner to get out, returns the shortest throwing distance
	public Baserunner easiestOut (List <Baserunner> runners) {
		
		Baserunner ret = null;
		double shortestDist = Double.MAX_VALUE;
		
		for (Baserunner curRunner: runners) {
			
			//if this baserunner is not advancing, go to next runner
			if (curRunner.getAttempt() == null || curRunner.getDestination() == null) {
				continue;
			}
			
			double groundDist = curRunner.getAttempt().getLoc().diff(getLoc()).mag2D();
			
			double timeToBase = curRunner.timeToDestination(curRunner.getDestination());
			double timeForThrow = timeForThrow(groundDist);
			
			
			//check if there is enohgh time for a throw
			if (timeToBase > timeForThrow) {
				
				if (shortestDist > groundDist) {
					shortestDist = groundDist;
					ret = curRunner;
				}
				
			}
			
		}
		
		return ret;
		
	}
	
	//time needed for a throw to reach the base
	public double timeForThrow (double dist) {
		
		//double ret = getPlayer().getgRatings().gloveToHandTime() + getPlayer().getgRatings().windUpTime(); //factor in animation time
		double ret = 0;
		return ret + (dist/getPlayer().getfRatings().throwingSpeed());
		
	}
	
	/*controls all decisions regarding movement the fielder need to make.  flags actions that need to be taken
	 *curBall - the actual ball that was hit
	 *model - models of the ending position of balls that were hit
	 *returns whether the player is moving
	 */
	public boolean movementBrain (BallInPlay model, Base [] bases, List <Fielder> allFielders, Fielder ballChaser, List <Wall> walls) {
		
		if (hasBall == true) {
			destination = null;
			return false;
		}
		
		//set destination to stay in same place
		destination = getLoc();

		Fielder playerChasingBall = ballChaser;
		boolean hitToRightSide = Physics.radsToDegrees(model.launchDir) < 45; //check which side of the field the ball is hit to

		//run to position
		if (playerChasingBall.getPlayer().getPos().isOutField()) {

			if (getPlayer().getPos().equals(Position.FIRST)) {
				destination = FieldConstants.firstBase();
			}

			else if (getPlayer().getPos().equals(Position.SECOND)) {

				if (hitToRightSide) {
					destination = FieldConstants.std2BCutoff();
				}

				else  {
					destination = FieldConstants.secondBase();
				}

			}

			else if (getPlayer().getPos().equals(Position.THIRD)) {
				destination = FieldConstants.thirdBase();
			}

			else if (getPlayer().getPos().equals(Position.SHORT)) {

				if (hitToRightSide) {
					destination = FieldConstants.secondBase();
				}

				else  {
					destination = FieldConstants.stdSSCutoff();
				}

			}

			else if (getPlayer().getPos().equals(Position.CATCHER)) {
				destination = FieldConstants.homePlate();
			}

			else if (getPlayer().getPos().equals(Position.PITCHER)) {
				destination = FieldConstants.pitchersMound();
			}

			//OF
			else {

				return false;

			}

		}

		//the ball is not to be fielded by an outfielder
		else {

			if (!getPlayer().getPos().isOutField()) {
				destination = getLoc().closest(Coordinate3D.basesInOrder());
			}

		}
		
		//leave base if they were on one
		if (baseGuard != null) {
			leaveBase(baseGuard);
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
	
	public void setTarget (Base toGo) {
		destination = toGo.getLoc().copy();
		baseGuard = toGo;
	}
	
	public void leaveBase (Base leave) {
		baseOn = null;
		leave.setFielderOn(null);
	}
	
	//returns true if there was a force out at the base
	public boolean arriveAtBase (Base arrivedAt) {
		
		Baserunner force = arrivedAt.getToBeForced();
		baseGuard.setFielderOn(this);
		baseOn = arrivedAt;
		
		//force out
		if (force != null && arrivedAt.isForceOut() && hasBall) {
			return true;
		}
		
		//no force out
		else {
			return false;
		}
		
	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up. flags that the ball is possessed by a fielder. changes the balls state
	//post-condition: fielders destination will be set to null
	public boolean receiveBall (BallInPlay ball) {

		ball.loose = false;
		destination = null;
		ball.state = BallStatus.CARRIED;
		pickUpBall(ball);
		ball.clearThrownTo();
		ball.velocity.x = 0;
		ball.velocity.y = 0;
		ball.velocity.z = 0;
		return true;

	}
	
	public Base getBaseOn () {
		return baseOn;
	}
		
	public void resetHasBall () {
		hasBall = false;
	}
	
	//returns the ground distance from the player to the ball
	public double distanceFromBall (Coordinate3D ballLoc) {
		return getLoc().diff(ballLoc).mag2D();
	}

	//return true if picking up ball was successful
	public boolean pickUpBall (BallInPlay ball) {
		setActionTimer(getPlayer().getgRatings().gloveToHandTime());
		ball.getLoc().z = getLoc().z;
		this.ball = ball;
		ball.setHolding(this);
		this.hasBall = true;
		return true;
	}

	//return the LocationTracker of the first reachable spot.  returns the final resting spot if nowhere is reachable
	public LocationTracker firstReachableSpot (BallInPlay fullFlightModel) {
				
		final int SLACK = 2;
		double delay = getPlayer().getgRatings().reactionTime();
		
		List <LocationTracker> flyBallModel = fullFlightModel.getTracker();

		LocationTracker toReturn = flyBallModel.get(flyBallModel.size()-1);
		
		for (LocationTracker curLoc: flyBallModel) {

			Coordinate3D cur = curLoc.loc;
			double physicalDistance = Physics.groundDistanceBetween(cur, getLoc()); //how far away the ball is
			double timeGiven = curLoc.time - delay; //amount of running time the player has
			
			double rangeInTime = (timeGiven * getPlayer().getgRatings().getSpeed()) + getReach()/2; //distance the player can run
			boolean canCatchBall = cur.z < getReach();
			
			if ((physicalDistance+SLACK) <= rangeInTime && canCatchBall) {
				return curLoc;
			}

		}

		return toReturn;

	}
	
	public void reset () {
		resetHasBall();
		resetLoc();
		destination = null;
		baseGuard = null;
		baseOn = null;
		setThrowingDecisionMade(false);
		setThrowingDestination(null);
		setActionTimer(getPlayer().getgRatings().reactionTime());
	}

	//returns the closest spot the fielder can get to given location, time and speed
	//ball is a completed model of a ball
	private LocationTracker closestSpot (BallInPlay ball) {

		List <LocationTracker> locs = ball.getTracker();

		double speed = getPlayer().getgRatings().getSpeed();
		LocationTracker ret = locs.get(locs.size()-1); //if no ball is reachable in time, return the last one
		final double timeDelay = getPlayer().getgRatings().reactionTime(); //delay time i.e. reaction
		
		for (LocationTracker cur: locs) {
			
			//only check if the ball is within z
			if (cur.loc.z < getReach()) {
			
				double availTime = cur.time-timeDelay; //calc amount of time available to get to ball
				double distCover = cur.loc.diff(getLoc()).mag2D();
				
				//has enough time to get to the ball
				if (availTime * speed > distCover) {
					return cur;
				}
				
			}
			
		}

		return ret;

	}
	
	//returns if the ball is within the reach of the fielder
	public boolean canGrabBall (Coordinate3D hitBallLoc) {
		return hitBallLoc.diff(getLoc()).mag2D() <= this.getReach()/2 && hitBallLoc.z < getReach();
	}

	//returns the amount of time taken for the fielder to get to the ball.  looks at the closest route
	//uses the final ball model
	public LocationTracker timeToBall (BallInPlay model) {

		LocationTracker closestSpot = closestSpot(model);
		return closestSpot;

	}
	 

	public String toString () {
		return getPlayer().fullName() + "" + getID() + " " + getPlayer().getPos().toString();
	}

}
