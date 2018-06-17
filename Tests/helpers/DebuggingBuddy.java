package helpers;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

//a DebuggingBuddy waits for the user to advance before notify it's budddy thread
public class DebuggingBuddy {
	
	public static void wait (JFrame frame) {
		JOptionPane.showMessageDialog(frame, "Click the button to continue!");
	}
	
}
