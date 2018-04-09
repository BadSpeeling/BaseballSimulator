import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;

/* Eric Frye
 * Game is a game of baseball. A game of baseball is played with two teams.  The rules of the game are governed by a RuleSet object.
 * */

public class Game {

	//these constants are to be used when accessing the fielders linkedlist
	static final int PITCHERNUM = 0;
	static final int CATCHERNUM = 1;
	static final int FIRSTNUM = 2;
	static final int SECONDNUM = 3;
	static final int THIRDNUM = 4;
	static final int SHORTNUM = 5;
	static final int LEFTNUM = 6;
	static final int CENTERNUM = 7;
	static final int RIGHTNUM = 8;

	int gID = 0; //ID of the game being played. The default value is 0.
	boolean extraInnings = false; //If the game has entered extra innings.
	RuleSet rules; //Rules for the game. 
	GameTeam homeTeam; //Team for home team.
	GameTeam awayTeam; //Teams for away team.
	Linescore homeScore = new Linescore (true); //Hits Runs and Errors for home team.
	Linescore awayScore = new Linescore (false); //Hits Runs and Errors for away team.
	InningCounters inningsCTR = new InningCounters (); //Count of balls, strikes, outs, inning
	GamePlayer [] bases = new GamePlayer [3]; //the bases
	GameState state = GameState.STANDARD; //A game starts in standard state.
	GameTeam atBat ; //Away team always bats first.
	GameTeam inField; //Home team always fields first.
	Linescore toUpdate = awayScore; //Team at bat.
	Linescore nextToUpdate = homeScore; //Team in the field.
	GamePlayer curBatter;
	GamePlayer curPitcher;
	Stadium stadium;
	GameDisplay view;
	GameLogger log = new GameLogger ();
	FieldEvent status = new FieldEvent ();
	
	public Game (RuleSet rules, int id, GameTeam homeTeam, GameTeam awayTeam, Stadium stadium) {

		this.rules = rules;
		this.stadium = stadium;
		gID = id;
		atBat = awayTeam;
		inField = homeTeam;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		curBatter = atBat.lineup.next();
		curPitcher = inField.pitcher;

		view = new GameDisplay (500,500, this.stadium.dim.get("f"), stadium);

		//view.drawField(-1*this.stadium.field.foulTerritory+1, this.stadium);
		//view.drawFieldOutline();

	}

