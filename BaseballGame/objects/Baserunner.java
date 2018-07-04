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
import messages.RunnerOutMsg;
import physics.Physics;
import player.Player;
import ratings.GeneralRatings;
import stadium.Wall;
import stats.BattingStatline;
import stats.PitchingStatline;
import ui.GameDisplay;

public class Baserunner extends OnFieldPlayer {

	public Coordinate3D destination = null;
	public Coordinate3D lastLoc = new Coordinate3D (0,0,0);
	public Base baseOn = null;
	public Base attempt = null;
	private Base homeBase;
	private boolean advancing = true;
	private int bestBaseAchieved = -1;
	private Base lastBaseAttempt = null;

	public Baserunner (Player other, int color, BattingStatline bs, PitchingStatline ps) {
		super(other, FieldConstants.homePlate(), color, bs, ps);
	}

	//to be used to initialize a baserunner  
	public void init (BaseType base, Base [] bases) {

		int numBase = base.num();
		this.baseOn = bases[numBase];
		advancing = true;
		attempt = null;
		destination = null;
		setLoc(baseOn.getBase().equiv());
		lastLoc = getLoc().copy();
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

	//returns the base the player should run to
	//null if stay put
	//chaser is the person to be receiving the ball, either running after it or 
	//baseTry is the base that will be attempted
	public Base baserunnerBrain (Base baseTry, Base [] bases, Fielder chaser, LocationTracker pickUpInfo, BallInPlay curBall) {

		//forced to run to next base
		if (baseOn.isForceOut() || baseOn.getRunnerTo() != null) {
			return baseTry;
		}

		//Base baseTry = nextBaseFromBase(bases);
		Coordinate3D baseLoc = baseTry.getLoc();

		//check if the next base is open to run to
		if (baseTry.runnerOn() || baseTry.getRunnerTo() != null) {
			return null;
		}

		double mySpeed = getPlayer().getgRatings().getSpeed();
		Coordinate3D ballLoc = curBall.getLoc();
		double timeAvailable = 1.3;
		double distanceToBase = 90;

		if (chaser != null && curBall.isBallLoose()) {
			Coordinate3D chaserLoc = chaser.getLoc();

			//check if the ball is under controll of fielders, i.e dont need to factor in chasing time 

			Coordinate3D pickUpLoc = pickUpInfo.loc;
			double runToGroundDist = pickUpLoc.diff(chaserLoc).mag2D();
			double chaserSpeed = chaser.getPlayer().getgRatings().getSpeed();
			timeAvailable += runToGroundDist/chaserSpeed;

		}

		double throwDist = -1;

		//the ball is coming to us
		if (!curBall.isBallLoose())
			throwDist = ballLoc.diff(baseLoc).mag2D();
		//the ball is being chased
		else
			throwDist = pickUpInfo.loc.diff(baseLoc).mag2D();

		double throwSpeed = chaser.getPlayer().getgRatings().throwSpeed();
		timeAvailable += throwDist/throwSpeed;

		//do we have enough time to make it to the next base
		if (distanceToBase < mySpeed * timeAvailable) {

			//dont run to a base with someone on
			if (baseTry.runnerOn()) {
				return null;
			}

			else {
				return baseTry;
			}

		}

		//we are staying put
		else {
			return null;
		}

	}

	//flip the way that the play is running, set their destination to this new place
	public void reverseDirection () {

		if (lastBaseAttempt == null) {
			System.out.println("DAFsas");
		}

		Base temp = lastBaseAttempt;
		lastBaseAttempt = attempt;
		attempt = temp;
		if(attempt != null)destination = attempt.getLoc();
		advancing = !advancing;

	}

	public void resetLastBaseAttempt () {
		if (baseOn != null) {
			lastBaseAttempt = baseOn;
		}
	}

	//sets the players running destination to the next appropriate base.
	//player must be on a base
	public void getNewDestination (Base [] bases, Fielder chaser, LocationTracker tracker, BallInPlay curBall) {

		if (baseOn != null) {

			Base runTo = baserunnerBrain(nextBaseFromBase(bases, baseOn), bases, chaser, tracker, curBall);

			if (runTo != null) {

				attempt = runTo;
				attempt.setRunnerTo(this);
				destination = attempt.getLoc();

				if (baseOn != null) {
					baseOn.leaveBase(this);
					baseOn = null;
				}

			}

			else {
				attempt = null;
			}

		}

	}

	//take a step towards the base
	//player must have a destination
	public int run (Base [] bases, Fielder chaser, LocationTracker tracker, BallInPlay curBall) {

		move(destination.diff(getLoc()));

		if (Physics.within(getLoc().diff(destination), 2.0)) {

			destination = null;
			boolean safe = attempt.arriveAtBase(this);

			if (safe) {

				lastBaseAttempt = attempt;
				baseOn = attempt;
				setLoc(baseOn.getLoc().copy());
				baseOn.setRunnerTo(null);
				bestBaseAchieved = baseOn.getBase().num();
				getNewDestination(bases, chaser, tracker, curBall);

			}

		}

		return 1;

	}

	//gets the next base to advance to, given the runner is on a base
	public Base nextBaseFromBase (Base [] bases, Base baseOnTemp) {

		int num = baseOnTemp.getBase().num();

		if (advancing) {
			num++;
		}

		else {
			num--;
		}

		if (num < 0) {
			return bases[0];
		}

		return bases[num];

	}

	public void setBaseOn (Base set) {
		set.arriveAtBase(this);
		baseOn = set;
		setLoc(baseOn.getBase().equiv());
	}

	public void setBestBase (int num) {
		bestBaseAchieved = num;
	}


	public boolean isAttempting () {
		return attempt != null;
	}

	@Override
	public String toString() {

		BaseType homeBaseType = homeBase == null ? BaseType.NONE : homeBase.getBase();
		BaseType attemptBaseType = attempt == null ? BaseType.NONE : attempt.getBase();
		BaseType onBaseType = baseOn == null ? BaseType.NONE : baseOn.getBase();
		BaseType lastBaseAttemptType = baseOn == null ? BaseType.NONE : lastBaseAttempt.getBase();

		return "Baserunner [name= "+getPlayer().fullName()+", destination=" + destination + ", loc=" + getLoc() + ", lastLoc=" + lastLoc + ", baseOn=" + onBaseType + ", attempt="
		+ attemptBaseType + ", homeBase=" + homeBaseType + ", advancing=" + advancing + ", bestBaseAchieved="
		+ bestBaseAchieved + ", lastBaseAttempt=" + lastBaseAttemptType + "]";
	}

}
