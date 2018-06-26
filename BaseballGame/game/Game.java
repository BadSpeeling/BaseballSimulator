package game;
import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import javax.swing.*;

import atbat.HitTypeCalculator;
import atbat.ThrownPitch;
import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import helpers.DebuggingBuddy;
import messages.AdvancingNumberOfBases;
import messages.BallOverWallMsg;
import messages.BaserunnerOutMsg;
import messages.DebugMessage;
import messages.FlyballCaughtMsg;
import messages.ForceOutMsg;
import messages.MakeNewDecisions;
import messages.Message;
import messages.RunScoredMsg;
import messages.RunnerOutMsg;
import numbers.RandomNumber;
import objects.Base;
import objects.BaseType;
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
import team.Team;
import testing.AllOnFieldObjectContainer;
import ui.GameDisplay;

/* Eric Frye
 * Game is a game of baseball. A game of baseball is played with two teams.  The rules of the game are governed by a RuleSet object.
 * */

public class Game {

	private final int WHITE = 0xFFFFFF;
	private final int BLACK = 0x000000;

	//these constants are to be used when accessing the fielders linkedlist
	public static final int PITCHERNUM = 0;
	public static final int CATCHERNUM = 1;
	public static final int FIRSTNUM = 2;
	public static final int SECONDNUM = 3;
	public static final int THIRDNUM = 4;
	public static final int SHORTNUM = 5;
	public static final int LEFTNUM = 6;
	public static final int CENTERNUM = 7;
	public static final int RIGHTNUM = 8;

	public static final int HOMEPLATE = 3;
	public static final int FIRSTBASE = 0;
	public static final int SECONDBASE = 1;
	public static final int THIRDBASE = 2;

	public final int WAIT;

	private int abNumber = 1;

	public int gID = 0; //ID of the game being played. The default value is 0.
	public boolean extraInnings = false; //If the game has entered extra innings.
	public RuleSet rules; //Rules for the game. 
	public GameTeam homeTeam; //Team for home team.
	public GameTeam awayTeam; //Teams for away team.
	public Linescore homeScore = new Linescore (true); //Hits Runs and Errors for home team.
	public Linescore awayScore = new Linescore (false); //Hits Runs and Errors for away team.
	public InningCounters inningsCTR = new InningCounters (); //Count of balls, strikes, outs, inning
	public GameState state = GameState.STANDARD; //A game starts in standard state.
	public GameTeam atBat ; //Away team always bats first.
	public GameTeam inField; //Home team always fields first.
	public Linescore toUpdate = awayScore; //Team at bat.
	public Linescore nextToUpdate = homeScore; //Team in the field.
	public Stadium stadium;
	public GameDisplay view;
	public Base [] bases = new Base [4]; 
	public static Queue <Message> messages = new LinkedList <Message> ();

	//score cards and extra variables to facilitate swapping
	private Scorecard homeStats; //home stats
	private Scorecard awayStats;  //away stats
	private Scorecard pitchingCard; //always has team pitching
	private Scorecard battingCard; //always has team batting
	
	private HitTypeCalculator hitTypeCalc = new HitTypeCalculator ();
	
	//debugging
	private AllOnFieldObjectContainer allObjs = null;
	
	public Game (RuleSet rules, int id, Team homeTeam, Team awayTeam, Stadium stadium, int wait) {
		
		hitTypeCalc.init();
		
		GameTeam home = homeTeam.makeInGameTeam(true);
		GameTeam away = awayTeam.makeInGameTeam(false);

		this.rules = rules;
		this.stadium = stadium;
		gID = id;
		atBat = away;
		inField = home;
		this.homeTeam = home;
		this.awayTeam = away;
		
		WAIT = wait;

		view = new GameDisplay (500,500, this.stadium.dim.get("f"), stadium, 1, awayTeam.tID, homeTeam.tID);
		
		//add players on field to cards
		for (Player curPlayerInStartingLineUp: atBat.inTheField) {
			view.getAwayStats().addBattingRow(curPlayerInStartingLineUp.pID, curPlayerInStartingLineUp.fullName());
		}

		for (Player curPlayerInStartingLineUp: inField.inTheField) {
			view.getHomeStats().addBattingRow(curPlayerInStartingLineUp.pID, curPlayerInStartingLineUp.fullName());
		}

		homeStats = new Scorecard (homeTeam.tID,1,view.getHomeStats());
		awayStats = new Scorecard (awayTeam.tID,1,view.getAwayStats());

		pitchingCard = homeStats;
		battingCard = awayStats;

		for (Player curPlayer: homeTeam.playersOnTeam) {
			homeStats.addPlayer(curPlayer.pID);
		}

		for (Player curPlayer: awayTeam.playersOnTeam) {
			awayStats.addPlayer(curPlayer.pID);
		}

		bases[0] = (new Base (FieldConstants.firstBase(), BaseType.FIRST, WHITE));
		bases[1] = (new Base (FieldConstants.secondBase(), BaseType.SECOND, WHITE));
		bases[2] = (new Base (FieldConstants.thirdBase(), BaseType.THIRD, WHITE));
		bases[3] = (new Base (FieldConstants.homePlate(), BaseType.HOME, WHITE));
		
	}
	
