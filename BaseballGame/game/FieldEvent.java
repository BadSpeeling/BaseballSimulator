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
import objects.Base;
import objects.BaseType;
import objects.Baserunner;
import objects.Fielder;
import objects.GameTeam;
import objects.OnFieldObject;
import objects.OnFieldPlayer;
import physics.Physics;
import player.Generators;
import player.Player;
import stadium.Stadium;
import stats.PlateAppearance;
import stats.Result;
import testing.AllOnFieldObjectContainer;
import testing.DebugInfoFrame;
import ui.FieldEventDisplay;

//encapsulates pointers to the OnFieldPlayer that in doing something

public class FieldEvent {

	private final boolean drawField = false;

	private final int WHITE = 0xFFFFFF;
	
	public FieldEventDisplay view;
	private Stadium stadium;
	private int abNumber;
	private List <Baserunner> runners;
	private Base [] bases;	
	private HitTypeCalculator hitTypeCalc;
	private Player batter;
	private Player pitcher;
	private int outs;
	
	private List <Integer> playersScoredIDs = new LinkedList <Integer> (); 
	private List <Integer> playersOutIDs = new LinkedList <Integer> ();
	
	//debugging
	private AllOnFieldObjectContainer allObjs = new AllOnFieldObjectContainer ();
	private AllOnFieldObjectContainer prevAllObjs = new AllOnFieldObjectContainer ();
	private DebugInfoFrame baseDebugging;
	
