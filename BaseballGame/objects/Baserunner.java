package objects;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import ball.BallInPlay;
import ball.LocationTracker;
import datatype.Coordinate3D;
import game.FieldConstants;
import game.FieldEvent;
import game.Game;
import physics.Physics;
import ratings.GeneralRatings;
import stadium.Wall;
import stats.BattingStatline;
import stats.PitchingStatline;
import ui.FieldEventDisplay;

public class Baserunner extends OnFieldPlayer {

	private Coordinate3D destination = null;
	private Base baseOn = null;
	private Base attempt = null;
	private Base homeBase;
	private boolean advancing = true;
	private boolean returningToHomeBase = false;
	private int bestBaseAchieved = -1;
	private Base lastBaseOn = null;
	private Double advancingTimer = null; //controlls how long the baserunner can advance on a flyball until needing to wait
	private BaserunnerStatus basePathStatus = BaserunnerStatus.Init;
	
	public Baserunner (GamePlayer other, int color) {
		super(other, FieldConstants.homePlate(), color, other.getCurGameBatting(), other.getCurGamePitching());
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
		lastBaseOn = baseOn;
		homeBase = this.baseOn;

	}

	public int getMarkerSize () {
		return 1;
	}

	public void advancing () {
		advancing = true;
	}
	
	public boolean isAdvancing () {
		return advancing;
	}

	public void setAdvancing (boolean adv) {
		advancing = adv;
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
	
	public Base getLastBaseOn () {
		return lastBaseOn;
	}
	
	public void reset () {
		homeBase = baseOn;
		attempt = null;
		destination = null;
		advancing = true;
		lastBaseOn = baseOn;
	}
	
	//baserunner will begin running to the base, checking  
	public void runToBase (Base baseRunningTo) {
		
		//clear out base that is being run to, remove destination
		if (attempt != null) {
			attempt.removeRunnerTo(this);
			attempt = null;
			destination = null;
		}
		
		//clear the base that you are on
		if (baseOn != null) {
			lastBaseOn = baseRunningTo;
			baseOn.clearRunnerOn();
			baseOn = null;
		}
		
		//set variables for running to the next base
		if (baseRunningTo != null) {
			attempt = baseRunningTo;
			destination = baseRunningTo.getLoc().copy();
			baseRunningTo.addRunnerTo(this);
		}
			
	}
	
	public void setBasePathStatus (BaserunnerStatus status) {
		basePathStatus = status;
	}
	
	//stops a runner from running to a base
	public void stopRunningToBase () {
		
		if (attempt != null) {
			
			attempt.removeRunnerTo(this);
			attempt = null;
			
		}
		
	}
	
	//assumes that the runner is close enough to reach the base
	//returns true if the runner has been tagged out, otherwise sets on base and returns true
	//return codes: 0-tag out, 1-safe, 2-safe at home 
	public int arriveAtBase () {
		
		Base arrivingAt = attempt;
		
		//clear base of runnerTo
		attempt.removeRunnerTo(this);
		attempt = null;
		destination = null;
		
		Fielder curFielder = arrivingAt.getFielderOn();
		
		//a tag out has occured
		if (curFielder != null && curFielder.hasBall()) {
			return 0;
		}
		
		else {
			
			this.baseOn = arrivingAt;
			
			//check if a force needs to be cleared
			if (baseOn.isThisPlayerToBeForced(this)) {
				baseOn.clearForce(this);
			}
			
			//the base the runner has reached is not home
			if (!arrivingAt.isHome()) {
				arrivingAt.setRunnerOn(this);
				baseOn = arrivingAt;
				lastBaseOn = arrivingAt;
				setLoc(baseOn.getLoc().copy());
				return 1;
			}
			
			//runner reached home and scored a run
			else {
				return 2;
			}
			
		}
		
	}
	//returns the base the player should run to
	//null if stay put
	//chaser is the person to be receiving the ball, either running after it or 
	//baseTry is the base that will be attempted
	public Base baserunnerBrain (Base baseTry, Base [] bases, Fielder chaser, LocationTracker pickUpInfo, BallInPlay curBall) {
		
		//if we are retreating
		if (!advancing) {
			return baseTry;
		}
		
		if (baseOn.isForceOut() || baseOn.isSomeoneRunningAtMyBase()) {
			return baseTry;
		}

		double timeAvailable = 1.3;
		double distanceToBase = getLoc().diff(baseTry.getLoc()).mag2D();
		double mySpeed = getPlayer().getgRatings().getSpeed();
		
		Coordinate3D baseLoc = baseTry.getLoc();
		Coordinate3D ballLoc = curBall.getLoc();

		//the ball is yet to be picked up
		if (pickUpInfo != null) {
			
			Coordinate3D chaserLoc = chaser.getLoc();
			Coordinate3D pickUpLoc = pickUpInfo.loc;
			
			//compute the distance to ball and therefore time to run to ball
			double runToGroundDist = pickUpLoc.diff(chaserLoc).mag2D();
			double chaserSpeed = chaser.getPlayer().getgRatings().getSpeed();
			timeAvailable += runToGroundDist/chaserSpeed;

		}

		double throwDist = -1;

		//the ball has been fielded, use balls current spot
		if (pickUpInfo == null)
			throwDist = ballLoc.diff(baseLoc).mag2D();
		//the ball is being chased
		else
			throwDist = pickUpInfo.loc.diff(baseLoc).mag2D();

		double throwSpeed = 100;

		/*
		//if the ball is yet to be thrown, see what the chasers arm strength is
		if (pickUpInfo != null) {
			throwSpeed = chaser.getPlayer().getgRatings().throwSpeed();
		}

		else {
			throwSpeed = curBall.velocity.mag2D();
		}
		*/

		timeAvailable += throwDist/throwSpeed;

		//do we have enough time to make it to the next base
		if (distanceToBase < mySpeed * timeAvailable) {
			return baseTry;
		}

		//we are staying put
		else {
			return null;
		}

	}

	public void flipAdvancing () {
		advancing = !advancing;
	}

	public void clearAttempt () {
		
		if (attempt != null) {
			
			attempt.removeRunnerTo(this);
			attempt = null;
			destination = null;
			
		}
		
	}
	
	public void clearBaseOn () {
		
		if (baseOn != null) {
			
			baseOn.clearRunnerOn();
			baseOn = null;
			
		}
		
	}
	
	public void placeOnBase (Base on) {
		baseOn = on;
		homeBase = on;
		setLoc(baseOn.getLoc().copy());
		lastBaseOn = on;
		on.setRunnerOn(this);
	}
	
	public int getBestBase () {
		return bestBaseAchieved;
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
			return null;
		}

		return bases[num];

	}