	public void saveLog (String folderName) {
		allObjs.writePlayToFile(folderName);
	}

	public int fieldEvent (LinkedList <Fielder> onTheField, BallInPlay hitBall, List <Baserunner> runners, Player batter, Player pitcher, PlateAppearance pa, boolean debugMode) {
				
		int retCode = 0;
		
		if (debugMode) {
			allObjs = new AllOnFieldObjectContainer ();
		}
		
		//store ball handler info
		Fielder currentlyHasBall = null;
		Fielder chasing = null;
		
		double time = 0;
		
		//recalculate landing spot whenever you need to
		BallInPlay airModel = hitBall.modelBallDistance(true, stadium);
		BallInPlay finalModel = hitBall.modelBallDistance(false, stadium);
		
		Map <String, BallInPlay> models = new HashMap <String, BallInPlay> ();
		models.put("aM", airModel);
		models.put("fM", finalModel);

		//add flag to make new decisions
		Message newFielderDes = new MakeNewDecisions();
		messages.add(newFielderDes);

		Baserunner newBaserunner = new Baserunner (batter, 0x00FF00);
		hitBall.batter = newBaserunner;

		runners.add(newBaserunner);

		//first always starts as a force out
		bases[FIRSTBASE].setForceOut(true);

		//set force outs for rest of bases
		for (int i = 1; i < bases.length; i++) {

			if (!bases[i-1].runnerOn()) {
				break;
			}

			bases[i].setForceOut(true);

		}
		
		for (Fielder curFielder: onTheField) {
			drawObject(curFielder);
			curFielder.setActionTimer(curFielder.gRats.reactionTime());
		}
		
		
		//draw ball marker
		view.drawBall(airModel.loc, 0x00FF00,0);

		int frame = 0;
		int outsRec = 0; //number of outs recorded during this play
		int curOuts = outsRec; //used to determine if outs new outs were records
		
		while (!hitBall.state.equals(BallStatus.DEAD)) {
			
			frame++;
						
			if (frame == 10000) {
				saveLog("D:\\Java_Projects\\BaseballSimulator\\temp_files");
			}
			
			//for saving play
			if (debugMode) {
					
				if (frame % 5 == 0) {
					allObjs.addCur(onTheField, hitBall, runners);
				}
					
			}
			
			//process messages
			for (Message cur: messages) {

				if (cur instanceof RunnerOutMsg) {
					runnerOut(runners,((RunnerOutMsg) cur).runner);
					outsRec++;
				}
				
				else if (cur instanceof DebugMessage) {
					retCode = -1;
				}

				else if (cur instanceof MakeNewDecisions) {

					chasing = fielderToGetBall(onTheField,models.get("fM"));
					decideRemainingFielders(onTheField,models.get("fM"), chasing);
					int basesTake = newBaserunner.batterBaseBrain(finalModel, onTheField, chasing, hitBall, bases);
					
					for (Baserunner curRunner: runners) {
						curRunner.baserunnerBrain(basesTake);
					}
					
				}

				else if (cur instanceof FlyballCaughtMsg) {

					runnerOut(runners,((FlyballCaughtMsg) cur).runner);
					outsRec++;

					for (Baserunner curRunner: runners) {
						curRunner.returnToHomeBase();
					}

				}

				else if (cur instanceof RunScoredMsg) {

					Baserunner curRunner = ((RunScoredMsg) cur).scorer;
					runScored(runners, curRunner, batter.pID, pitcher.pID);
					view.writeText(curRunner.fName + " has scored a run.");
				}

				else if (cur instanceof ForceOutMsg) {

					ForceOutMsg msg1 = (ForceOutMsg)cur;

					//determine who the forceout was
					for (Baserunner curRunner: runners) {

						if (curRunner.attempt == msg1.outAt) {
							runnerOut(runners,curRunner);
							outsRec++;
						}

						break;

					}

				}

				else if (cur instanceof BallOverWallMsg) {

					while (!runners.isEmpty())
						runScored(runners,runners.get(0), batter.pID, pitcher.pID);

					view.writeText(newBaserunner.fName + " has hit a homerun.");
					newBaserunner.setBestBaseAchieved(3);
					hitBall.state = BallStatus.DEAD;

				}

			}

			messages.clear(); //clear out messages for next turn

			//check if any outs were made on the last iteration, if so notify fielders to make new decisions
			if (curOuts != outsRec) {
				curOuts = outsRec;
			}

			time += Physics.tick;
			hitBall.tick(stadium, hitBall.thrown,false);

			for (Fielder cur: onTheField) {
				
				cur.decrementActionTimer();
				
				if (cur.canPerformAction()) {
					cur.run(bases, stadium.getWalls());
				}
					
			}

			for (Baserunner runner: runners) {
				runner.decrementActionTimer();
				runner.run(bases, stadium.getWalls());
			}
			
			if (chasing != null) {
				
				if (chasing.canGrabBall(hitBall.loc)) {
					chasing.receiveBall(hitBall, runners);
					currentlyHasBall = chasing;
					chasing = null;
				}
				
			}
			
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
			
			//draw(frame, onTheField, runners, hitBall, false);
			
			//check for play being over
			if (frame%100 == 0) {
				boolean over = true;

				for (Baserunner cur: runners) {

					if (cur.isAttempting()) {
						over = false;
						break;
					}

				}

				if (over) {
					hitBall.state = BallStatus.DEAD;
				}
			}

			
		}

		//determine play outcome
		if (outsRec == 0) {

			int bases = newBaserunner.getBestBaseAchieved();

			if (bases == 0) {
				pa.setOutcome(Result.S);
			}

			else if (bases == 1) {
				pa.setOutcome(Result.D);
			}

			else if (bases == 2) {
				pa.setOutcome(Result.T);
			}

			else if (bases == -1) {
				pa.setOutcome(Result.OUT);
			}

			else {
				pa.setOutcome(Result.HR);
			}

		}

		else {
			pa.setOutcome(Result.OUT);
		}

		view.writeText(batter.fullName() + "'s at bat has resulted in a(n) " + pa.getOutcome().toString());
		battingCard.addBattingStats(batter.pID, pa);
		pitchingCard.addPitchingStats(pitcher.pID, pa);

		if (pa.getOutcome().wasAHit()) {
			view.getLinescore().incHits(inningsCTR.isTop());
		}

		//remove ball locator
		clearObject(hitBall);
		view.drawBall(airModel.loc, 0x000000,airModel.getMarkerSize()); //remove ball marker
		return retCode;

	}
	
