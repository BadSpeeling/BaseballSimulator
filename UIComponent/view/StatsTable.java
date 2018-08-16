package view;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class StatsTable extends JTable {
	
	public StatsTable (String [] colNames, String [][] data) {
		super(data, colNames);
	}
	
	public void setColumnSize (int col, int size) {
		getColumnModel().getColumn(col).setWidth(size);
	}
	
	//returns a component ready to be displayed
	public JScrollPane getScrollView () {
		
		Dimension d = getPreferredSize();

		JScrollPane ret = new JScrollPane (this);
		ret.setPreferredSize(new Dimension(d.width, getRowHeight()*(getRowCount()+1) + 7 ));
		setFillsViewportHeight(true);
		return ret;
		
	}
	
}
