import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.*;

/* Eric Frye
 * Game is a game of baseball. A game of baseball is played with two teams.  The rules of the game are governed by a RuleSet object.
 * */

public class Game {
	
	final int PITCHERNUM = 0;
	final int CATCHERNUM = 1;
	final int FIRSTNUM = 2;
	final int SECONDNUM = 3;
	final int THIRDNUM = 4;
	final int SHORTNUM = 5;
	final int LEFTNUM = 6;
	final int CENTERNUM = 7;
	final int RIGHTNUM = 8;
	
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
	
	public void liveBallDriver (LinkedList <Fielder> onTheField, BallInPlay hitBall) {
		
		//recalculate landing spot whenever you need to
		Coordinate3D landingSpot = hitBall.modelBallDistance(stadium);
				
		while (!hitBall.state.equals(BallStatus.DEAD)) {
						
			if (hitBall.state.equals(BallStatus.IN_AIR)) {
				view.drawBall(landingSpot, 0x00FF00);
			}
			
			//redraw how the field looks
			view.drawFieldOutline();
			view.drawBall(hitBall.lastLoc, 0x00000);
			view.drawBall(hitBall.loc, 0xFF0000);
			view.repaint();
			
			hitBall.tick(stadium);
			
			//update all fielders
			for (Fielder cur: onTheField) {
				cur.brain(hitBall, stadium, landingSpot, log);
				view.drawBall(cur.lastLoc, 0x000000);
				view.drawBall(cur.loc, 0x008000);
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
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
								
				/*TODO - catcher decides on pitch effect: pitch to, pitch around, swing and miss and the pitch selection*/
				
				/*TODO - pitcher throws the pitch: effective, average, poor, meatball*/
				
				ThrownPitch incomingPitch = curPitcher.throwPitch(curBatter, inField.inTheField.get(CATCHERNUM));
				
				/*TODO - batter sees the pitch and decides to swing or not*/
				
				/*TODO - result of the pitch is determined*/
				
				/*TODO - if ball is hit in play, determine to which fielder it is hit*/
				
				/*TODO - batter, baserunners, and fielders react to ball in play. */
				
				/*TODO - update BSO counters*/
								
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
