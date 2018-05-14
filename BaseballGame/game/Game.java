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

import ball.BallInPlay;
import ball.BallStatus;
import ball.LocationTracker;
import datatype.Coordinate3D;
import datatype.RandomNumber;
import main.Base;
import main.BaseType;
import main.Baserunner;
import main.Fielder;
import main.MakeNewDecisions;
import main.OnFieldObject;
import main.OnFieldPlayer;
import messages.AdvancingNumberOfBases;
import messages.BallOverWallMsg;
import messages.BaserunnerOutMsg;
import messages.FlyballCaughtMsg;
import messages.ForceOutMsg;
import messages.Message;
import messages.RunScoredMsg;
import messages.RunnerOutMsg;
import physics.Physics;
import player.Player;
import stadium.Stadium;
import stats.PlateAppearance;
import stats.Result;
import stats.Scorecard;
import team.GameTeam;
import team.Team;
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
	public GameLogger log = new GameLogger ();
	//FieldEvent status = new FieldEvent ();
	public Base [] bases = new Base [4]; 
	public static Queue <Message> messages = new LinkedList <Message> ();

	private Scorecard homeStats;
	private Scorecard awayStats;
	private Scorecard pitchingCard;
	private Scorecard battingCard;

	public Game (RuleSet rules, int id, Team homeTeam, Team awayTeam, Stadium stadium, int wait) {

		GameTeam home = homeTeam.makeInGameTeam(true);
		GameTeam away = awayTeam.makeInGameTeam(false);

		this.rules = rules;
		this.stadium = stadium;
		gID = id;
		atBat = away;
		inField = home;
		this.homeTeam = home;
		this.awayTeam = away;

		homeStats = new Scorecard (homeTeam.tID,1);
		awayStats = new Scorecard (awayTeam.tID,1);

		pitchingCard = homeStats;
		battingCard = awayStats;

		for (Player curPlayer: homeTeam.playersOnTeam) {
			homeStats.addPlayer(curPlayer.pID);
		}

		for (Player curPlayer: awayTeam.playersOnTeam) {
			awayStats.addPlayer(curPlayer.pID);
		}
	
		WAIT = wait;

		view = new GameDisplay (500,500, this.stadium.dim.get("f"), stadium);
		
		for (Player curPlayerInStartingLineUp: atBat.inTheField) {
			view.getAwayStats().addBattingRow(curPlayerInStartingLineUp.pID, curPlayerInStartingLineUp.fullName());
		}
		
		bases[0] = (new Base (FieldConstants.firstBase(), BaseType.FIRST, WHITE));
		bases[1] = (new Base (FieldConstants.secondBase(), BaseType.SECOND, WHITE));
		bases[2] = (new Base (FieldConstants.thirdBase(), BaseType.THIRD, WHITE));
		bases[3] = (new Base (FieldConstants.homePlate(), BaseType.HOME, WHITE));


	}

	public void fieldEvent (LinkedList <Fielder> onTheField, BallInPlay hitBall, List <Baserunner> runners, Player batter, Player pitcher, PlateAppearance pa) {

		Fielder lastBallHandler = null;
		Fielder currentlyHasBall = null;

		double time = 0;

		//recalculate landing spot whenever you need to
		BallInPlay airModel = hitBall.modelBallDistance(true);
		BallInPlay finalModel = hitBall.modelBallDistance(false);

		Map <String, BallInPlay> models = new HashMap <String, BallInPlay> ();
		models.put("aM", airModel);
		models.put("fM", finalModel);

		//add flag to make new decisions
		Message newFielderDes = new MakeNewDecisions();
		messages.add(newFielderDes);

		Baserunner newBaserunner = new Baserunner (batter,log, 0x00FF00);
		hitBall.batter = newBaserunner;

		newBaserunner.batterBaseBrain(models, onTheField, hitBall, bases);

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
		}

		List <OnFieldPlayer> movingPlayers = new LinkedList <OnFieldPlayer> ();

		//draw ball marker
		view.drawBall(airModel.loc, 0x00FF00,0);

		int frame = 0;
		int outsRec = 0; //number of outs recorded during this play

		while (!hitBall.state.equals(BallStatus.DEAD)) {

			frame++;

			//process messages
			for (Message cur: messages) {

				view.writeText(cur.toString());

				if (cur instanceof AdvancingNumberOfBases) {

					//brain for all runners
					for (Baserunner curRunner: runners) {
						curRunner.baserunnerBrain(((AdvancingNumberOfBases) cur).getNumBases());
					}

				}

				else if (cur instanceof RunnerOutMsg) {
					runnerOut(runners,movingPlayers,((RunnerOutMsg) cur).runner);
					outsRec++;
				}

				else if (cur instanceof MakeNewDecisions) {

					Fielder chasing = fielderToGetBall(onTheField,models.get("fM"));
					decideRemainingFielders(onTheField,models.get("fM"), chasing, movingPlayers);

				}

				else if (cur instanceof FlyballCaughtMsg) {

					runnerOut(runners,movingPlayers,((FlyballCaughtMsg) cur).runner);
					outsRec++;

					for (Baserunner curRunner: runners) {
						curRunner.returnToHomeBase();
					}

				}

				else if (cur instanceof RunScoredMsg) {
					Baserunner curRunner = ((RunScoredMsg) cur).scorer;
					runScored(runners, movingPlayers, curRunner, batter.pID, pitcher.pID);
				}

				else if (cur instanceof ForceOutMsg) {

					ForceOutMsg msg1 = (ForceOutMsg)cur;

					//determine who the forceout was
					for (Baserunner curRunner: runners) {

						if (curRunner.attempt == msg1.outAt) {
							runnerOut(runners,movingPlayers,curRunner);
							outsRec++;
						}

						break;

					}

				}

				else if (cur instanceof BallOverWallMsg) {

					while (!runners.isEmpty())
						runScored(runners,movingPlayers,runners.get(0), batter.pID, pitcher.pID);

					pa.setOutcome(Result.HR);
					hitBall.state = BallStatus.DEAD;

				}

			}

			messages.clear(); //clear out messages for next turn

			time += Physics.tick;
			hitBall.tick(stadium, hitBall.thrown,false);

			for (Fielder cur: onTheField) {

				cur.run(bases, stadium.getWalls());

				//pick up ball. last handler cannot pick up ball
				if (cur != lastBallHandler && (cur.loc.diff(hitBall.loc).mag() < 4) && hitBall.loc.z < cur.getReach()) {
					cur.receiveBall(hitBall, log, runners);
					currentlyHasBall = cur;
					lastBallHandler = cur;
				}

			}

			for (Baserunner runner: runners) {
				runner.run(bases, stadium.getWalls());
			}

			//has ball and needs to decide what to do with it
			if (currentlyHasBall != null) {

				currentlyHasBall.decrementActionTimer();

				if (currentlyHasBall.canPerformAction()) {

					//TODO this can be significantly improved
					if (currentlyHasBall.destination == null && currentlyHasBall.getThrowingDestination() == null) {
						currentlyHasBall.throwingBrain(bases, runners, hitBall, onTheField);
					}

					else if (currentlyHasBall.getThrowingDestination() != null) {
						currentlyHasBall.throwBall(hitBall, bases);
						currentlyHasBall = null;
					}

				}

			}

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

			//update image
			if (frame % 12 == 0) {
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

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//runner is standing on a base, determine what kind of hit they had
		if (newBaserunner.baseOn != null) {

			switch (newBaserunner.baseOn.getBase()) {
			case FIRST:
				pa.setOutcome(Result.S);
				break;
			case SECOND:
				pa.setOutcome(Result.D);
				break;
			case THIRD:
				pa.setOutcome(Result.T);
				break;
			default:
				break;
			}

		}
		
		//batter is not on a base, an out has been recorded
		else {
			pa.setOutcome(Result.OUT);
		}
		
		System.out.println(battingCard);
		System.out.println(pitchingCard);
		System.out.println(batter.pID);
		battingCard.addBattingStats(batter.pID, pa);
		pitchingCard.addPitchingStats(pitcher.pID, pa);
		
		System.out.println("Event over.");
		//remove ball locator
		clearObject(hitBall);
		view.drawBall(airModel.loc, 0x000000,airModel.getMarkerSize()); //remove ball marker
		return;

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

			System.out.println("Num outs " + inningsCTR.getOuts());
			BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(RandomNumber.roll(0, 30)),Physics.degreesToRads(RandomNumber.roll(0, 90)),140,stadium,WHITE);
			//BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(0), Physics.degreesToRads(10),145,stadium, WHITE);

			fieldEvent(fielders, hitBall, runners, curBatter, curPitcher, pa);

			//reset fielders
			for (Fielder curFielder: fielders) {
				clearObject(curFielder);
				curFielder.resetHasBall();
				curFielder.resetLoc();
				curFielder.destination = null;
				curFielder.baseGuard = null;
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

		toRet.destination = loc;
		return toRet;

	}

	//draws the current location and removes the previous
	private void drawObject (OnFieldObject obj) {

		view.drawBall(obj.lastLoc, BLACK, obj.getMarkerSize()+1);
		obj.lastLoc = obj.loc.copy();
		view.drawBall(obj.loc, obj.getColor(), obj.getMarkerSize());

	}

	//clear ball spot
	private void clearObject (OnFieldObject obj) {
		view.drawBall(obj.lastLoc, BLACK, 1);
		view.drawBall(obj.loc, BLACK, 1);
	}

	//Swaps which team is fielding and which is batting.  To be used after an inning is over.
	private boolean halfInningOver () {

		//end of game
		if (!inningsCTR.isTop() && inningsCTR.getInning() == rules.numInnings) {
			return true;
		}

		System.out.println("Switching sides.");
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
	private void decideRemainingFielders (List <Fielder> fielders, BallInPlay model, Fielder chaser, List <OnFieldPlayer> moving) {

		for (Fielder curFielder: fielders) {

			if (curFielder.destination == null) {
				if (curFielder.movementBrain(model,bases,fielders,chaser,stadium.getWalls()))
					moving.add(curFielder);
			}

		}

	} 


	//removes runner from event, inc outs
	private void runnerOut (List <Baserunner> runners, List <OnFieldPlayer> movingPlayers, OnFieldPlayer rem) {

		runners.remove(rem);
		movingPlayers.remove(rem);
		clearObject(rem);
		inningsCTR.incOuts();

	}

	private void runScored (List <Baserunner> runners, List <OnFieldPlayer> movingPlayers, OnFieldPlayer rem, int batterID, int pitcherID) {

		runners.remove(rem);
		movingPlayers.remove(rem);
		clearObject(rem);
		toUpdate.runs++;

		//update stats
		battingCard.droveInRun(batterID);
		battingCard.scoredRun(rem.getID());
		pitchingCard.allowedRun(pitcherID);

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
