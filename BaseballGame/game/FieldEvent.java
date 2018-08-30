package game;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import atbat.HitType;
import atbat.HitTypeCalculator;
import atbat.ThrownPitch;
import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import helpers.DebuggingBuddy;
import messages.BallOverWallMsg;
import messages.DebugMessage;
import messages.FlyballCaughtMsg;
import messages.ForceOutMsg;
import messages.MakeNewDecisions;
import messages.Message;
import messages.RunScoredMsg;
import messages.RunnerOutMsg;
import objects.Base;
import objects.BaseType;
import objects.Baserunner;
import objects.Fielder;
import objects.GamePlayer;
import objects.GameTeam;
import objects.OnFieldObject;
import objects.OnFieldPlayer;
import physics.Physics;
import player.Generators;
import stadium.Stadium;
import stats.PlateAppearance;
import stats.Result;
import testing.AllOnFieldObjectContainer;
import testing.DebugInfoFrame;
import ui.FieldEventDisplay;

//encapsulates pointers to the OnFieldPlayer that in doing something

public class FieldEvent {

	private final int WHITE = 0xFFFFFF;
	
	public FieldEventDisplay view;
	private Stadium stadium;
	private List <Baserunner> playersScored = new <Baserunner> LinkedList ();
	private List <Baserunner> playersOut = new <Baserunner> LinkedList ();
	private int abNumber;
	private List <Baserunner> runners;
	private Base [] bases;	
	private HitTypeCalculator hitTypeCalc;
	private boolean outRec = false;
	public GamePlayer batter;
	public GamePlayer pitcher;
	
	private List <Integer> playersScoredIDs = new LinkedList <Integer> (); 
	private List <Integer> playersOutIDs = new LinkedList <Integer> ();
	
	//debugging
	private AllOnFieldObjectContainer allObjs = new AllOnFieldObjectContainer ();
	private AllOnFieldObjectContainer prevAllObjs = new AllOnFieldObjectContainer ();
	private DebugInfoFrame baseDebugging;
	
	public FieldEvent (int abNumber, Stadium stadium, FieldEventDisplay display, GamePlayer pitcher) {
		
		this.view = display;
		this.stadium = stadium;
		this.abNumber = abNumber;
		this.hitTypeCalc = new HitTypeCalculator ();
		this.hitTypeCalc.init();
		this.pitcher = pitcher;
		runners = new LinkedList <Baserunner> ();
		
		bases = new Base [4];
		bases[0] = (new Base (FieldConstants.firstBase(), BaseType.FIRST, WHITE));
		bases[1] = (new Base (FieldConstants.secondBase(), BaseType.SECOND, WHITE));
		bases[2] = (new Base (FieldConstants.thirdBase(), BaseType.THIRD, WHITE));
		bases[3] = (new Base (FieldConstants.homePlate(), BaseType.HOME, WHITE));
		
	}
	
	public void placeTestRunner (BaseType on) {
		Baserunner first = Generators.generateRunner(bases[on.num()]);
		runners.add(first);
	}

