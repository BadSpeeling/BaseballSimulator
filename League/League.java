import java.awt.Container;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import FileSystem.LocalFile;
import ID.Serialized;
import game.Game;
import team.Team;

public class League extends Serialized {
	
	private Map <Integer, Team> teams;
	private int currentYear;
	private int nextGameID;
	
	private Map <Integer, Game> activeGame;
	
	public League (int id, int year) {
		
		super(id);
		teams = new HashMap <Integer, Team> ();
		this.currentYear = year;
		this.nextGameID = 1;
		this.activeGame = new HashMap <Integer, Game> ();
		
	}
	
	public void addTeam (Team toAdd) {
		teams.put(toAdd.getID(), toAdd);
	}
	
	/**
	 * Plays a Game.  homeID and awayID must be IDs of valid teams already added to the league.  The game being played will be added to activeGame
	 * @param homeID
	 * @param awayID
	 * @param uiContainer The container that will hold the UI for the game.  If null no UI will be shown
	 */
	public void playGame (int homeID, int awayID, Container uiContainer) {
		
		Team homeTeam = teams.get(homeID);
		Team awayTeam = teams.get(awayID);
		
		if (homeTeam == null) {
			throw new IllegalArgumentException ("Home Team not found");
		}
		
		if (awayTeam == null) {
			throw new IllegalArgumentException ("Away Team not found");
		}
		
		Game gameToPlay = new Game (homeTeam, awayTeam, nextGameID, getID(), currentYear);
		
		//add the ui for the game to the panel
		if (uiContainer != null) {
			uiContainer.add(gameToPlay.getGameView());
			uiContainer.setVisible(true);
			gameToPlay.shouldUIBeDrawn(true);
		}
			
		activeGame.put(nextGameID, gameToPlay);
		nextGameID++;
		gameToPlay.playGame();
		gameToPlay.saveGameStats(getID(), currentYear);
		
	}
	
	public void addTeam (int id, LocalFile fileDir) {
		
		//TODO
		
		
	}
	
}