	public void fieldEvent (LinkedList <Fielder> onTheField, BallInPlay hitBall, List <Baserunner> runners, GamePlayer batter) {
		
		double time = 0;
		
		//recalculate landing spot whenever you need to
		BallInPlay airModel = hitBall.modelBallDistance(true);
		BallInPlay finalModel = hitBall.modelBallDistance(false);

		
		Map <String, BallInPlay> models = new HashMap <String, BallInPlay> ();
		models.put("aM", airModel);
		models.put("fM", finalModel);
				
		Baserunner newBaserunner = new Baserunner (batter,status,log);
		
		newBaserunner.batterBaseBrain(models, onTheField, hitBall);
		runners.add(newBaserunner);
		
		while (!hitBall.state.equals(BallStatus.DEAD)) {
			
			time += Physics.tick;
			
			if (hitBall.state.equals(BallStatus.IN_AIR)) {
				view.drawBall(airModel.loc, 0x00FF00);
			}

			hitBall.tick(stadium, hitBall.thrown, status);
			
			//redraw how the field looks
			view.drawFieldOutline();
			view.drawBall(hitBall.lastLoc, 0x00000);
			view.drawBall(hitBall.loc, 0xFF0000);
			view.repaint();
						
			//players have been notified to make a new choice
			if (status.newFielderDecisions) {
				//update all fielders
				for (Fielder cur: onTheField) {
					cur.movementBrain(hitBall, models, log, status);
				}
				
				status.newFielderDecisions = false; //flip status - this will be re flipped below if updates are needed
				
			}
			
			//move and draw location
			for (Fielder cur: onTheField) {
				cur.move(hitBall, status, log);
				view.drawBall(cur.lastLoc, 0x000000);
				view.drawBall(cur.loc, 0x008000);
			}
			
			//moves and draw location
			for (Baserunner cur: runners) {
				if (status.newBaserunnerDecisions) {
					cur.baserunnerBrain(status.basesAttempt);
				}  //make new decisions if it has been signaled
				cur.run();
				view.drawBall(cur.lastLoc, 0x000000);
				view.drawBall(cur.loc, 0x0000FF);
			}
			
			status.newBaserunnerDecisions = false;
			
			/* BALL THROWING POLICY:
			 * 1) A fielder will update the field status to notify the game the that he will pick up the ball by updating FieldEvent.pickingUpBall
			 * 2) On notification, the fielder will pick up the ball and the ball's status will be changed to FIELDED.  
			 * the fielder will update the field status to announce he has picked up the ball
			 * 3) The fielder will then throw the ball and FieldEvent.pickingUpBall will be set to null.  Set flag for fielder to remake decisions
			 * The fielder will set the state of who is being thrown to, change the ball's status to THROWN and set the ball's thrown flag to true
			 * 4) The fielder to receive the ball will continually check if they can catch the ball until it is within 2 feet of them.  Upon receiving throw
			 * the ball's state should be set to as they were.  thrower and beingthrownTo states are reset
			 * */

			//process player picking up ball
			if (status.pickingUpBall != null) {
				status.pickingUpBall.receiveBall(hitBall, status, log);
			}

			//handle player throwing ball
			if (status.hasBall != null && !hitBall.thrown) {
				status.hasBall.throwingBrain(log, status, runners, hitBall);
				status.pickingUpBall = null;
				status.newFielderDecisions = true;
			}

			//check if fielder can receive throw. must be within 2 feet
			if (status.beingThrownTo != null && (status.beingThrownTo.loc.diff(hitBall.loc).mag() < 2)) {
				status.pickingUpBall = status.beingThrownTo;
				status.beingThrownTo = null;
				status.thrower = null;
				hitBall.thrown = false;
				status.newFielderDecisions = true;				
			}

			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
				
		/*
		System.out.println(status.beingThrownTo);
		System.out.println(status.thrower);
		System.out.println(status.hasBall);
		System.out.println(hitBall.thrown);
		System.out.println(status.pickingUpBall);
		System.out.println(hitBall.state);
		*/
		
	}

	public void playGame (boolean debug) {

		Scanner delay = new Scanner (System.in); //For debugging.

		System.out.println("\nThe game is in the top of the 1.");
		System.out.println(curPitcher.fullName() + " is on the mound.");

		while (state != GameState.OVER) {

			System.out.println("New at bat.");
			System.out.println(curBatter.fullName() + " is at the plate.");
			System.out.println("Outs: " + inningsCTR.outs);

			AtBatState curAtBatState = AtBatState.ONGOING;

			while (curAtBatState == AtBatState.ONGOING) {

				ThrownPitch incomingPitch = curPitcher.throwPitch(curBatter, inField.inTheField.get(CATCHERNUM));

			}

			/*
			 * After this point the at bat has ended.  All baserunner positions must now be updated.  All counters must be updated or reset.
			 * */

			//move to next half inning.
			if (inningsCTR.outs == 3) {
				state = halfInningOver();
				GameTeam temp = new GameTeam (atBat);
				atBat = new GameTeam (inField);
				inField = new GameTeam (temp);
				curPitcher = inField.pitcher;

				if (state != GameState.OVER)
					System.out.println(curPitcher.fullName() + " is on the mound.");

			}

			//next batter up
			curBatter = atBat.lineup.next();
			curAtBatState = AtBatState.ONGOING;
			inningsCTR.nextAtBat();

		}

		System.out.println("Game Over!");

	}

	//Swaps which team is fielding and which is batting.  To be used after an inning is over.
	public GameState halfInningOver () {

		//end of game
		if (!inningsCTR.top && inningsCTR.inning == rules.numInnings) {
			return GameState.OVER;
		}

		System.out.println("Switching sides.");
		inningsCTR.nextHalfInning();

		//clear bases
		for (int i = 0; i < bases.length; i++) {
			bases[i] = null;
		}		
		return GameState.STANDARD;

	}

	public void ballInPlay (BallInPlay hitBall) {

		final double tick = .05; //how many seconds go by in each tick.  20 ticks per second



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
