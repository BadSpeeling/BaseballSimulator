package game;

import atbat.HitTypeCalculator;
import helpers.DebuggingBuddy;
import player.Player;
import stadium.Stadium;
import team.GameTeam;
import team.Team;
import ui.FieldEventDisplay;

public class Game {
	
	private GameTeam homeTeam;
	private GameTeam awayTeam;
	private FieldEvent field;
	
	//holds the team currently on offense
	private GameTeam onOffense;
	//holds the team currently pitching
	private GameTeam onDefense;
		
	public Game (Team home, Team away, Stadium stadium, FieldEventDisplay display) {
		this.homeTeam = home.makeInGameTeam(true);
		this.awayTeam = away.makeInGameTeam(false);
		this.onOffense = awayTeam;
		this.onDefense = homeTeam;

		field = new FieldEvent (1,display,stadium);
	}
	
	public void playInning () {
		
		Player playerOnTheMound = onDefense.getCurrentPitcher();
		field.pitcher = playerOnTheMound;
		
		while (field.getNumOuts() < 3) {
			
			Player nextBatter = onOffense.nextBatter();
			field.batter = nextBatter;
			field.batterPitcherInteraction(onDefense.getFielders());
			
			System.out.println(field.getNumOuts());
			DebuggingBuddy.wait(field.view);
			
		}
		
	}
	
}
