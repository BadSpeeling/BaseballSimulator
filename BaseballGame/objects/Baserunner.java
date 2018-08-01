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
import messages.AdvancingNumberOfBases;
import messages.RunScoredMsg;
import messages.RunnerOutMsg;
import physics.Physics;
import player.Player;
import ratings.GeneralRatings;
import stadium.Wall;
import stats.BattingStatline;
import stats.PitchingStatline;
import ui.FieldEventDisplay;

public class Baserunner extends OnFieldPlayer {

	public Coordinate3D destination = null;
	public Coordinate3D lastLoc = new Coordinate3D (0,0,0);
	public Base baseOn = null;
	public Base attempt = null;
	public Base homeBase;
	private boolean advancing = true;
	private int bestBaseAchieved = -1;
	private Base lastBaseOn = null;

	public Baserunner (Player other, int color) {
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

	public void clearRunnersBases () {

		attempt = null;	
		destination = null;

	}
	
	public void placeOnBase (Base on) {
		baseOn = on;
		homeBase = on;
		setLoc(baseOn.getLoc().copy());
		lastBaseOn = on;
		on.setRunnerOn(this);
	}

	public void leaveBase (Base toLeave) {

		lastBaseOn = baseOn;
		baseOn = null;

	}

	//updates fields for arriving at a base
	public void arriveAtBase () {

		//clear base of attempt
		baseOn = attempt;
		setLoc(baseOn.getLoc().copy());
		lastBaseOn = attempt;
		destination = null;
		attempt = null;

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