	public PlateAppearance batterPitcherInteraction (List <Fielder> fielders, int inning, int numOuts) {
		
		if (batter == null || pitcher == null) {
			try {
				throw new Exception ((batter == null ? "Batter" : "Pitcher") + " is null.  Set the field before running interaction.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		PlateAppearance pa = new PlateAppearance (abNumber,batter.getpID(),pitcher.getpID(), inning, numOuts);
		playersOut.clear();
		playersScored.clear();
		playersScoredIDs.clear();
		playersOutIDs.clear();
		
		boolean walked = false;
		boolean struckOut = false;

		BallInPlay hitBall = null;

		while (hitBall == null) {
			
			//hitBall = generateBall();
			hitBall = new ThrownPitch(pitcher,batter).throwBall(pa,stadium,hitTypeCalc);

			if (pa.isStrikeout()) {
				struckOut = true;
				break;
			}

			if (pa.isWalk()) {
				walked = true;
				Baserunner walkedRunner = new Baserunner (batter, 0xFFFFFF);
				runners.add(walkedRunner);
				break;
			}

		}

		//BB or K
		if (hitBall == null) {

			if (walked) {
				batterWalked();
				pa.setOutcome(Result.BB);
			}

			if (struckOut) {
				pa.setOutcome(Result.K);
			}

		}

		//ball put in play, run play
		else {
			int bestBase = fieldEvent(hitBall, fielders, new Baserunner(batter,0xFFFFFF), true);
			setPAOutcome(pa, bestBase);
		}

		postPlayWork(fielders, runners, bases);
		return pa;

	}

	private void setPAOutcome (PlateAppearance pa, int bestBase) {

		if (bestBase == -1) {
			pa.setOutcome(Result.OUT);
		}

		else if (bestBase == 0) {
			pa.setOutcome(Result.S);
		}

		else if (bestBase == 1) {
			pa.setOutcome(Result.D);
		}

		else if (bestBase == 2) {
			pa.setOutcome(Result.T);
		}
		
		else {
			pa.setOutcome(Result.HR);
		}

	}
	
	private int fieldEvent (BallInPlay hitBall, List <Fielder> fielders, Baserunner newBaserunner, boolean debugMode) {
		
		createBaseDebugFrame();
		
		double time = 0;
		
		LocationTracker pickUpSpot = null;
		//recalculate landing spot whenever you need to
		BallInPlay airModel = hitBall.modelBallDistance(true, stadium);
		BallInPlay finalModel = hitBall.modelBallDistance(false, stadium);

		Map <String, BallInPlay> models = new HashMap <String, BallInPlay> ();
		models.put("aM", airModel);
		models.put("fM", finalModel);

		hitBall.batter = newBaserunner;
		newBaserunner.setActionTimer(.5);
		
		//set force outs for rest of bases
		for (int i = 1; i < bases.length; i++) {

			if (!bases[i-1].isRunnerOn()) {
				break;
			}

			bases[i].setForceOut(true);
			bases[i].setToBeForced(bases[i-1].getRunnerOn());

		}

		for (Fielder curFielder: fielders) {
			drawObject(curFielder);
			curFielder.setActionTimer(curFielder.getPlayer().getgRatings().reactionTime()+.25);
		}

		//draw ball marker
		view.drawBall(airModel.getLoc(), 0x00FF00,0);

		int frame = 0;

		//determine initial fielder decisions
		Fielder toReceiveBall = fielderToGetBall(fielders,models.get("fM"));
		Fielder holdingBall = null;
		decideRemainingFielders(bases, fielders,models.get("fM"), toReceiveBall);
		pickUpSpot = toReceiveBall.firstReachableSpot(finalModel);
		
		//add new baserunner, set them up
		newBaserunner.attempt = bases[0];
		newBaserunner.destination = bases[0].getLoc().copy();
		bases[0].runnerTo.add(newBaserunner);
		bases[0].forceOut = true;
		bases[0].toBeForced = newBaserunner;
		runners.add(newBaserunner);
		
		//other runners must decide what they will do
		for (int i = runners.size()-2; i >= 0 ; i--) {
			Base attempt = runnerDecision(runners.get(i), toReceiveBall, pickUpSpot, hitBall, bases);
			Baserunner runner = runners.get(i);
			runner.setAdvancingTimer(computeFlyballWaitTime(pickUpSpot, runner));
			
			//attempt base
			if (attempt != null) {
				runner.baseOn.runnerOn = null;
				runner.baseOn = null;
				runner.attempt = attempt;
				runner.destination = attempt.getLoc();
				attempt.runnerTo.add(runner);
			}
			
		}
		
		while (true) {
		
			draw(frame, fielders, runners, hitBall, true);
			//DebuggingBuddy.wait(view);
			writeToBaseDebugFrame();
			
			time += Physics.tick;
			hitBall.tick(stadium, hitBall.loose,false);
			
			if (frame == 5000) {
				System.out.println("uh oh");
			}
			
			//homerun
			if (hitBall.isBallOutOfPlay()) {

				while (!runners.isEmpty()) {
					
					if (runners.get(0).baseOn != null) {
						runners.get(0).baseOn.runnerOn = null;
					}
					
					playersScoredIDs.add(runners.get(0).getID());
					runners.remove(0);
					
				}

				hitBall.state = BallStatus.DEAD;
				return 3;

			}

			if (playIsOver(runners, hitBall)) {
				break;
			}
			
			for (Fielder curFielder: fielders) {
				
				curFielder.decrementActionTimer();
				
				if (curFielder.destination != null) {

					Coordinate3D toGo = curFielder.destination.diff(curFielder.getLoc());

					//we are on the base
					if (toGo.mag() < 1) {

						curFielder.destination = null;
						
						if (curFielder.baseGuard != null) {
							Base baseGuard = curFielder.baseGuard;
							curFielder.baseOn = baseGuard;
							curFielder.baseOn.fielderOn = curFielder;	
							
							//fielder ran to base with ball, resulting in a forceout
							if (baseGuard != null && baseGuard.isForceOut() && curFielder.hasBall()) {
								Baserunner to = baseGuard.toBeForced;
								baseGuard.clearForce(to);
								outRec = true;
								playersOutIDs.add(to.getID());
								runners.remove(to);
								to.attempt = null;
								to.destination = null;
								to.baseOn = null;
							}
						}

					}

					//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
					else if (Physics.handleCollision(stadium.getWalls(), curFielder.getLoc()) == 0) {

						curFielder.move(toGo);

						//move the ball with the player if they are holding it
						if (curFielder.hasBall()) {
							hitBall.move(toGo, curFielder.getPlayer().getgRatings().getSpeed());
						}

					}

				}
				
			}
			
			//ball grabbing handler
			if (toReceiveBall != null && toReceiveBall.canGrabBall(hitBall.getLoc())) {

				toReceiveBall.receiveBall(hitBall);
				holdingBall = toReceiveBall;
				toReceiveBall = null;
				pickUpSpot = null;
				
				//flyball was caught
				if (hitBall.canRecordOut) {
					
					Baserunner batter = hitBall.batter;
					Base baseOn = batter.baseOn;
					hitBall.canRecordOut = false;
					outRec = true;			
					
					//remove the batter from the play
					runners.remove(batter);
					playersOutIDs.add(batter.getID());
					
					//clear batters base they are on
					if (baseOn != null) {
						baseOn.runnerOn = null;
						batter.baseOn =  null;
					}
					
					//clear batter attempt
					else {
						
						Base attempt = batter.attempt;
						
						//clear potential force
						if (attempt.toBeForced == batter) {
							attempt.toBeForced = null;
							attempt.forceOut = false;
						}
						
						attempt.runnerTo.remove(batter);
						attempt = null;
						batter.destination = null;
						
					}
					
					//clear all bases of forces
					for (Base curBase: bases) {
						curBase.forceOut = false;
						curBase.toBeForced = null;
					}
					
					//return all remaining runners
					for (Baserunner curRunner: runners) {
						
						curRunner.clearRunnersBases();
						curRunner.flipAdvancing();
						curRunner.baseOn = null;
						
						//clear base if it was being ran to
						if (curRunner.attempt != null) {
							curRunner.attempt.runnerTo.remove(curRunner);
						}
						
						//get runners to make way back home
						Base attempt = runnerDecision(curRunner,toReceiveBall,pickUpSpot,hitBall,bases);
						
						//clear old base
						if (attempt != null) {
							
							//check if this runner was on a base that should be cleared
							if (curRunner.baseOn != null) {
								curRunner.baseOn.runnerOn = null;
								curRunner.baseOn = null;
							}
								
							curRunner.attempt = attempt;
							curRunner.destination = attempt.getLoc();
							attempt.runnerTo.add(curRunner);
						}
						
						//set to be forced field
						curRunner.getHomeBase().nowIsForceOut(curRunner);
						
					}
					
					
				}
				
				Base baseOn = holdingBall.getBaseOn();
				
				//the base the player picking up the ball is on was a forceout, the player running to this base is forced out
				if (baseOn != null && baseOn.isForceOut()) {
					Baserunner to = baseOn.toBeForced;
					playersOutIDs.add(to.getID());
					baseOn.clearForce(to);
					runners.remove(to);
					to.attempt = null;
					to.destination = null;
					to.baseOn = null;
					outRec = true;
				}
				
			}

			//if the player needs to throw the ball, do so
			if (holdingBall != null && holdingBall.canPerformAction() && holdingBall.getThrowingDestination() != null) {
				holdingBall.throwBall(hitBall, bases);
				holdingBall = null;
			}
			
			//find a location to throw the ball to
			if (holdingBall != null && !holdingBall.getThrowingDecisionMade() && holdingBall.canPerformAction()) {
				toReceiveBall = holdingBall.throwingBrain(bases, runners, hitBall, fielders);
			}
						
			for (Baserunner curRunner: runners) {

				curRunner.setFlyBallLanded(!hitBall.canRecordOut);
				curRunner.decrementActionTimer();
				curRunner.decrementAdvancingTimer(Physics.tick);	
				
				//dont do anything if no where to run
				if (curRunner.destination != null && curRunner.canPerformAction() && !curRunner.isAdvanceLocked()) {

					//move the runner
					curRunner.move(curRunner.destination.diff(curRunner.getLoc()));

					//arrive at base
					if (Physics.within(curRunner.getLoc().diff(curRunner.destination), 2.0)) {
						
						Base arriving = curRunner.attempt;
						
						//run scored
						if (arriving.isHome()) {
							playersScored.add(curRunner);
							playersScoredIDs.add(curRunner.getID());
						}
						
						//tag out
						else if (arriving.fielderOn != null && arriving.fielderOn.hasBall()) {
							playersOut.add(curRunner);
							playersOutIDs.add(curRunner.getID());
							outRec = true;
						}
						
						else {
							
							//clear force out if proper runner has arrived
							if (arriving.toBeForced != null && arriving.toBeForced == curRunner) {
								arriving.toBeForced = null;
								arriving.forceOut = false;
							}
							
							//clear arriving base on runnerTo and place runner on the base
							arriving.runnerTo.remove(curRunner);
							arriving.runnerOn = curRunner;
							
							//place runner on base
							curRunner.arriveAtBase();
							curRunner.setBestBaseAchieved(arriving.getBase().num());
							
							//check to see if the runner should continue running
							Base nextAttempt = runnerDecision(curRunner, toReceiveBall, pickUpSpot, hitBall, bases);
							
							//continue running 
							if (nextAttempt != null) {
								
								curRunner.attempt = nextAttempt;
								curRunner.attempt.runnerTo.add(curRunner);
								curRunner.destination = curRunner.attempt.getLoc().copy();
								curRunner.baseOn = null;
								arriving.runnerOn = null;
								
							}
							
							else {
								
								curRunner.baseOn = arriving;
								curRunner.attempt = null;
								
							}
							
						}
						
					}
					
				}

			}

			for (Baserunner cur: playersScored) {
				runners.remove(cur);
			}

			for (Baserunner cur: playersOut) {
				runners.remove(cur);
			}
			
			playersScored.clear();
			playersOut.clear();
			
			frame++;

		}
		
		//remove ball locator
		clearObject(hitBall);
		view.removeSpot(hitBall.getLoc(), 1);
		view.drawBall(airModel.getLoc(), 0x000000,airModel.getMarkerSize()); //remove ball marker
		return !outRec ? -1 : newBaserunner.getBestBase();

	}

	private void postPlayWork (List <Fielder> fielders, List <Baserunner> runners, Base [] bases) {

		//reset fielders
		for (Fielder curFielder: fielders) {
			clearObject(curFielder);
			curFielder.reset();
		}

		//reset baserunners
		for (Baserunner runner: runners) {
			runner.reset();
		}

		//reset bases
		for (int i = 0; i < bases.length; i++) {			
			bases[i].clearForNextAB();
		}

	}
	
	public void nextHalfInning () {
		
		playersScoredIDs.clear();
		playersOutIDs.clear();
		playersScored.clear();
		playersOut.clear();
		outRec = false;
		batter = null;
		pitcher = null;
		
	}
	
	//draws the current location and removes the previous
	private void drawObject (OnFieldObject obj) {

		if (obj.getLastDrawnLoc() != null)
			view.drawBall(obj.getLastDrawnLoc(), 0x000000, obj.getMarkerSize()+1);
		obj.setLastDrawnLoc(obj.getLoc());
		view.drawBall(obj.getLoc(), obj.getColor(), obj.getMarkerSize());

	}

	//clear ball spot
	private void clearObject (OnFieldObject obj) {
		view.drawBall(obj.lastLoc, 0x000000, obj.getMarkerSize()+1);
		view.drawBall(obj.getLoc(), 0x000000, obj.getMarkerSize()+1);
	}
	
	//returns the fielder that can get the a hit ball the earliest.  their destination field will be properly updated
	private Fielder fielderToGetBall (List <Fielder> fielders, BallInPlay fullModel) {

		double bestTime = Double.MAX_VALUE;
		Fielder toRet = null;
		Coordinate3D loc = null;

		for (Fielder curFielder: fielders) {

			LocationTracker cur = curFielder.firstReachableSpot(fullModel);

			double distance = cur.loc.diff(curFielder.getLoc()).mag();

			if (distance < bestTime) {
				bestTime = distance;
				toRet = curFielder;
				loc = cur.loc;
			}

		}

		//emergency case - center fielder will get ball if noone else
		if (toRet == null) {
			fielders.get(7).destination = fullModel.getLoc();
			return fielders.get(7);
		}

		else {
			toRet.destination = loc;
			return toRet;
		}


	}

	//determines the action for the remaining fielders.  assumes that status.chasingBall has been updated
	//@param model the ball in play being modelled
	private void decideRemainingFielders (Base [] bases, List <Fielder> fielders, BallInPlay model, Fielder chaser) {

		for (Fielder curFielder: fielders) {

			if (curFielder.destination == null) {
				curFielder.movementBrain(model,bases,fielders,chaser,stadium.getWalls());
			}

		}

	}
		
	private Base runnerDecision (Baserunner curRunner, Fielder chaser, LocationTracker pickUpInfo, BallInPlay curBall, Base [] bases) {
		
		//check if retreating runner has made it home. prevents from retreating past home base
		if (!curRunner.isAdvancing() && curRunner.getHomeBase() == curRunner.baseOn) {
			return null;
		}
		
		Base baseTry = null;
		
		//determine what the next base should be
		if (curRunner.baseOn == null) {
			//run back to last base
			baseTry = curRunner.getLastBaseOn();
		}
		
		else {
			//depending on if we are advancing or not, get the next logical base
			baseTry = curRunner.nextBaseFromBase(bases, curRunner.baseOn);
		}
		
		Base goTo = null;
		
		//if we have a valid base to try
		if (baseTry != null) 
			goTo = curRunner.baserunnerBrain(baseTry, bases, chaser, pickUpInfo, curBall);
		
		return goTo;
		
		
	}

	private void writeBases () {
		String [] vals = new String [4];

		for (int i = 0; i < bases.length; i++) {
			vals[i] = bases[i].forceOut ? "Force" : "No Force";
		}

		//view.writeToDebuggerAndUpdate(vals);
		
	}
	
	private void batterWalked () {
		
		int num = 0;
		Baserunner runnerToMoveUp = runners.get(runners.size()-1);
		Base nextBase = bases[0];
		
		//try to move runners up, stop before getting to home
		while (num != 3 && runnerToMoveUp != null) {
			Baserunner nextRunner = nextBase.runnerOn;
			nextBase.runnerOn = runnerToMoveUp;
			runnerToMoveUp.baseOn = nextBase;
			runnerToMoveUp.setLoc(nextBase.getLoc().copy());
			runnerToMoveUp.homeBase = nextBase;
			nextBase.runnerOn = runnerToMoveUp;
			num++;
			nextBase = bases[num];
			runnerToMoveUp = nextRunner;
		}
		
		//walked a run home
		if (runnerToMoveUp != null) {
			runners.remove(runnerToMoveUp);
			playersScoredIDs.add(runnerToMoveUp.getID());
		}
		
	}
	
	private void writeBaseRunners () {
		String [] vals = new String [runners.size()];

		for (int i = 0; i < vals.length; i++) {
			vals[i] = runners.get(i).toString();
		}

		//view.writeToDebuggerAndUpdate(vals);
	}

	private boolean playIsOver (List <Baserunner> runners, BallInPlay hitBall) {

		boolean over = true;

		for (Baserunner cur: runners) {

			if (cur.isAttempting()) {
				over = false;
				break;
			}

		}

		return (over && !hitBall.canRecordOut); 

	}

	//to be called after a field event.  cleans up any errors so that the game can play properly
	private List <Baserunner> validStateSetter (List <Baserunner> runners) {

		List <Baserunner> ret = new LinkedList <Baserunner> ();

		for (Baserunner curRunner: runners) {

			//process if they did not end on a base
			if (curRunner.baseOn == null) {

				continue;

			}

			ret.add(curRunner);

		}

		return ret;

	}

	public List <Baserunner> getRunnersScored () {
		return playersScored;
	}
	
	private BallInPlay generateBall () {
		
		double launchAngle = 0;
		double launchDir = 60;
		double hitspeed = 120;
		
		return new BallInPlay (new Coordinate3D(0,0,3), Physics.degreesToRads(launchAngle), Physics.degreesToRads(launchDir), hitspeed, stadium, 0xFFFFFF, HitType.DEEPFLYBALL);
	}
	
	private void draw (int frame, List <Fielder> onTheField, List <Baserunner> runners, BallInPlay hitBall, boolean wait) {
		//update image
		if (frame % 4 == 0) {

			for (Fielder curFielder: onTheField) {
				drawObject(curFielder);
			}

			for (Baserunner runner: runners) {
				drawObject(runner);
			}

			view.drawFieldOutline();
			drawObject(hitBall);
			view.getFieldImage().repaint();

		}

		if (wait) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public List <Integer> getIDRunnersScored () {
		return playersScoredIDs;
	}
	
	public List <Integer> getIDRunnersOut () {
		return playersOutIDs;
	}
	
	private Double computeFlyballWaitTime (LocationTracker pickupSpot, Baserunner runnerFor) {
		
		/*
		//the ball is fielded when it is not a flyball, therefore no lock should be placed
		if (!pickupSpot.inAir) {
			return new Double(-1);
		}
		*/
		
		double ret = 0;
		
		double groundDistance = pickupSpot.loc.mag2D();
		ret = groundDistance/150;
		
		return ret;
		
	}
	
	public void createBaseDebugFrame () {
		
		if (baseDebugging != null) {
			baseDebugging.setVisible(false);
		}
		
		baseDebugging = new DebugInfoFrame (500,500);
	}
	
	public void writeToBaseDebugFrame () {
		baseDebugging.writeBasesToScreen(bases);
	}

}