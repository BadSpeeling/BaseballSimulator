import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

/* Eric Frye
 * Team represents a baseball team.  A baseball team is valid if it follows the roster restrictions designated by a RuleSet.  Players
 * are on a Team.  A Team can take part in a BaseBallGame or be a part of a league. 
 * */

public class Team {

	LinkedList <Player> playersOnTeam = new LinkedList <Player> (); //Set of players on the team
	int tID; //Team ID
	int leagueID; //ID of league that the team is in

	/*
	 * Adds single Player to Team.
	 * toAdd: player to add.
	 * */
	public void addPlayer (Player toAdd, int max) {

		//check that there are not too many players on team
		if (playersOnTeam.size()+1 > max) {
			System.out.println("Cannot add player.");
		}

		else {
			playersOnTeam.add(toAdd);
		}

	}

	/*
	 * Adds multiple Player's to Team.
	 * toAdd: players to add. 
	 * */
	public void addPlayers (LinkedList <Player> toAdd, int max) {

		//check that there are not too many players on team
		if (toAdd.size()+playersOnTeam.size() > max) {
			System.out.println("Cannot add players");
		}

		else { 

			for (int i = 0; i < toAdd.size(); i++) {
				playersOnTeam.add(toAdd.get(i));
			}

		}

	}

	/* 
	 * Adds fake players up to max designated by RuleSet.
	 * */
	public void addFakePlayers (int max) {

		File cwd = new File(System.getProperty("user.dir") + "\\Player\\Names");
		Random r = new Random ();

		int [] positions = {1,2,3,4,5,6,7,8,9};
		Position [] posToAdd = Position.getAllEnums(positions);

		if (max < posToAdd.length) {
			System.out.println("Too many players in fake team generation (programmer error)");
			return;
		}

		Scanner firstNames = null;
		Scanner lastNames = null;
				
		try {
			firstNames = new Scanner (new File (cwd  + "\\firstNames"));
			lastNames = new Scanner (new File (cwd + "\\lastNames"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String [] allFirstNames = firstNames.nextLine().split(",");
		String [] allLastNames = lastNames.nextLine().split(",");

		for (int i = 0; i < posToAdd.length; i++) {

			Player toAdd = new Player (allFirstNames[r.nextInt(allFirstNames.length)], allLastNames[r.nextInt(allLastNames.length)], posToAdd[i]);
			playersOnTeam.add(toAdd);

		}

		firstNames.close();
		lastNames.close();

	}
	
	/*
	 * Print all the players on the team.
	 * */
	public void printTeam () {
		
		Iterator <Player> cur = playersOnTeam.iterator();
		
		while (cur.hasNext()) {
			System.out.println(cur.next());
		}
		
	}
	
	/*
	 * Creates an InGameTeam.  Currently using a basic method - no unique lineup, no bullpen and no bench.
	 * */
	public GameTeam makeInGameTeam (boolean homeTeam) {
		
		CircularLinkedList <GamePlayer> lineup = new CircularLinkedList <GamePlayer> (9);
		
		GamePlayer pitcher = null;
		
		LinkedList <GamePlayer> fielders = new LinkedList <GamePlayer> ();
		
		for (Player cur: playersOnTeam) {
						
			//if player is a pitcher
			if ((cur.pos.ordinal()+1) == 1) {
				pitcher = new GamePlayer(cur.firstName, cur.lastName, cur.pos);
			}
			
			GamePlayer toAdd = new GamePlayer(cur.firstName, cur.lastName, cur.pos);
			
			lineup.add(toAdd);
			fielders.add(toAdd);
			
		}
		
		return new GameTeam(lineup, pitcher, null, null, null, homeTeam, fielders);
		
	}

}