	public Scorecard getHomeStatline () {
		return homeStats;
	}
	
	public Scorecard getAwayStatline () {
		return awayStats;
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
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	//runs on basic values to calc ball in play
	private BallInPlay basicBallInPlayGenerator (Player curBatter) {
		
		return null;
		
	}
	
	public void playInning () {

		List <Baserunner> runners = new LinkedList <Baserunner> ();

		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();

		for (Player cur: inField.inTheField) {
			fielders.add(new Fielder(cur, 0x00FFFF));
		}

		while (inningsCTR.getOuts() < 3) {
						
			Player curBatter = atBat.lineup.next();
			Player curPitcher = inField.pitcher;
			PlateAppearance pa = new PlateAppearance (abNumber,curBatter.pID,curPitcher.pID,inningsCTR);

			BallInPlay hitBall = null;
			boolean walked = false;
			boolean struckOut = false;
			
			while (hitBall == null) {
				
				hitBall = new ThrownPitch(curPitcher,curBatter).throwBall(pa,stadium,hitTypeCalc);
				
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
					batterWalked(runners,curBatter,curPitcher.pID);
				}
				
				if (struckOut) {
					pa.setOutcome(Result.K);
					inningsCTR.incOuts();
				}
				
				battingCard.addBattingStats(curBatter.pID, pa);
				pitchingCard.addPitchingStats(curPitcher.pID, pa);
				continue;
				
			}
			
			//reset fielders
			for (Fielder curFielder: fielders) {
				clearObject(curFielder);
				curFielder.resetHasBall();
				curFielder.resetLoc();
				curFielder.destination = null;
				curFielder.baseGuard = null;
				curFielder.setThrowingDecisionMade(false);
				curFielder.setThrowingDestination(null);
				curFielder.setActionTimer(curFielder.gRats.reactionTime());
			}

			//reset baserunners
			for (Baserunner runner: runners) {

				runner.advancing();
				runner.setHomeBase(runner.baseOn);
				runner.loc = runner.baseOn.getBase().equiv(); //place on base

			}

			//reset bases
			for (int i = 0; i < bases.length; i++) {			
				bases[i].setForceOut(false);
				bases[i].clearFielder();
			}
			
			int code = fieldEvent(fielders, hitBall, runners, curBatter, curPitcher, pa, true);
			
			if (code == -1) {
				saveLog("D:\\Java_Projects\\BaseballSimulator\\temp_files");
			}
			
			//remove ball
			view.removeSpot(hitBall.loc, 1);

		}

		for (Fielder curFielder: fielders) {
			clearObject(curFielder);
		}

	}