	public FieldEvent (int abNumber, Stadium stadium, FieldEventDisplay display) {
		
		this.view = display;
		this.stadium = stadium;
		this.abNumber = abNumber;
		this.hitTypeCalc = new HitTypeCalculator ();
		this.hitTypeCalc.init();
		this.outs = 0;
		runners = new LinkedList <Baserunner> ();
		
		//init bases
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
				playersOutIDs.add(batter.getpID());
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
		
		//send new base runner to first
		Base batterGoingTo = bases[0];
		newBaserunner.runToBase(batterGoingTo);
		batterGoingTo.runnerWillBeForced(newBaserunner);
		runners.add(newBaserunner);
		
		//other runners must decide what they will do
		for (int i = runners.size()-2; i >= 0 ; i--) {
			Base attempt = runnerDecision(runners.get(i), toReceiveBall, pickUpSpot, hitBall, bases);
			Baserunner runner = runners.get(i);
			runner.setAdvancingTimer(computeFlyballWaitTime(pickUpSpot, runner));
			
			//attempt base
			if (attempt != null) {
				runner.runToBase(attempt);
			}
			
		}
		
		
		while (outs < 3) {
		
			if (drawField)
				draw(frame, fielders, runners, hitBall, true);
			
			time += Physics.tick;
			hitBall.tick(stadium, hitBall.loose,false);
			
			//homerun
			if (hitBall.isBallOutOfPlay()) {

				while (!runners.isEmpty()) {
					
					if (runners.get(0).getBaseOn() != null) {
						runners.get(0).getBaseOn().runnerOn = null;
					}
					
					playersScoredIDs.add(runners.get(0).getID());
					removeBaserunner(runners.get(0));
					
				}

				clearObject(hitBall);
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
							
							boolean runnerForced = curFielder.arriveAtBase(curFielder.baseGuard);
							
							if (runnerForced) {
								
								//remove the forceout
								Baserunner out = curFielder.baseOn.getToBeForced();
								curFielder.baseOn.clearForce(out);
								
								playersOutIDs.add(out.getID());
								outs++;
								removeBaserunner(out);
								
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
					
					hitBall.canRecordOut = false;
					
					//clear out the batters 
					Baserunner batter = hitBall.batter;
					
					batter.clearForce();
					batter.clearBaseOn();
					
					playersOutIDs.add(batter.getID());
					outs++;
					removeBaserunner(batter);
					
					//return all remaining runners
					for (int i = runners.size()-1; i >= 0; i--) {
						
						Baserunner curRunner = runners.get(i);
						
						Base prev = curRunner.getLastBaseOn();
						
						//stop running to base
						curRunner.clearForce();
						
						//tell runner they are going backwards
						curRunner.flipAdvancing();
						
						//run to the last base 
						curRunner.runToBase(prev);
						
						//set to be forced field
						curRunner.getHomeBase().nowIsForceOut(curRunner);
						
					}
					
					
				}
				
				Base baseOn = holdingBall.getBaseOn();
				
				//the base the player picking up the ball is on was a forceout, the player running to this base is forced out
				if (baseOn != null && baseOn.isForceOut()) {
					
					Baserunner playerOut = baseOn.getToBeForced();
		
					playersOutIDs.add(playerOut.getID());
					outs++;
					removeBaserunner(playerOut);
					playerOut.clearForce();
					
					
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
						
			for (int i = runners.size()-1; i >= 0; i--) {

				Baserunner curRunner = runners.get(i);
				
				curRunner.decrementActionTimer();
				curRunner.decrementAdvancingTimer();	
				
				//dont do anything if no where to run
				if (curRunner.getDestination() != null && curRunner.canPerformAction() && !curRunner.isAdvanceLocked(hitBall.canRecordOut)) {

					//move the runner
					curRunner.move(curRunner.getDestination().diff(curRunner.getLoc()));

					//arrive at base
					if (Physics.within(curRunner.getLoc().diff(curRunner.getDestination()), 2.0)) {
						
						int reachedBaseCode = curRunner.arriveAtBase();
						
						//tag out occured
						if (reachedBaseCode == 0) {
							playersOutIDs.add(curRunner.getID());
							outs++;
							runners.remove(i);
						}
						
						//safe at base
						else if (reachedBaseCode == 1) {
							
							Base nextAttempt = runnerDecision(curRunner, toReceiveBall, pickUpSpot, hitBall, bases);
							
							if (nextAttempt != null) {
								curRunner.runToBase(nextAttempt);
							}
							
						}
						
						//scored a run
						else {
							playersScoredIDs.add(curRunner.getID());
							removeBaserunner(curRunner);
						}
						
					}
					
				}

			}

			frame++;

		}
		
		//DebuggingBuddy.wait(baseDebugging);
		//remove ball locator
		clearObject(hitBall);
		view.removeSpot(hitBall.getLoc(), 1);
		view.drawBall(airModel.getLoc(), 0x000000,airModel.getMarkerSize()); //remove ball marker
		return !playersOutIDs.isEmpty() ? -1 : newBaserunner.getBaseOn().getBase().num();

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
		batter = null;
		pitcher = null;
		outs = 0;
		runners.clear();
		
		for (Base base: bases) {
			base.clearForNextInning();
		}
		
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
		
	//determines which base a baserunner should attempt to run to.  null if they should go nowhere
	private Base runnerDecision (Baserunner curRunner, Fielder chaser, LocationTracker pickUpInfo, BallInPlay curBall, Base [] bases) {
		
		//check if retreating runner has made it home. prevents from retreating past home base
		if (!curRunner.isAdvancing() && curRunner.getHomeBase() == curRunner.getBaseOn()) {
			return null;
		}
		
		Base baseTry = null;
		
		//determine what the next base should be
		if (curRunner.getBaseOn() == null) {
			//run back to last base
			baseTry = curRunner.getLastBaseOn();
		}
		
		else {
			//depending on if we are advancing or not, get the next logical base
			baseTry = curRunner.nextBaseFromBase(bases, curRunner.getBaseOn());
		}
		
		Base goTo = null;
		
		//if we have a valid base to try
		if (baseTry != null) 
			goTo = curRunner.baserunnerBrain(baseTry, bases, chaser, pickUpInfo, curBall);
		
		return goTo;
		
		
	}
	
	private void batterWalked () {
		
		int num = 0;
		Baserunner runnerToMoveUp = runners.get(runners.size()-1);
		Base nextBase = bases[0];
		
		//try to move runners up, stop before getting to home
		while (num != 3 && runnerToMoveUp != null) {
			Baserunner nextRunner = nextBase.runnerOn;
			nextBase.runnerOn = runnerToMoveUp;
			runnerToMoveUp.setBaseOn(nextBase);
			runnerToMoveUp.setLoc(nextBase.getLoc().copy());
			runnerToMoveUp.setHomeBase(nextBase);
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
	
	//takes the baserunner off the runners list, removes them from the ui
	private void removeBaserunner (Baserunner player) {
		
		clearObject(player);
		runners.remove(player);
		
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
		
		//the ball is fielded when it is not a flyball, therefore no lock should be placed
		//if (!pickupSpot.inAir) {
		//	return Double.MAX_VALUE;
		//}
		
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

	public void setCurBatter (Player batter) {
		this.batter = batter;
	}
	
	public void setCurPitcher (Player pitcher) {
		this.pitcher = pitcher;
	}
	
	public Player getCurBatter () {
		return batter;
	}
	
	public Player getCurPitcher () {
		return pitcher;
	}
	
}