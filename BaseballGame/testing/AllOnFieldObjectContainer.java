package testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import ball.BallInPlay;
import datatype.Coordinate3D;
import objects.Baserunner;
import objects.Fielder;
import ui.BasicBoard;

public class AllOnFieldObjectContainer {
	
	private final int BALLNUMBER = -10; //special id for a ball in play - since it doesnt have a unique id
	
	private List <List <String>> allObjects = new LinkedList <List <String>> ();
	
	//toWrite contains all data to be on a singleline of ouput, i.e. current game state
	public void writeLine (List <String> toWrite) {
		
		allObjects.add(toWrite);
		
	}
	
	public void writePlayToFile (String folderName) {
		
		File data = new File (folderName + "\\"  + java.lang.System.nanoTime());
		String toWrite = "";
		
		for (List <String> curObj: allObjects) {

			for (String curString: curObj) {
				toWrite += curString + "\n";
			}
			
			toWrite += "\n";
		}
		
		try {
			Writer writer = new PrintWriter (data);
			writer.write(toWrite);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void runTest () {
		new View();
	}
	
}

class View extends JFrame {
	
	private BasicBoard display;
	private List<LinkedList<Coordinate3D>> positions = new LinkedList <LinkedList <Coordinate3D>> ();
	
	public View () {
		
		display = new BasicBoard (500,500,10);
		setSize(1000,1000);
		setVisible(true);
		
		add(display.getDisplay());
		int size = -1;
		
		try {
			
			final JFileChooser fc = new JFileChooser("D:\\Java_Projects\\BaseballSimulator\\temp_files");

			int returnVal = fc.showOpenDialog(this);
			
			File file =  fc.getSelectedFile();
			
			Scanner input = new Scanner (file);
			
			while (input.hasNextLine()) {
				String data = input.nextLine();
				String [] coors = data.split(";");
				
				LinkedList <Coordinate3D> curObj = new LinkedList <Coordinate3D> ();
				
				for (String cur: coors) {
					Coordinate3D curLoc = Coordinate3D.convertFromToString(cur);
					curObj.add(curLoc);
				}
				
				size = positions.size();
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
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			System.out.println("done");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
}