	public void playGame () {

		boolean gameOver = false;
		
		while (!gameOver) {

			playInning();
			gameOver = halfInningOver();

		}
		
	}
 
	public void walkTester (Player batter, Player pitcher, List <Baserunner> runners) {
		batterWalked(runners,batter,pitcher.pID);
	}
	
	//advances all batters forced up to the next base
	private void batterWalked (List <Baserunner> runners, Player batter, int pID) {
		
		Baserunner curPlayerMovingUp = new Baserunner(batter, 0x00FF00);
		runners.add(curPlayerMovingUp);
		
		//run over bases first to third
		for (int i = 0; i < 3; i++) {
			
			Baserunner temp = bases[i].getRunnerOn();
			bases[i].arriveAtBase(curPlayerMovingUp);
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
			runScored(runners, curPlayerMovingUp, batter.pID, pID);
		}
		
	}
	
	//returns the fielder that can get the a hit ball the earliest.  their destination field will be properly updated
	private Fielder fielderToGetBall (List <Fielder> fielders, BallInPlay fullModel) {

		double bestTime = Double.MAX_VALUE;
		Fielder toRet = null;
		Coordinate3D loc = null;

		for (Fielder curFielder: fielders) {

			LocationTracker cur = curFielder.firstReachableSpot(fullModel);

			double distance = cur.loc.diff(curFielder.loc).mag();

			if (distance < bestTime) {
				bestTime = distance;
				toRet = curFielder;
				loc = cur.loc;
			}

		}
		
		if (toRet == null) {
			fielders.get(7).destination = fullModel.loc;
			return fielders.get(7);
		}
		
		else {
			toRet.destination = loc;
			return toRet;
		}
			

	}

	//draws the current location and removes the previous
	private void drawObject (OnFieldObject obj) {

		view.drawBall(obj.lastLoc, BLACK, obj.getMarkerSize()+1);
		obj.lastLoc = obj.loc.copy();
		view.drawBall(obj.loc, obj.getColor(), obj.getMarkerSize());

	}

	//clear ball spot
	private void clearObject (OnFieldObject obj) {
		view.drawBall(obj.lastLoc, BLACK, obj.getMarkerSize()+1);
		view.drawBall(obj.loc, BLACK, obj.getMarkerSize()+1);
	}

	//Swaps which team is fielding and which is batting.  To be used after an inning is over.
	private boolean halfInningOver () {

		//end of game
		if (!inningsCTR.isTop() && inningsCTR.getInning() == rules.numInnings) {
			return true;
		}

		inningsCTR.nextHalfInning();

		for (Base curBase: bases) {
			curBase.clearBase();
		}

		//swap team
		GameTeam temp1 = inField;
		inField = atBat;
		atBat = temp1;

		//swap line scores
		Linescore temp2 = toUpdate;
		toUpdate = nextToUpdate;
		nextToUpdate = temp2;

		Scorecard temp = pitchingCard;
		pitchingCard = battingCard;
		battingCard = temp;

		return false;

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
		inningsCTR.incOuts();

	}

	private void runScored (List <Baserunner> runners, OnFieldPlayer rem, int batterID, int pitcherID) {

		runners.remove(rem);
		clearObject(rem);
		toUpdate.runs++;

		//update stats
		battingCard.droveInRun(batterID);
		battingCard.scoredRun(rem.getID());
		pitchingCard.allowedRun(pitcherID);
		
		view.getLinescore().runScored(inningsCTR.getInning(), inningsCTR.isTop());
		
	}
	
	//writes the bases info to the display
	private void writeBasesToDebugger () {
		
		String [] val = {bases[0].toString(), bases[1].toString(), bases[2].toString(), bases[3].toString()};
		view.writeToDebuggerAndUpdate(val);
		
	}

	/*
	 * GameState defines the state of a baseball game.  
	 * Delay refers to any event that would delay a baseball game.   
	 * Over ends the loop that lets the next batter come to the plate.
	 * */
	enum GameState {
		STANDARD,DELAY,NEXT_RUN_WINS,OVER
	}

	/*
	 * AtBatState defines the state of an at bat.
	 * Ongoing is the standard case.
	 * Over means that the at bat has ended and a next batter is due up.  A change of half inning is also possible.  The GameState should be checked for OVER
	 * */
	enum AtBatState {
		ONGOING,OVER,BALL_IN_PLAY
	}


}
