package game;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import atbat.HitTypeCalculator;
import atbat.ThrownPitch;
import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import messages.BallOverWallMsg;
import messages.DebugMessage;
import messages.FlyballCaughtMsg;
import messages.ForceOutMsg;
import messages.MakeNewDecisions;
import messages.Message;
import messages.RunScoredMsg;
import messages.RunnerOutMsg;
import objects.Base;
import objects.Baserunner;
import objects.Fielder;
import objects.OnFieldObject;
import objects.OnFieldPlayer;
import physics.Physics;
import player.Player;
import stadium.Stadium;
import stats.PlateAppearance;
import stats.Result;
import stats.Scorecard;
import team.GameTeam;
import testing.AllOnFieldObjectContainer;
import ui.GameDisplay;

//encapsulates pointers to the OnFieldPlayer that in doing something

public class FieldEvent {

	private List <Baserunner> runners;
	private List <Fielder> fielders;
	private GameDisplay view;
	private BallInPlay hitBall;
	private Player batter;
	private Player pitcher;
	private Stadium stadium;
	private Base [] bases;
	private List <Baserunner> playersScored = new <Baserunner> LinkedList ();
	private List <Fielder> playersOut = new <Fielder> LinkedList ();
	public static Queue <Message> messages = new LinkedList <Message> ();
	private HitTypeCalculator hitTypeCalc = new HitTypeCalculator ();
	private int abNumber;
	private int inning = 0;
	private int outs = 0;

	//debugging
	private AllOnFieldObjectContainer allObjs = new AllOnFieldObjectContainer ();
	private AllOnFieldObjectContainer prevAllObjs = new AllOnFieldObjectContainer ();

	public FieldEvent (int abNumber, List <Baserunner> runners, List <Fielder> fielders, GameDisplay view, Player pitcher, Player batter, Stadium stadium, Base [] bases) {
		this.runners = runners;
		this.fielders = fielders;
		this.view = view;
		this.batter = batter;
		this.pitcher = pitcher;
		this.stadium = stadium;
		this.bases = bases;
		this.abNumber = abNumber;
		hitTypeCalc.init();
	}

	public PlateAppearance batterPitcherInteraction () {

		PlateAppearance pa = new PlateAppearance (abNumber,batter.getpID(),pitcher.getpID(), inning, outs);

		boolean walked = false;
		boolean struckOut = false;

		while (hitBall == null) {

			hitBall = new ThrownPitch(pitcher,batter).throwBall(pa,stadium,hitTypeCalc);

			if (pa.isStrikeout()) {
				struckOut = true;
				break;
			}

			if (pa.isWalk()) {
				walked = true;
				break;
			}

		}

		//BB or K
		if (hitBall == null) {

			if (walked) {
				pa.setOutcome(Result.BB);
				batterWalked(runners,batter,pitcher.getpID());
			}

			if (struckOut) {
				pa.setOutcome(Result.K);
				outs++;
			}

		}

		else {
			int bestBase = fieldEvent(fielders,hitBall,runners,new Baserunner(batter,0xFFFFFF), true);
			setPAOutcome(pa, bestBase);
		}

		postPlayWork();
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

	}

