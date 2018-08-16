package boxscore;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class LinescoreTable extends JTable {

	private TableModel model;
	private final int gID;
	private final int awayID;
	private final int homeID;

	//max innings to be played.  can be incrememnted by one when a game goes to an extra frame
	private int maxInnings = 9;

	//used to measure the size of the boxes
	private final int box_width = 20;
	private final int box_height = 12;

	public LinescoreTable (int gID, int awayID, int homeID) {

		super(new DefaultTableModel(3,12));
		this.gID = gID;
		this.awayID = awayID;
		this.homeID = homeID;
		model = getModel();

		final String [] headers = {"1","2","3","4","5","6","7","8","9","R","H","E"};

		for (int i = 0; i < model.getColumnCount(); i++) {
			model.setValueAt(headers[i], 0, i);
		}

		//init values
		for (int i = 0; i < model.getColumnCount(); i++) {
			for (int j = 1; j < model.getRowCount(); j++) {
				model.setValueAt(0, j, i);
			}

			getColumnModel().getColumn(i).setMaxWidth(box_width);

		}

	}

	public int getgID() {
		return gID;
	}

	public int getAwayID() {
		return awayID;
	}

	public int getHomeID() {
		return homeID;
	}

	public Dimension getSize () {
		return new Dimension (box_width*(maxInnings+4),box_height*2);
	}

	public void incHits (boolean top) {

		int row;
			
		if (top) {
			row = 1;
		}

		else {
			row = 2;
		}

		model.setValueAt((int)(model.getValueAt(row, maxInnings+1))+1, row, maxInnings+1);

	}

	//increases the run total
	public void runScored (int inning, boolean top) {

		int row;

		if (top) {
			row = 1;
		}

		else {
			row = 2;
		}
				
		model.setValueAt((int)(model.getValueAt(row, inning-1))+1, row, inning-1);
		model.setValueAt((int)(model.getValueAt(row, maxInnings))+1, row, maxInnings);

		
	}

}
