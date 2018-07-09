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

	//score cards and extra variables to facilitate swapping
	private Scorecard homeStats; //home stats
	private Scorecard awayStats;  //away stats
	private Scorecard pitchingCard; //always has team pitching
	private Scorecard battingCard; //always has team batting

	//debugging
	private AllOnFieldObjectContainer allObjs = null;

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

		WAIT = wait;

		view = new GameDisplay (500,500, this.stadium.dim.get("f"), stadium, 1, awayTeam.tID, homeTeam.tID);

		//add players on field to cards
		for (Player curPlayerInStartingLineUp: atBat.inTheField) {
			view.getAwayStats().addBattingRow(curPlayerInStartingLineUp.getpID(), curPlayerInStartingLineUp.fullName());
		}

		for (Player curPlayerInStartingLineUp: inField.inTheField) {
			view.getHomeStats().addBattingRow(curPlayerInStartingLineUp.getpID(), curPlayerInStartingLineUp.fullName());
		}

		homeStats = new Scorecard (homeTeam.tID,1,view.getHomeStats());
		awayStats = new Scorecard (awayTeam.tID,1,view.getAwayStats());

		pitchingCard = homeStats;
		battingCard = awayStats;

		for (Player curPlayer: homeTeam.playersOnTeam) {
			homeStats.addPlayer(curPlayer);
		}

		for (Player curPlayer: awayTeam.playersOnTeam) {
			awayStats.addPlayer(curPlayer);
		}

		bases[0] = (new Base (FieldConstants.firstBase(), BaseType.FIRST, WHITE));
		bases[1] = (new Base (FieldConstants.secondBase(), BaseType.SECOND, WHITE));
		bases[2] = (new Base (FieldConstants.thirdBase(), BaseType.THIRD, WHITE));
		bases[3] = (new Base (FieldConstants.homePlate(), BaseType.HOME, WHITE));

	}

	public void saveLog (String folderName) {
		allObjs.writePlayToFile(folderName);
	}

	public Scorecard getHomeStatline () {
		return homeStats;
	}

	public Scorecard getAwayStatline () {
		return awayStats;
	}

	/*
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
	 */

	public void playInning () {

		List <Baserunner> runners = new LinkedList <Baserunner> ();

		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();

		for (Player cur: inField.inTheField) {
			fielders.add(new Fielder(cur, 0x00FFFF));
		}

		while (inningsCTR.getOuts() < 3) {

			/*
			Player curBatter = atBat.lineup.next();
			Player curPitcher = inField.pitcher;
			PlateAppearance pa = new PlateAppearance (abNumber,curBatter.getpID(),curPitcher.getpID(),inningsCTR);

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
					batterWalked(runners,curBatter,curPitcher.getpID());
				}

				if (struckOut) {
					pa.setOutcome(Result.K);
					inningsCTR.incOuts();
				}

				battingCard.addBattingStats(curBatter.getpID(), pa);
				pitchingCard.addPitchingStats(curPitcher.getpID(), pa);
				continue;

			}
			 */

			/*
			//reset fielders
			for (Fielder curFielder: fielders) {
				clearObject(curFielder);
				curFielder.reset();
			}

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

			int code = fieldEvent(fielders, hitBall, runners, curBatter, curPitcher, pa, true);

			if (code == -1) {
				validStateSetter(runners);
				System.out.println("error!");
				//saveLog("D:\\Java_Projects\\BaseballSimulator\\temp_files");
			}
			 */

		}

		for (Fielder curFielder: fielders) {
			clearObject(curFielder);
		}

	}

	//clear ball spot
	private void clearObject (OnFieldObject obj) {
		view.drawBall(obj.lastLoc, 0x000000, obj.getMarkerSize()+1);
		view.drawBall(obj.getLoc(), 0x000000, obj.getMarkerSize()+1);
	}

	public void playGame () {

		boolean gameOver = false;

		while (!gameOver) {

			playInning();
			gameOver = halfInningOver();

		}

		homeStats.updateSeasonStats();
		awayStats.updateSeasonStats();

		view.setVisible(false);

	}

	//Swaps which team is fielding and which is batting.  To be used after an inning is over.
	private boolean halfInningOver () {

		//end of game
		if (!inningsCTR.isTop() && inningsCTR.getInning() == rules.numInnings) {
			return true;
		}

		inningsCTR.nextHalfInning();

		for (Base curBase: bases) {
			curBase.clearForNextInning();
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
