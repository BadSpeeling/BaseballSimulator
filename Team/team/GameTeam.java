package team;
/* Eric Frye
 * InGameTeam is a team that is taking part in a Game.
 * */

import java.util.HashSet;
import java.util.LinkedList;

import datatype.CircularLinkedList;
import game.Linescore;
import manager.Manager;
import player.Player;

public class GameTeam {
	
	public CircularLinkedList <Player> lineup; //Lineup. This variable is a CLL because a lineup loops back up to the first spot once it reaches the end.
	public LinkedList <Player> inTheField;
	public Player pitcher; //Current player on the mound.
	public HashSet <Player> bench; //Available players on bench.  This variable is a set because there is no ordering of the players.
	public HashSet <Player> bullPen; //Available players in the bullpen.  No specific ordering.
	public Manager manager; //Manager.
	public Linescore score;
	
	public GameTeam (CircularLinkedList <Player> lineup, Player pitcher, HashSet <Player> bench, HashSet <Player> bullPen, Manager manager, boolean homeTeam, LinkedList <Player> inField) {
		this.lineup = lineup;
		this.pitcher = pitcher;
		this.bench = bench;
		this.bullPen = bullPen;
		this.manager = manager;
		this.score = new Linescore (homeTeam);
		this.inTheField = inField;
	}
	
	public GameTeam (GameTeam copy) {
		
		lineup = copy.lineup;
		pitcher = copy.pitcher;
		bench = copy.bench;
		bullPen = copy.bullPen;
		manager = copy.manager;
		score = copy.score;
		
	}

}
