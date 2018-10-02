package ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import game.Game;
import objects.GameTeam;
import player.Player;
import stats.BattingStatline;
import view.StatsTable;

public class TeamBoxScore extends StatsTable {

	private ArrayList <Integer> idLocations; //the index for a pID in this list corresponds to its location in the StatsTable
	
	public TeamBoxScore (String [] disp, String [][] data, Player [] order) {
		
		super(disp, data);
		
		idLocations = new ArrayList <Integer> ();
		
		for (Player curPlayer: order) {
			idLocations.add(curPlayer.getpID());
		}
		
	}
	
	public void updateBox (int curPlayerID, String [] stats, int colOffset) {
		
		int index = idLocations.indexOf(curPlayerID);
		
		//dont try to update for a player that is not in this box
		if (index != -1) {
			
			for (int i = colOffset; i < stats.length+colOffset; i++) {
				updateLoc(index, i, stats[i-colOffset]);
			}
			
		}
		
	}
	

}