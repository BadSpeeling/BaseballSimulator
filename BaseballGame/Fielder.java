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
	GameLogger log;
	Base baseGuard = null;
	private boolean hasBall = false;
	private BallInPlay ball = null;

	public Fielder (GameLogger log, Coordinate3D loc, GamePlayer player, LinkedList <Coordinate3D> dimensions) {
		super(Coordinate3D.standardPos(player.pos), player.gRatings, player.fullName());
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = player.pos;
		fullName = player.fullName();
		this.dimensions = dimensions;
		this.log = log;
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

	public void throwBall (BallInPlay toThrow, Coordinate3D target) {

		double throwSpeed = 80; //in ft/s
		Coordinate3D spot = target.diff(this.loc);
		double angle = Physics.angleFromXAxis(spot);
		toThrow.velocity = new Coordinate3D(throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);
		toThrow.thrown = true;
		toThrow.state = BallStatus.THROWN;

	}

	//decides who to throw the ball to.  status will be updated to rep who is throwing and who is being thrown to
	public void throwingBrain (GameLogger log, Base [] bases, List <Baserunner> runners, BallInPlay curBall, List <Fielder> fielders) {

		if (!runners.isEmpty()) {

			Baserunner leadRunner = runners.get(0);
			Base targetBase = leadRunner.attempt;
			Coordinate3D target = null;

			//check which fielder is covering the intended base
			for (Fielder curFielder: fielders) {

				if (curFielder != this && curFielder.baseGuard == targetBase) {
					target = curFielder.loc;
				}

			}

			//target found, throw it
			if (target != null) {

				curBall.thrown = true;
				curBall.state = BallStatus.THROWN;

				ball = null;
				hasBall = false;

				double angle = Physics.angleFromXAxis(target.diff(loc));
				double throwSpeed = fRats.throwingSpeed();

				curBall.velocity = new Coordinate3D (throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);

			}

			//target not found, run to base
			else {

				baseGuard = targetBase;
				setDestination(targetBase.loc);

			}

		}

	}

	//sets the fielders destination equals to a copy of param
	public void setDestination (Coordinate3D dest) {
		destination = dest.copy();
	}

	/*controls all decisions regarding movement the fielder need to make.  flags actions that need to be taken
	 *curBall - the actual ball that was hit
	 *model - models of the ending position of balls that were hit
	 *returns the type of base the player is covering
	 */
	public void movementBrain (BallInPlay model, Base [] bases, List <Fielder> allFielders, Fielder ballChaser) {

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

				Coordinate3D loc = playerChasingBall.loc;
				loc.add(RandomNumber.roll(-10, 10), RandomNumber.roll(-10, 10), 0);
				destination = loc;

			}

		}

		//the ball is not to be fielded by an outfielder
		else {

			if (!position.isOutField()) {
				destination = loc.closest(Coordinate3D.basesInOrder());
			}

		}

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

	}

	public void move (BallInPlay curBall, GameLogger log) {

		//TODO handle arriving at base

		//only move if theres somewhere to go
		if (destination != null) {

			Coordinate3D toGo = this.destination.diff(this.loc);
			double runSpeed = gRats.runSpeed();

			//we are on the base
			if (toGo.mag() < 1) {
				destination = null;

				//update the base
				if (baseGuard != null) {
					baseGuard.arriveAtBase(this);
				}

			}

			//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
			if (Physics.calcPythag(toGo.x, toGo.y) > .25 && Physics.handleCollision(dimensions, this.loc) == 0) {

				double angleToSpot = Physics.angleFromXAxis(toGo);
				double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
				double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;

				//move the player
				lastLoc.x = loc.x;
				lastLoc.y = loc.y;
				loc.add(xDisplacement, yDisplacement, 0);

				if (ball != null) {
					ball.move(new Coordinate3D(xDisplacement, yDisplacement,0));
				}

			}

			//set flag that the player is next to the ball and will pick it up


		}

	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up. flags that the ball is possessed by a fielder. changes the balls state
	public boolean receiveBall (BallInPlay ball, GameLogger log, List <Baserunner> runners) {


		//check for fly out
		if (ball.canRecordOut) {
			ball.state = BallStatus.DEAD;
			Game.messages.add(new FlyballCaughtMsg(this,runners.get(0)));
			ball.canRecordOut = false;
		}

		if (baseGuard != null && baseGuard.isForceOut()) {

			for (Baserunner runner: runners) {

				if (runner.attempt == baseGuard) {
					Game.messages.add(new RunnerOutMsg(runner.attempt, runner, this));
				}

			}

		}

		log.add(GameEvent.fieldedBall(this.fullName, this.loc));
		ball.state = BallStatus.CARRIED;
		pickUpBall(ball);
		ball.velocity.x = 0;
		ball.velocity.y = 0;
		ball.velocity.z = 0;
		return true;

	}

	//return true if picking up ball was successful
	public boolean pickUpBall (BallInPlay ball) {
		this.ball = ball;
		return true;
	}

	//return the LocationTracker of the first reachable spot.  returns the final resting spot if nowhere is reachable
	public LocationTracker firstReachableSpot (BallInPlay fullFlightModel) {

		final int SLACK = 10;

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
		return fullName;
	}

}
