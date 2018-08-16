package ui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import boxscore.GameStatus;
import boxscore.LinescoreTable;
import game.InningCounters;
import helpers.DebuggerInfo;
import stadium.Stadium;

public class BaseballGameDisplay extends JPanel {
		
	private JTextArea info; 
	private JScrollPane scrollBar;
	
	private LinescoreTable linescore;
	
	private GameStatus ctrDisplay = new GameStatus ();
	
	private DebuggerInfo debugInfo;
	
	public BaseballGameDisplay (FieldEventDisplay event, int gID, int aID, int hID) {
		
		setSize(1200, 900);
		setVisible(true);
		setLayout(new FlowLayout());

		//add the field to the game
		add(event.getFieldImage());
			
		info = new JTextArea();
		info.setVisible(true);
		info.setSize(300, 500);
		info.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		info.setEnabled(false);
		
		scrollBar = new JScrollPane(info);
		scrollBar.setVisible(true);
		scrollBar.setPreferredSize(info.getSize());
		

	
		linescore = new LinescoreTable (gID, aID, hID);
		linescore.setVisible(true);
		linescore.setSize(linescore.getSize());
				
	}
	
	public void showDebugger (boolean val) {
		debugInfo.setVisible(val);
	}
	
	public void writeToDebugger (String [] toAdd) {
		debugInfo.addText(toAdd);
	}
	
	public void writeToDebuggerAndUpdate (String [] toAdd) {
		debugInfo.addText(toAdd);
		debugInfo.update();
	}
	
	public void updateCTR (InningCounters ctr, int hRuns, int aRuns) {
		ctrDisplay.upDate(ctr, hRuns, aRuns);
		ctrDisplay.setText(ctrDisplay.toString());
	}
	
	public void writeText (String text) {
		info.setText(info.getText() + "\n" + text);
			
		//update the text later
		Thread setBar = new Thread () {
			public void run () {
				scrollBar.getVerticalScrollBar().setValue(scrollBar.getVerticalScrollBar().getMaximum());
			}
		};
		
		SwingUtilities.invokeLater(setBar);
		
	}
	
	public LinescoreTable getLinescore () {
		return linescore;
	}
	
}
