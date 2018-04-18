import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//a player that trys to record outs
public class Fielder extends OnFieldPlayer {

	FieldingRatings fRats;
	Coordinate3D lastLoc;
	Position position; //number from 1-9, standard baseball numbering
	Coordinate3D destination = null; //the coordinate the fielder wants to get to. should be null if the decision needs to be made
	FielderDecision action = FielderDecision.UNKNOWN;
	String fullName;
	LinkedList <Coordinate3D> dimensions; //TODO make static
	FieldEvent status;
	GameLogger log;
	Base targetBase = null;
	
	public Fielder (FieldEvent status, GameLogger log, Coordinate3D loc, GamePlayer player, LinkedList <Coordinate3D> dimensions) {
		super(Coordinate3D.standardPos(player.pos), player.gRatings, player.fullName(), new LinkedList <LocationTracker> ());
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = player.pos;
		fullName = player.fullName();
		this.dimensions = dimensions;
		this.status = status;
		this.log = log;
	}

	//determine what the fielder should do for a hitball
	public void decideInitAction (BallInPlay curBall) {

	}

	public void throwBall (BallInPlay toThrow, Coordinate3D target) {

		double throwSpeed = 80; //in ft/s
		Coordinate3D spot = target.diff(this.loc);
		double angle = Physics.angleFromXAxis(spot);
		toThrow.velocity = new Coordinate3D(throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);
		toThrow.thrown = true;
		toThrow.state = BallStatus.THROWN;

	}

	//decides who to throw the ball to.  status will be updated to rep who is throwing and who is being thrown to
	public void throwingBrain (GameLogger log, FieldEvent status, List <Baserunner> runner, BallInPlay curBall) {

		status.thrower = this;

		if (position.equals(Position.SECOND) || position.equals(Position.SHORT)) {
			status.beingThrownTo = status.fOnFirst;
		}

		else if (position.equals(Position.LEFT) ||position.equals(Position.CENTER) || position.equals(Position.RIGHT)) {
			status.beingThrownTo = status.fCutoff;
		}

		else if (position.equals(Position.FIRST)) {
			status.beingThrownTo = status.fOnThird;
		}

		else if (position.equals(Position.THIRD)) {
			status.beingThrownTo = status.fOnSecond;
		}

		throwBall(curBall, status.beingThrownTo.loc);
		log.add(GameEvent.threwBall(this.fullName, status.beingThrownTo.fullName));

	}

	//controls all decisions regarding movement the fielder need to make.  flags actions that need to be taken
	//curBall - the actual ball that was hit
	//model - models of the ending position of balls that were hit
	public void movementBrain (BallInPlay curBall, Map <String, BallInPlay> model, List <Base> bases) {

		destination = firstReachableSpot(model.get("fM")).loc;
		
		//update the pointer to targetBase if fielder is running to a base
		if (destination.equals(FieldConstants.firstBase())) {
			targetBase = bases.get(BaseType.FIRST.num());
		}

		else if (destination.equals(FieldConstants.secondBase())) {
			targetBase = bases.get(BaseType.SECOND.num());
		}

		else if (destination.equals(FieldConstants.thirdBase())) {
			targetBase = bases.get(BaseType.THIRD.num());
		}

		else if (destination.equals(FieldConstants.homePlate())) {
			targetBase = bases.get(BaseType.HOME.num());
		}


	}



	public void move (BallInPlay curBall, FieldEvent status, GameLogger log) {
		
		//only move if theres somewhere to go
		if (destination != null) {
						
			Coordinate3D toGo = this.destination.diff(this.loc);
			double runSpeed = gRats.runSpeed();
						
			if (toGo.mag() < 1) {
				destination = null;
				
				if (targetBase != null) {
					targetBase.arriveAtBase(this);
				}
				
			}
			
			//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
			if (Physics.calcPythag(toGo.x, toGo.y) > .25 && Physics.handleCollision(dimensions, this.loc) == 0) {
				
				System.out.println(loc);
				
				double angleToSpot = Physics.angleFromXAxis(toGo);
				double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
				double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;
								
				//move the player
				lastLoc.x = this.loc.x;
				lastLoc.y = this.loc.y;
				this.loc.add(xDisplacement, yDisplacement, 0);
	
			}
			
			//set flag that the player is next to the ball and will pick it up
			if (Physics.distanceBetween(this.loc, curBall.loc) < 2 && (curBall.state.equals(BallStatus.IN_AIR) || curBall.state.equals(BallStatus.ON_GROUND))) {
				status.receivingBall = this;	
			}
			
		}

	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up. flags that the ball is possessed by a fielder. changes the balls state
	public boolean receiveBall (BallInPlay ball, FieldEvent status, GameLogger log) {

		if (ball.canRecordOut) {
			ball.state = BallStatus.DEAD;
			return true;
		}
		
		//player is standing at position the ball is thrown to
		if (destination == null) {
			
			if (targetBase != null && targetBase.isForceOut()) {
				status.playerOut = targetBase.getAdvancingRunner();
			}
			
		}

		log.add(GameEvent.fieldedBall(this.fullName, this.loc));
		status.hasBall = this;
		ball.state = BallStatus.FIELDED;
		ball.velocity.x = 0;
		ball.velocity.y = 0;
		ball.velocity.z = 0;
		return true;

	}

	//return the LocationTracker of the first reachable spot.  returns the final resting spot if nowhere is reachable
	public LocationTracker firstReachableSpot (BallInPlay fullFlightModel) {

		List <LocationTracker> flyBallModel = fullFlightModel.getTracker();
		
		double bestGroundDistance = Double.MAX_VALUE;
		LocationTracker toReturn = flyBallModel.get(flyBallModel.size()-1);
		
		for (LocationTracker curLoc: flyBallModel) {
			
			Coordinate3D cur = curLoc.loc;
			double physicalDistance = Physics.groundDistanceBetween(cur, loc);
			double timeGiven = curLoc.time;
						
			if (physicalDistance <= timeGiven * gRats.runSpeed() && cur.z < getReach()) {
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

}
