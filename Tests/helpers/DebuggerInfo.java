package helpers;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebuggerInfo extends JScrollPane {
	
	private List <String> textToDisplay = new LinkedList <String> ();
	private JTextArea info;
	
	public DebuggerInfo (JTextArea info, int width, int height) {
		super(info);
		this.info = info;
		info.setSize(width,height);
		info.setVisible(true);
		info.disable();
		setPreferredSize(info.getSize());
		setVisible(true);
}
	
	public void addText (String toAdd) {
		textToDisplay.add(toAdd);
	}
	
	public void addText (String [] toAdd) {
		
		for (String cur: toAdd) {
			textToDisplay.add(cur);
		}
		
	}
	
	public void update () {
		
		String disp = "";
		
		for (String curString: textToDisplay) {
			disp += curString + "\n";
		}
		
		info.setText(disp);
		textToDisplay.clear();
		
	}
	
}
