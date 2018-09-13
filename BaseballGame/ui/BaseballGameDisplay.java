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
import objects.Base;
import stadium.Stadium;
import testing.DebugInfoFrame;

public class BaseballGameDisplay extends JPanel {
		

	
	public BaseballGameDisplay (FieldEventDisplay event, int gID, int aID, int hID) {
		
		setSize(1200, 900);
		setVisible(true);
		setLayout(new FlowLayout());

		//add the field to the game
		add(event.getFieldImage());
			

				
	}

}
