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
		homeBase = this.baseOn;
		
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
		
		//need too get back home
		if (!advancing) {
			
			if (baseOn == homeBase) {
				return null;
			}
			
			else {
				return baseTry;
			}
			
		}
		
		//forced to run to next base
		if (baseOn.isForceOut() || baseOn.isSomeoneRunningAtMyBase()) {
			return baseTry;
		}

		//Base baseTry = nextBaseFromBase(bases);
		Coordinate3D baseLoc = baseTry.getLoc();

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
			return baseTry;
		}

		//we are staying put
		else {
			return null;
		}

	}

	//flip the way that the play is running, set their destination to this new place
	public void reverseDirection () {

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

				//will be running to a new base
				attemptBase(runTo);
				
			}

			else {
				attempt = null;
				destination = null;
			}

		}

	}
	
	public void attemptBase (Base goTo) {
		
		destination = goTo.getLoc().copy();
		
		//on a base, set this as our last attempt
		if (baseOn != null) {
			leaveBase(baseOn);
		}
		
		//changing direction, current attempt is now last attempt
		else if (attempt != null) {
			lastBaseAttempt = attempt;
		}
		
		attempt = goTo;
		goTo.addRunnerTo(this);
		
	}
	
	public void leaveBase (Base toLeave) {
		
		baseOn = null;
		lastBaseAttempt = toLeave;
		toLeave.baserunnerLeave();
		
	}
	
	//true if the runner is still in the play
	public boolean arriveAtBase (Base arriving) {
		
		//clear base of attempt
		arriving.removeRunnerTo(this);
		destination = null;
		attempt = null;
		arriving.setForceOut(false);
		Fielder fielderOnBase = arriving.getFielderOn();
		
		//player is out by a tag
		if (fielderOnBase != null && fielderOnBase.hasBall()) {
			FieldEvent.messages.add(new RunnerOutMsg(arriving,this,fielderOnBase));
			return false;
		}
		
		//safely on base
		else {
			
			//safe at home
			if (arriving.isHome()) {
				FieldEvent.messages.add(new RunScoredMsg(this));
				return false;
			}
			
			//safe at a base
			else {
				baseOn = arriving;
				setBestBaseAchieved(baseOn.getBase().num());
				arriving.setRunnerOn(this);
				return true;
			}
			
		}
		
	}

	//take a step towards the base
	//player must have a destination
	public int run (Base [] bases, Fielder chaser, LocationTracker tracker, BallInPlay curBall) {
		
		if (attempt == null) {
			return 1;
		}
		
		move(destination.diff(getLoc()));

		//slide in to base
		if (Physics.within(getLoc().diff(destination), 2.0)) {

			destination = null;
			boolean stillInPlay = arriveAtBase(attempt);
			
			//if this runner is still in the play, see if they need to make a new decision
			if (stillInPlay) {

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

	@Override
	public String toString() {

		BaseType homeBaseType = homeBase == null ? BaseType.NONE : homeBase.getBase();
		BaseType attemptBaseType = attempt == null ? BaseType.NONE : attempt.getBase();
		BaseType onBaseType = baseOn == null ? BaseType.NONE : baseOn.getBase();
		BaseType lastBaseAttemptType = lastBaseAttempt == null ? BaseType.NONE : lastBaseAttempt.getBase();

		return "Baserunner [name= "+getPlayer().fullName()+", destination=" + destination + ", loc=" + getLoc() + ", lastLoc=" + lastLoc + ", baseOn=" + onBaseType + ", attempt="
		+ attemptBaseType + ", homeBase=" + homeBaseType + ", advancing=" + advancing + ", bestBaseAchieved="
		+ bestBaseAchieved + ", lastBaseAttempt=" + lastBaseAttemptType + "]";
	}

}
