package team;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

import datatype.CircularLinkedList;
import objects.Fielder;
import objects.GamePlayer;
import objects.GameTeam;
import player.Position;

/* Eric Frye
 * Team represents a baseball team.  A baseball team is valid if it follows the roster restrictions designated by a RuleSet.  Players
 * are on a Team.  A Team can take part in a BaseBallGame or be a part of a league. 
 * */

public class Team {
	
	private static int nextID = 1;
	
	public LinkedList <GamePlayer> playersOnTeam = new LinkedList <GamePlayer> (); //Set of players on the team
	public int tID; //Team ID
	public int leagueID; //ID of league that the team is in

	/*
	 * Adds single Player to Team.
	 * toAdd: player to add.
	 * */
	public void addPlayer (GamePlayer toAdd, int max) {

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
	public void addPlayers (LinkedList <GamePlayer> toAdd, int max) {

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
	public void addFakePlayers () {

		File cwd = new File(System.getProperty("user.dir") + "\\Player\\Names");
		Random r = new Random ();

		int [] positions = {1,2,3,4,5,6,7,8,9};
		Position [] posToAdd = Position.getAllEnums(positions);
		
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

			GamePlayer toAdd = new GamePlayer (posToAdd[i], allFirstNames[r.nextInt(allFirstNames.length)], allLastNames[r.nextInt(allLastNames.length)], nextID++);
			toAdd.generateSimpleStats();
			playersOnTeam.add(toAdd);

		}

		firstNames.close();
		lastNames.close();

	}
	
	/*
	 * Print all the players on the team.
	 * */
	public void printTeam () {
		
		Iterator <GamePlayer> cur = playersOnTeam.iterator();
		
		while (cur.hasNext()) {
			System.out.println(cur.next());
		}
		
	}
	
	/*
	 * Creates an InGameTeam.  Currently using a basic method - no unique lineup, no bullpen and no bench.
	 * */
	public GameTeam makeInGameTeam (boolean homeTeam) {
		
		GamePlayer [] lineup = new GamePlayer [9]; 
		
		GamePlayer pitcher = null;
		
		List <Fielder> fielders = new LinkedList <Fielder> ();
		
		for (int i = 0; i < 9; i++) {
						
			GamePlayer cur = playersOnTeam.get(i);
			
			//if player is a pitcher
			if (cur.isPitcher()) {
				pitcher = cur;
			}
			
			GamePlayer toAdd = cur;
			
			fielders.add(new Fielder(toAdd,0xFFFFFF));
			lineup[i] = cur;
			
		}
		
		return new GameTeam(tID,lineup, pitcher, null, null, null, homeTeam, fielders);
		
	}

}