	public void setBaseOn (Base set) {
		set.setRunnerOn(this);
		baseOn = set;
		setLoc(baseOn.getBase().equiv());
	}

	public void setBestBase (int num) {
		bestBaseAchieved = num;
	}

	//if they are not on a base, i am live
	public boolean isAttempting () {
		return baseOn == null;
	}

	public boolean isEqual (Baserunner other) {
		return this.getID() == other.getID();
	}
	
	//checks if this runner can move
	public boolean isAdvanceLocked (boolean canRecordAirOut) {
		return advancingTimer != null && advancingTimer < 0 && canRecordAirOut;
	}
	
	public void decrementAdvancingTimer () {
		
		//prevent decreasing a null value
		if (advancingTimer != null) {
			advancingTimer -= Physics.tick;
		}
		
	}
	
	public void setAdvancingTimer (double time) {
		advancingTimer = time;
	}
	
	public Base getBaseOn () {
		return baseOn;
	}
	
	public Base getAttempt () {
		return attempt;
	}
	
	public void clearForce () {
		attempt.clearForce(this);
		clearAttempt();
	}

	public Coordinate3D getDestination () {
		return destination;
	}
	
	@Override
	public String toString() {

		BaseType homeBaseType = homeBase == null ? BaseType.NONE : homeBase.getBase();
		BaseType attemptBaseType = attempt == null ? BaseType.NONE : attempt.getBase();
		BaseType onBaseType = baseOn == null ? BaseType.NONE : baseOn.getBase();
		BaseType lastBaseAttemptType = lastBaseOn == null ? BaseType.NONE : lastBaseOn.getBase();

		return "Baserunner [name= "+getPlayer().fullName()+", destination=" + destination + ", loc=" + getLoc() + ", lastLoc=" + lastLoc + ", baseOn=" + onBaseType + ", attempt="
		+ attemptBaseType + ", homeBase=" + homeBaseType + ", advancing=" + advancing + ", bestBaseAchieved="
		+ bestBaseAchieved + ", lastBaseAttempt=" + lastBaseAttemptType + "]";
		
	}

}