	private int fieldEvent (List <Fielder> onTheField, BallInPlay hitBall, List <Baserunner> runners, Baserunner newBaserunner, boolean debugMode) {

		prevAllObjs = allObjs;
		allObjs = new AllOnFieldObjectContainer ();

		int retCode = 0;
		messages.clear();

		//store ball handler info
		Fielder currentlyHasBall = null;
		Fielder chasing = null;
		Fielder toHoldBall = null;

		double time = 0;

		LocationTracker pickUpSpot = null;
		//recalculate landing spot whenever you need to
		BallInPlay airModel = hitBall.modelBallDistance(true, stadium);
		BallInPlay finalModel = hitBall.modelBallDistance(false, stadium);

		Map <String, BallInPlay> models = new HashMap <String, BallInPlay> ();
		models.put("aM", airModel);
		models.put("fM", finalModel);

		//add flag to make new decisions
		Message newFielderDes = new MakeNewDecisions();
		messages.add(newFielderDes);

		hitBall.batter = newBaserunner;

		//set force outs for rest of bases
		for (int i = 1; i < bases.length; i++) {

			if (!bases[i-1].isRunnerOn()) {
				break;
			}

			bases[i].setForceOut(true);
			bases[i].setToBeForced(bases[i-1].getRunnerOn());

		}

		for (Fielder curFielder: onTheField) {
			drawObject(curFielder);
			curFielder.setActionTimer(curFielder.getPlayer().getgRatings().reactionTime());
		}

		//draw ball marker
		view.drawBall(airModel.getLoc(), 0x00FF00,0);

		int frame = 0;
		int outsRec = 0; //number of outs recorded during this play
		int curOuts = outsRec; //used to determine if outs new outs were records

		while (true) {

			//clean up game
			if (frame == 5000) {

				runners = validStateSetter(runners);
				return -1;

			}

			//process messages
			for (Message cur: messages) {

				if (cur instanceof RunnerOutMsg) {
					runnerOut(runners,((RunnerOutMsg) cur).runner);
					outsRec++;
				}

				else if (cur instanceof DebugMessage) {
				}

				else if (cur instanceof MakeNewDecisions) {

					chasing = fielderToGetBall(onTheField,models.get("fM"));
					toHoldBall = chasing;
					decideRemainingFielders(onTheField,models.get("fM"), chasing);
					pickUpSpot = chasing.firstReachableSpot(finalModel);

					newBaserunner.attemptBase(bases[0]);
					bases[0].setForceOut(true);
					bases[0].setToBeForced(newBaserunner);

					for (Baserunner curRunner: runners) {
						curRunner.getNewDestination(bases, toHoldBall, pickUpSpot, hitBall);
					}

					runners.add(newBaserunner);

				}

				else if (cur instanceof FlyballCaughtMsg) {

					runnerOut(runners,((FlyballCaughtMsg) cur).runner);
					outsRec++;					

					for (Baserunner curRunner: runners) {
						curRunner.reverseDirection();
						curRunner.getHomeBase().setForceOut(true);
						curRunner.getHomeBase().setToBeForced(curRunner);
					}

				}

				else if (cur instanceof RunScoredMsg) {

					Baserunner curRunner = ((RunScoredMsg) cur).scorer;
					runScored(runners, curRunner, batter.getpID(), pitcher.getpID());

				}

				else if (cur instanceof ForceOutMsg) {

					ForceOutMsg msg1 = (ForceOutMsg)cur;

					//determine who the forceout was
					runnerOut(runners,msg1.outAt);
					outsRec++;

				}

				else if (cur instanceof BallOverWallMsg) {

					while (!runners.isEmpty())
						runScored(runners,runners.get(0), batter.getpID(), pitcher.getpID());

					newBaserunner.setBestBaseAchieved(3);
					hitBall.state = BallStatus.DEAD;

				}

			}
			
			if (playIsOver()) {
				break;
			}

			frame++;

			messages.clear(); //clear out messages for next turn

			//draw(frame, onTheField, runners, hitBall, true);	

			//check if any outs were made on the last iteration, if so notify fielders to make new decisions
			if (curOuts != outsRec) {
				curOuts = outsRec;
			}

			time += Physics.tick;
			hitBall.tick(stadium, hitBall.loose,false);

			//move fielders
			for (Fielder cur: onTheField) {

				cur.decrementActionTimer();

				//check that they do not have timers on
				if (cur.canPerformAction()) {
					cur.run(bases, stadium.getWalls());
				}

			}

			//move baserunenrs
			for (Baserunner runner: runners) {
				runner.decrementActionTimer();

				if (runner.destination != null) {
					runner.run(bases, toHoldBall, pickUpSpot, hitBall);
				}

			}

			//check if chasing player can get ball
			if (chasing != null) {

				if (chasing.canGrabBall(hitBall.getLoc())) {
					chasing.receiveBall(hitBall, runners);
					currentlyHasBall = chasing;
					toHoldBall = chasing;
					chasing = null;
				}

			}

			//when the player has the ball, decide what to do with it
			if (currentlyHasBall != null) {

				//decide who to throw the ball to
				if (currentlyHasBall.needToMakeThrowingDecision()) {
					chasing = currentlyHasBall.throwingBrain(bases, runners, hitBall, onTheField); //sets player being thrown to
				}

				//throw ball
				else if (currentlyHasBall.getThrowingDestination() != null){

					if (currentlyHasBall.canPerformAction()) {
						currentlyHasBall.throwBall(hitBall, bases);
						currentlyHasBall = null;
					}

				}

			}


		}

		//remove ball locator
		clearObject(hitBall);
		view.removeSpot(hitBall.getLoc(), 1);
		view.drawBall(airModel.getLoc(), 0x000000,airModel.getMarkerSize()); //remove ball marker
		return newBaserunner.getBestBaseAchieved();

	}

