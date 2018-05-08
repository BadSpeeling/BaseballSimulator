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
import player.GamePlayer;
import stadium.Stadium;
import team.GameTeam;

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
	public GamePlayer curBatter;
	public GamePlayer curPitcher;
	public Stadium stadium;
	public GameDisplay view;
	public GameLogger log = new GameLogger ();
	//FieldEvent status = new FieldEvent ();
	public Base [] bases = new Base [4]; 
	public static Queue <Message> messages = new LinkedList <Message> ();
	
	public Game (RuleSet rules, int id, GameTeam homeTeam, GameTeam awayTeam, Stadium stadium, int wait) {

		this.rules = rules;
		this.stadium = stadium;
		gID = id;
		atBat = awayTeam;
		inField = homeTeam;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		curBatter = atBat.lineup.next();
		curPitcher = inField.pitcher;
		
		WAIT = wait;
		
		view = new GameDisplay (500,500, this.stadium.dim.get("f"), stadium);
		
		bases[0] = (new Base (FieldConstants.firstBase(), BaseType.FIRST, WHITE));
		bases[1] = (new Base (FieldConstants.secondBase(), BaseType.SECOND, WHITE));
		bases[2] = (new Base (FieldConstants.thirdBase(), BaseType.THIRD, WHITE));
		bases[3] = (new Base (FieldConstants.homePlate(), BaseType.HOME, WHITE));
		
		//view.drawField(-1*this.stadium.field.foulTerritory+1, this.stadium);
		//view.drawFieldOutline();
		
	}
	
	public void fieldEvent (LinkedList <Fielder> onTheField, BallInPlay hitBall, List <Baserunner> runners, GamePlayer batter) {
		
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
		List <Baserunner> runnersRemove =  new LinkedList <Baserunner> ();
		
		//draw ball marker
		view.drawBall(airModel.loc, 0x00FF00,0);
	
		while (!hitBall.state.equals(BallStatus.DEAD)) {
						
			//process messages
			for (Message cur: messages) {
					
				System.out.println(cur.getClass() + " " + cur);
				
				if (cur instanceof AdvancingNumberOfBases) {
										
					//brain for all runners
					for (Baserunner curRunner: runners) {
						
						if (curRunner.baserunnerBrain(((AdvancingNumberOfBases) cur).getNumBases())) {
							movingPlayers.add(curRunner);
						}
						
					}
					
				}
				
				else if (cur instanceof RunnerOutMsg) {
					runnerOut(runners,movingPlayers,((RunnerOutMsg) cur).runner);
				}
				
				else if (cur instanceof MakeNewDecisions) {
					
					Fielder chasing = fielderToGetBall(onTheField,models.get("fM"));
					movingPlayers.add(chasing);
					decideRemainingFielders(onTheField,models.get("fM"), chasing, movingPlayers);
					
				}
				
				else if (cur instanceof FlyballCaughtMsg) {
										
					runnerOut(runners,movingPlayers,((FlyballCaughtMsg) cur).runner);
					
					for (Baserunner curRunner: runners) {
						curRunner.returnToHomeBase();
					}
					
				}
				
				else if (cur instanceof RunScoredMsg) {
					Baserunner curRunner = ((RunScoredMsg) cur).scorer;
					runScored(runners, movingPlayers, curRunner);
				}
				
				else if (cur instanceof ForceOutMsg) {
					
					ForceOutMsg msg1 = (ForceOutMsg)cur;
					
					//determine who the forceout was
					for (Baserunner curRunner: runners) {
						
						if (curRunner.attempt == msg1.outAt) {
							runnerOut(runners,movingPlayers,curRunner);
						}
						
						break;
						
					}
					
				}
				
				else if (cur instanceof BallOverWallMsg) {
					
					while (!runners.isEmpty())
						runScored(runners,movingPlayers,runners.get(0));
					
					
					hitBall.state = BallStatus.DEAD;
					
				}
				
			}
		
			messages.clear(); //clear out messages for next turn
			
			time += Physics.tick;

			hitBall.tick(stadium, hitBall.thrown,false);
						
			//redraw how the field looks
			view.drawFieldOutline();
			drawObject(hitBall);
			view.repaint();
			
			List <OnFieldPlayer> newList = new LinkedList <OnFieldPlayer> ();
			
			//move all the players
			for (OnFieldPlayer mover: movingPlayers) {
				
				mover.decrementActionTimer();
				
				//add players back to list
				if (mover.run(bases, stadium.getWalls())) {
					newList.add(mover);
					drawObject(mover);
				}
				
			}
			
			movingPlayers = newList;
			
			for (Fielder cur: onTheField) {
				
				//pick up ball. last handler cannot pick up ball
				if (cur != lastBallHandler && (cur.loc.diff(hitBall.loc).mag() < 4) && hitBall.loc.z < cur.getReach()) {
					cur.receiveBall(hitBall, log, runners);
					currentlyHasBall = cur;
					lastBallHandler = cur;
				}
				
				drawObject(hitBall);
				
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
			
			/*
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		
			
			//check for play being over
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
		
		System.out.println("Event over.");
		//remove ball locator
		clearObject(hitBall);
		view.drawBall(airModel.loc, 0x000000,0); //remove ball marker
		return;
		
	}
	
	public void playInning () {
				
		List <Baserunner> runners = new LinkedList <Baserunner> ();
		
		LinkedList <Fielder> fielders = new LinkedList <Fielder> ();
		
		for (GamePlayer cur: inField.inTheField) {
			fielders.add(new Fielder(cur, 0x00FFFF));
		}
		
		while (inningsCTR.getOuts() < 3) {
			
			System.out.println("Num outs " + inningsCTR.getOuts());
			BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(RandomNumber.roll(0, 30)),Physics.degreesToRads(RandomNumber.roll(0, 90)),140,stadium,WHITE);
			//BallInPlay hitBall = new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(30), Physics.degreesToRads(10),150,stadium, WHITE);
						
			fieldEvent(fielders, hitBall, runners, atBat.lineup.next());
			
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
		
		view.drawBall(obj.lastLoc, BLACK, 1);
		view.drawBall(obj.loc, obj.getColor(), 1);
		
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
	
	private void runScored (List <Baserunner> runners, List <OnFieldPlayer> movingPlayers, OnFieldPlayer rem) {
		runners.remove(rem);
		movingPlayers.remove(rem);
		clearObject(rem);
		toUpdate.runs++;
		
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
