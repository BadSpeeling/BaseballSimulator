package ui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import stats.BattingStatline;

@SuppressWarnings("serial")
public class StatsTable extends JTable {

	private final int rows;
	private final int cols;
	private final int tID; //teamID
	private TableModel model;
	private List <Integer> pIDLocs;
	
	public StatsTable (int rows, int cols, int tID) {
		super(new DefaultTableModel(rows+1,cols));
		this.rows = rows;
		this.cols = cols;
		this.tID = tID;
		model = getModel();
		pIDLocs = new LinkedList <Integer> ();
		pIDLocs.add(-1); //add the first spot as unaccessible
						
		//set headers
		model.setValueAt("AB", 0, 1);
		model.setValueAt("H", 0, 2);
		model.setValueAt("R", 0, 3);
		model.setValueAt("RBI", 0, 4);
		model.setValueAt("BB", 0, 5);
		model.setValueAt("K", 0, 6);
		
	}
	
	//increases the value of a cell by one
	public void incCell (int row, int col) {
		model.setValueAt((int)(model.getValueAt(row, col))+1, row, col);
	}
	
	//adds a player to the id list.  the order inserted in decides display order
	@SuppressWarnings("unused")
	private void addID (int pID) {
		pIDLocs.add(pID);
	}
	
	@SuppressWarnings("unused")
	private void addID (int pID, int row) {
		
		if (row > pIDLocs.size()) {
			pIDLocs.add(pID);
		}
		
		pIDLocs.add(row, pID);
	}
	
	//adds a new row to the display
	public void addBattingRow (int pID, String fName) {
		
		pIDLocs.add(pID);
		int row = whichRow(pID);
		
		model.setValueAt(fName, row, 0);
		
		for (int i = 1; i < cols; i++) {
			model.setValueAt(0, row, i);
		}
		
	}
	
	//returns what row a players stats are being held in
	public int whichRow (int pID) {
		return pIDLocs.indexOf(pID);
	}
	
	public void updateBattingRow (int pID, BattingStatline curStats) {
		
		
	}
	

	
}
