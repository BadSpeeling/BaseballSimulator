package game;

import javax.swing.JTable;

import atbat.HitTypeCalculator;
import helpers.DebuggingBuddy;
import player.Player;
import stadium.Stadium;
import team.GameTeam;
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
		
		this.fieldEvent = new FieldEvent (1,stadium,fieldDisplay);
		this.fullGameView  = new BaseballGameDisplay (fieldDisplay,1,awayTeam.getID(), homeTeam.getID()); 
		this.gameID = id;
		
	}
	
	public void playInning () {
		
		Player playerOnTheMound = onDefense.getCurrentPitcher();
		fieldEvent.pitcher = playerOnTheMound;
		
		while (fieldEvent.getNumOuts() < 3) {
			
			Player nextBatter = onOffense.nextBatter();
			fieldEvent.batter = nextBatter;
			fieldEvent.batterPitcherInteraction(onDefense.getFielders());
						
		}
		
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
