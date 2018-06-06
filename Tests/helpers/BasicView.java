package helpers;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;

import datatype.Coordinate3D;
import ui.BasicBoard;

class BasicView extends JFrame {
	
	private BasicBoard display;
	private List<LinkedList<Coordinate3D>> positions = new LinkedList <LinkedList <Coordinate3D>> ();
	
	public BasicView () {
		
		display = new BasicBoard (500,500,10);
		setSize(1000,1000);
		setVisible(true);
		
		add(display.getDisplay());
		
		try {
			Scanner input = new Scanner (new File("D:\\Java_Projects\\BaseballSimulator\\temp_files\\254999132952314"));
			
			while (input.hasNextLine()) {
				String data = input.nextLine();
				String [] coors = data.split(";");
				
				LinkedList <Coordinate3D> curObj = new LinkedList <Coordinate3D> ();
				
				for (String cur: coors) {
					Coordinate3D curLoc = Coordinate3D.convertFromToString(cur);
					curObj.add(curLoc);
				}
				
				positions.add(curObj);
				
			}
			
			input.close();
			
			LinkedList <Coordinate3D> prev = null;
			
			for (LinkedList <Coordinate3D> curLocs: positions) {
				
				if (prev != null) {
					
					
					for (Coordinate3D prevLoc: prev) {
						display.clearObject(prevLoc, 1);
					}
					
					
				}
				
				for (Coordinate3D curLoc: curLocs) {
					display.drawObject(curLoc, 0xFF00FF, 1);
				}
				
				prev = curLocs;
				display.redraw();
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}