package objects;
/* Eric Frye
 * InGameTeam is a team that is taking part in a Game.
 * */

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import datatype.CircularLinkedList;
import game.Game;
import game.Linescore;
import manager.Manager;
import player.Player;

public class GameTeam {

	private int curPA = 0;
	
	private List <Player> allPlayersOnTeam;
	
	private Player [] lineup; //Lineup.  Any player that enters the game must be placed in this list
	private List <Fielder> inTheField;
	private Player pitcher; //Current player on the mound.
	private HashSet <Player> bench; //Available players on bench.  This variable is a set because there is no ordering of the players.
	private HashSet <Player> bullPen; //Available players in the bullpen.  No specific ordering.
	
	private Manager manager; //Manager.
	private Linescore score;
	
	private int tID;
	
	private final int PLAYERSININITLINEUP = 9;
	private final int STARTINGPITCHERCOUNT = 1;
	
	public GameTeam (int id,Player [] lineup, Player pitcher, HashSet <Player> bench, HashSet <Player> bullPen, Manager manager, boolean homeTeam, List <Fielder> inField) {
		this.lineup = lineup;
		this.pitcher = pitcher;
		this.bench = bench;
		this.bullPen = bullPen;
		this.manager = manager;
		this.score = new Linescore (homeTeam);
		this.inTheField = inField;
		this.tID = id;
		
		allPlayersOnTeam = new LinkedList <Player> ();
		
		for (Player cur: lineup) {
			allPlayersOnTeam.add(cur);
		}
		
		if (bench != null) {
			allPlayersOnTeam.addAll(bench);
		}
		
		if (bullPen != null) {
			allPlayersOnTeam.addAll(bullPen);
		}
		
		allPlayersOnTeam.add(pitcher);
		
	}

	public GameTeam (GameTeam copy) {

		lineup = copy.lineup;
		pitcher = copy.pitcher;
		bench = copy.bench;
		bullPen = copy.bullPen;
		manager = copy.manager;
		score = copy.score;
		tID = copy.tID;

	}

	public int getID () {
		return tID;
	}

	public List <Fielder> getFielders () {
		return inTheField;
	}

	//returns the player that will throw the next pitch
	public Player getCurrentPitcher () {
		return pitcher;
	}

	//returns the next player due up in the batting order
	public Player nextBatter () {
		return lineup[curPA++%9];
	}

	public Player [] getLineup () {
		return lineup;
	}

	//returns a model for the initial box score for this team, to be added to a stats table
	public String [][] initBattingBoxScore () {
		
		String [][] ret = new String [PLAYERSININITLINEUP][Game.battingStatsDisplayed.length];
		
		for (int i = 0; i < ret.length; i++) {
			
			Player curPlayer = lineup[i];
			String [] playerBox = curPlayer.generateGameBattingStatsDisp();
			
			for (int j = 0; j < playerBox.length; j++) {
				ret[i][j] = playerBox[j];
			}
			
		}
		
		return ret;
		
	}
	
	//returns a model for the intial pitching box score for this team, to be added to a stats table
	public String [][] initPitchingBoxScore () {
		
		String [][] ret = new String [STARTINGPITCHERCOUNT][Game.pitchingStatsDisplayed.length];
		
		for (int i = 0; i < ret.length; i++) {
			
			Player curPlayer = lineup[i];
			String [] playerBox = curPlayer.generateGamePitchingStatsDisp();
			
			for (int j = 0; j < playerBox.length; j++) {
				ret[i][j] = playerBox[j];
			}
			
		}
		
		return ret;
		
	}
	
	public Player getPlayer (int pID) {
		
		//check players in the lineup first for id
		for (Player curPlayer: lineup) {
			
			if (curPlayer.isEqual(pID)) {
				return curPlayer;
			}
			
		}
		
		return null;
		
	}
	
	public String getBattingBoxScore () {
		
		String toRet = "";
			
		for (Player cur: allPlayersOnTeam) {
				
			String [] curStats = cur.generateCurGameBattingStatsDisp();

			for (String curStr: curStats) {
				toRet += curStr + " ";
			}
				
			toRet += "\n";
				
		}
		
		return toRet;
			
	}
	
	public String getPitchingBoxScore () {
		
		String toRet = "";
		
		for (Player cur: allPlayersOnTeam) {
			
			String [] curStats = cur.generateCurPitchingBattingStatsDisp();

			for (String curStr: curStats) {
				toRet += curStr + " ";
			}
			
			toRet += "\n";
			
		}
		
		return toRet;
		
	}
			
}
