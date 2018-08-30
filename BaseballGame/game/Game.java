package game;

import java.util.List;

import javax.swing.JTable;

import atbat.HitTypeCalculator;
import helpers.DebuggingBuddy;
import objects.GamePlayer;
import objects.GameTeam;
import stadium.Stadium;
import stats.PlateAppearance;
import team.Team;
import ui.BaseballGameDisplay;
import ui.FieldEventDisplay;
import ui.TeamBoxScore;
import view.StatsTable;
 
public class Game {
	
	private GameTeam homeTeam;
	private GameTeam awayTeam;
	private FieldEvent fieldEvent;
	private BaseballGameDisplay fullGameView;
	private int gameID;
	
	private int numOuts = 0;
	private int inning = 1;
	private boolean topOfInning = true;
	
	private boolean teamsSwitched = false; //lets containing objects know when the teams are switched
	
	//holds the team currently on offense
	private GameTeam onOffense;
	//holds the team currently pitching
	private GameTeam onDefense;
	
	public final static String [] battingStatsDisplayed = {"", "Name", "AB", "H", "R", "RBI", "K", "BB"};
	public final static String [] pitchingStatsDisplayed = {"Name", "IP", "H", "R", "BB", "K", "HR"}; 
	
	public Game (Team home, Team away, Stadium stadium, int id) {
		this.homeTeam = home.makeInGameTeam(true);
		this.awayTeam = away.makeInGameTeam(false);
		this.onOffense = awayTeam;
		this.onDefense = homeTeam;

		FieldEventDisplay fieldDisplay = new FieldEventDisplay (500,500,10,stadium);
		
		this.fieldEvent = new FieldEvent (1,stadium,fieldDisplay,onDefense.getCurrentPitcher());
		this.fullGameView  = new BaseballGameDisplay (fieldDisplay,1,awayTeam.getID(), homeTeam.getID()); 
		this.gameID = id;
		
	}
	
	public void playPlateAppearance () {
		
		GamePlayer nextBatter = onOffense.nextBatter();
		fieldEvent.batter = nextBatter;
		PlateAppearance paResult = fieldEvent.batterPitcherInteraction(onDefense.getFielders(), inning, numOuts);
		
		incrementOuts();
		
		for (Integer cur: fieldEvent.getIDRunnersScored()) {
			
			GamePlayer curPlayer = onOffense.getPlayer(cur);
			
			if (curPlayer != null) {
				curPlayer.scoredRun();
			}
			
		}
		
		if (!fieldEvent.getIDRunnersScored().isEmpty()) {
			nextBatter.droveInRuns(fieldEvent.getIDRunnersScored().size());
		}
		
		if (!fieldEvent.getIDRunnersScored().isEmpty()) {
			fieldEvent.pitcher.allowedRuns(fieldEvent.getIDRunnersScored().size());
		}
			
		nextBatter.addBattingPA(paResult);
		fieldEvent.pitcher.addPitchingPA(paResult);
		
	}
	

	public static Game basicGame () {
		
		Team home = new Team ();
		Team away = new Team ();
		Stadium stadium = Stadium.stdStadium();
		FieldEventDisplay disp = new FieldEventDisplay (500,500,10,stadium);
		
		home.addFakePlayers();
		away.addFakePlayers();
		
		return new Game (home, away, stadium, 1);
		
	}
	
	public boolean isGameOver () {
		return inning == 9 && !topOfInning; 
	}
	
	public void incrementOuts () {
		
		numOuts += fieldEvent.getIDRunnersOut().size();
		
		//check for inning over
		if (numOuts >= 3) {
			nextHalfInning();
		}
		
	}
	
	public void nextHalfInning () {

		inning += topOfInning ? 0 : 1;
		topOfInning = !topOfInning;
		numOuts = 0;
		fieldEvent.nextHalfInning();
		
		teamsSwitched = true;
		
		GameTeam temp = onOffense;
		onOffense = onDefense;
		onDefense = temp;
		
		fieldEvent.pitcher = onDefense.getCurrentPitcher();
		
	}
	
	public boolean didTeamsSwitch () {
		
		boolean result = teamsSwitched;
		
		if (teamsSwitched) {
			teamsSwitched = false;
		}
		
		return result;
		
	}
	
	public BaseballGameDisplay getFullGameView () {
		return fullGameView;
	}

	public GameTeam getHomeTeam() {
		return homeTeam;
	}

	public GameTeam getAwayTeam() {
		return awayTeam;
	}
		
}
