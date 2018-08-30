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
	
	//returns a component ready to be displayed w/ scroll bar
	public JScrollPane getScrollView () {
		
		Dimension d = getPreferredSize();

		JScrollPane ret = new JScrollPane (this);
		ret.setPreferredSize(new Dimension(d.width, getRowHeight()*(getRowCount()+1) + 7 ));
		setFillsViewportHeight(true);
		return ret;
		
	}
	
	//updates the model behind the jtable
	public void update (int row, String [] vals) {
		
		for (int col = 0; col < getColumnCount(); col++) {
			setValueAt(vals[col], row, col);
		}
		
	}
	
}
