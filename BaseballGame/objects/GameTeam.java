package objects;
/* Eric Frye
 * InGameTeam is a team that is taking part in a Game.
 * */

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import datatype.CircularLinkedList;
import game.Game;
import game.Linescore;
import manager.Manager;
import player.Player;
import stats.BattingStatline;
import stats.PitchingStatline;
import stats.PlateAppearance;
import team.Team;

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
	
	private HashMap <Integer, BattingStatline> battingStats;
	private HashMap <Integer, PitchingStatline> pitchingStats;
	
	private int tID;

	
	private final int PLAYERSININITLINEUP = 9;
	private final int STARTINGPITCHERCOUNT = 1; 
	
	public GameTeam (int id, Player [] lineup, Player pitcher, HashSet <Player> bench, HashSet <Player> bullPen, Manager manager, boolean homeTeam, List <Fielder> inField) {
		
		this.lineup = lineup;
		this.pitcher = pitcher;
		this.bench = bench;
		this.bullPen = bullPen;
		this.manager = manager;
		this.score = new Linescore (homeTeam);
		this.inTheField = inField;
		this.tID = id;
		this.battingStats = new HashMap <Integer, BattingStatline> ();
		this.pitchingStats = new HashMap <Integer, PitchingStatline> ();
		
		allPlayersOnTeam = new LinkedList <Player> ();
		
		for (Player cur: lineup) {
			allPlayersOnTeam.add(cur);
			battingStats.put(cur.getpID(), new BattingStatline(true));
		}
		
		if (bench != null) {
			allPlayersOnTeam.addAll(bench);
		}
		
		if (bullPen != null) {
			allPlayersOnTeam.addAll(bullPen);
		}
		
		allPlayersOnTeam.add(pitcher);
		pitchingStats.put(pitcher.getpID(), new PitchingStatline(true));
		
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

	
	public void updateBattersLine (int id, PlateAppearance pa) {
		battingStats.get(id).addPA(pa);	
	}
	
	public void updatePitchersLine (int id, PlateAppearance pa, int outs, int runs) {
		pitchingStats.get(id).addPA(pa, outs, runs);
	}
	
	public void updateRunnersRuns (int id) {
		battingStats.get(id).incRuns();
	}
	
	public void updateBattersRBI (int id, int by) {
		battingStats.get(id).incRbi(by);
	}
	
	public String [] generateCurBattingLineFor (int id) {
		
		BattingStatline curGameBatting = battingStats.get(id);
		String [] ret = {curGameBatting.getAB()+"",curGameBatting.getHits()+"",curGameBatting.getRuns()+"",curGameBatting.getRbi()+"",curGameBatting.getStrikeouts()+"",curGameBatting.getWalks()+""};
		return ret;
		
	}
	
	public String [] generateCurPitchingLineFor (int id) {
		
		PitchingStatline curGamePitching = pitchingStats.get(id);
		String [] ret = {curGamePitching.getOutsRec()+"",curGamePitching.getHits()+"",curGamePitching.getEra()+"",curGamePitching.getWalks()+"",curGamePitching.getStrikeouts()+"",curGamePitching.getHomeruns()+""};
		return ret;
		
	}
	
	public Set <Integer> getBattingsStatsKeys () {
		return battingStats.keySet();
	}
	
	public Set <Integer> getPitchingStatsKeys () {
		return pitchingStats.keySet();
	}
	
	public BattingStatline getGameBattingStatsFor (int id) {
		return battingStats.get(id);
	}
	
	public PitchingStatline getGamePitchingStatsFor (int id) {
		return pitchingStats.get(id);
	}
	
	//returns a String that represents the data that is to be saved for this players batting stat
	public String generateBattingLineToSaveFor (int id, int yearIn, int leagueID) {
		
		BattingStatline line = battingStats.get(id);
		return id + "," + yearIn
				+ "," + tID 
				+ ","  + leagueID 
				+ "," + line.getPA() 
				+ "," + line.getAB() 
				+ "," + line.getHits() 
				+ "," + line.getDoubles() 
				+ "," + line.getTriples()
				+ "," + line.getHomeruns() 
				+ "," + line.getWalks() 
				+ "," + line.getStrikeouts() 
				+ "," + line.getRbi() 
				+ "," + line.getRuns();
	}
	
	public String generatePitchingLineToSaveFor (int id, int yearIn, int leagueID) {
		
		PitchingStatline line = pitchingStats.get(id);
		return id + "," + yearIn
				+ "," + tID 
				+ ","  + leagueID 
				+ "," + line.getOutsRec() 
				+ "," + line.getHits() 
				+ "," + line.getDoubles() 
				+ "," + line.getTriples() 
				+ "," + line.getHomeruns()
				+ "," + line.getWalks() 
				+ "," + line.getStrikeouts() 
				+ "," + line.getEra();
		
	}
	
	/**
	 * Adds the BattingStatline generated by this GameTeam during the Game
	 * @param backingTeam The team that we will be adding the game stats to
	 */
	public void sendBattingStatsToTeam (Team backingTeam) {
		
		for (Integer curID: battingStats.keySet()) {
			
			BattingStatline curBattingLine = battingStats.get(curID);
			backingTeam.addPlayerBattingStats(curID, curBattingLine);
			
		}
		
	}
	
}