	private void postPlayWork () {

		//reset fielders
		for (Fielder curFielder: fielders) {
			clearObject(curFielder);
			curFielder.reset();
		}

		runners = validStateSetter(runners);
		
		//reset baserunners
		for (Baserunner runner: runners) {

			runner.advancing();
			runner.resetLastBaseAttempt();
			runner.setHomeBase(runner.baseOn);
			runner.setLoc(runner.baseOn.getBase().equiv()); //place on base				

		}

		//reset bases
		for (int i = 0; i < bases.length; i++) {			
			bases[i].setForceOut(false);
			bases[i].clearForNextAB();
		}

	}

	//draws the current location and removes the previous
	private void drawObject (OnFieldObject obj) {

		view.drawBall(obj.lastLoc, 0x000000, obj.getMarkerSize()+1);
		obj.lastLoc = obj.getLoc().copy();
		view.drawBall(obj.getLoc(), obj.getColor(), obj.getMarkerSize());

	}

	//clear ball spot
	private void clearObject (OnFieldObject obj) {
		view.drawBall(obj.lastLoc, 0x000000, obj.getMarkerSize()+1);
		view.drawBall(obj.getLoc(), 0x000000, obj.getMarkerSize()+1);
	}

	//advances all batters forced up to the next base
	private void batterWalked (List <Baserunner> runners, Player batter, int pID) {

		Baserunner curPlayerMovingUp = new Baserunner(batter, 0x00FF00);
		runners.add(curPlayerMovingUp);

		//run over bases first to third
		for (int i = 0; i < 3; i++) {

			Baserunner temp = bases[i].getRunnerOn();
			curPlayerMovingUp.arriveAtBase(bases[i]);
			curPlayerMovingUp.baseOn = bases[i];

			//end if no force
			if (temp == null) {
				return;
			}

			else {
				curPlayerMovingUp = temp;
			}

		}

		//handle player coming home
		if (curPlayerMovingUp != null) {
			runScored(runners, curPlayerMovingUp, batter.getpID(), pID);
		}

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

	/* 
	 * determines the action for the remaining fielders.  assumes that status.chasingBall has been updated
	 * @param model the ball in play being modelled
	 * */
	private void decideRemainingFielders (List <Fielder> fielders, BallInPlay model, Fielder chaser) {

		for (Fielder curFielder: fielders) {

			if (curFielder.destination == null) {
				curFielder.movementBrain(model,bases,fielders,chaser,stadium.getWalls());
			}

		}

	}  

	//removes runner from event, inc outs
	private void runnerOut (List <Baserunner> runners, OnFieldPlayer rem) {

		runners.remove(rem);
		clearObject(rem);

	}

	private void runScored (List <Baserunner> runners, OnFieldPlayer rem, int batterID, int pitcherID) {

		runners.remove(rem);
		clearObject(rem);

	}
	
	private void handleBaserunners () {
		
		for (Baserunner curRunner: runners) {
			
		}
		
	}
	
	private void handleFielders () {
		
		
		
	}

	private void writeBaseRunners () {
		String [] vals = new String [runners.size()];

		for (int i = 0; i < vals.length; i++) {
			vals[i] = runners.get(i).toString();
		}

		view.writeToDebuggerAndUpdate(vals);
	}
	
	private boolean playIsOver () {
		
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
			view.repaint();

		}

		if (wait) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}