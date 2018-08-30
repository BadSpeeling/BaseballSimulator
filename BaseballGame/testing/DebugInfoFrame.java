package testing;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import objects.Base;

public class DebugInfoFrame extends JFrame {
	
	private JLabel baseInfo;
	
	public DebugInfoFrame (int width, int height) {
		
		setSize(width, height);
		setVisible(true);
		setLayout(new FlowLayout());
		
		this.baseInfo = new JLabel ();
		
	}
	
	public void writeBasesToScreen (Base [] bases) {
		
		String textToShow = "<html>";
		final String br = "<br>";
		
		textToShow += bases[0].toString() + br + bases[1].toString() + br + bases[2].toString() + br + bases[3].toString() + "</html>"; 
		
		writeText(textToShow);
		
	}
	
	public void writeText (String toWrite) {
		baseInfo.setText(toWrite);
		
		add(baseInfo);
		pack();
	}
	
}
