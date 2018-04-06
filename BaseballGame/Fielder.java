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
	
	public Fielder (Coordinate3D loc, GamePlayer player, LinkedList <Coordinate3D> dimensions) {
		super(Coordinate3D.standardPos(player.pos), player.gRatings, player.fullName());
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = player.pos;
		fullName = player.fullName();
		this.dimensions = dimensions;
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
	public void movementBrain (BallInPlay curBall, Map <String, BallInPlay> model, GameLogger log, FieldEvent status) {

		//determine where we want the player to run to
		if (curBall.type.equals(InPlayType.FLYBALL)) {

			if (this.position.equals(Position.CATCHER)) {
				destination = FieldConstants.homePlate();
				status.fOnHome = this;
			}

			else if (this.position.equals(Position.FIRST)) {
				destination = FieldConstants.firstBase();
				status.fOnFirst = this;
			}

			else if (this.position.equals(Position.SECOND)) {

				//cutoff
				if (Physics.radsToDegrees(curBall.launchDir) <= 45) {
					destination = FieldConstants.std2BCutoff();
					status.fCutoff = this;
				}

				else {
					destination = FieldConstants.secondBase();
					status.fOnSecond = this;
				}

			}

			else if (this.position.equals(Position.THIRD)) {
				destination = FieldConstants.thirdBase();
				status.fOnThird = this;
			}

			else if (this.position.equals(Position.SHORT)) {

				//cutoff
				if (Physics.radsToDegrees(curBall.launchDir) > 45) {
					destination = FieldConstants.stdSSCutoff();
					status.fCutoff = this;
				}

				else {
					destination = FieldConstants.secondBase();
					status.fOnSecond = this;
				}

			}

			else if (this.position.equals(Position.PITCHER)) {
				destination = FieldConstants.pitchersMound();
			}

			//outfielders
			else {

				if (canReachFlyBall(model.get("aM"))) {
					destination = model.get("aM").loc;
				}

				else {
					destination = closestSpot(model.get("fM"), model.get("fM").airTime).loc;
				}

			}

		}
		

	}
	
	public void move (BallInPlay curBall, FieldEvent status, GameLogger log) {
		
		Coordinate3D toGo = this.destination.diff(this.loc);
		double runSpeed = gRats.runSpeed();
		
		//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
		if (Physics.calcPythag(toGo.x, toGo.y) > .25 && Physics.handleCollision(dimensions, this.loc) == 0) {
						
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
			status.pickingUpBall = this;	
		}
		
	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up. flags that the ball is possessed by a fielder. changes the balls state
	public boolean receiveBall (BallInPlay ball, FieldEvent status, GameLogger log) {

		if (ball.canRecordOut) {
			ball.state = BallStatus.DEAD;
			return true;
		}

		log.add(GameEvent.fieldedBall(this.fullName, this.loc));
		status.hasBall = this;
		ball.state = BallStatus.FIELDED;
		ball.velocity.x = 0;
		ball.velocity.y = 0;
		ball.velocity.z = 0;
		return true;

	}

	//true if the player can reach a fly ball
	private boolean canReachFlyBall (BallInPlay airModel) {

		Coordinate3D distanceRun = airModel.loc.diff(this.loc);
		double speed = gRats.runSpeed(); //running speed

		return distanceRun.mag() < (speed * airModel.airTime);

	}

	//returns the closest spot the fielder can get to given location, time and speed
	//ball is a completed model of a ball
	private LocationTracker closestSpot (BallInPlay ball, double airTime) {

		List <LocationTracker> locs = ball.tracker;

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
