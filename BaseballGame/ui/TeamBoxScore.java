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
import player.Player;
import stats.BattingStatline;
import team.GameTeam;
import view.StatsTable;

public class TeamBoxScore extends StatsTable {

	private ArrayList <Integer> pIDLocations; //the index for a pID in this list corresponds to its location in the StatsTable
 	
	public TeamBoxScore (String [] disp, String [][] data, Player [] order) {
		
		super(disp, data);
		
		pIDLocations = new ArrayList <Integer> ();
		
		for (Player curPlayer: order) {
			pIDLocations.add(curPlayer.getpID());
		}
		
	}
	

}