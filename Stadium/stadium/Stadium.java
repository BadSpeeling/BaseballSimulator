package stadium;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import datatype.Coordinate3D;

import java.math.*;

/* Stadium represents a place that a baseball game is played in.  A stadium can have different
 * dimensions
 * 
 * */

public class Stadium {

	public HashMap <String, Integer> dim = new HashMap <String, Integer> ();
	//FieldMatrix field;
	private List <Wall> walls = new LinkedList <Wall> (); //stores the xy coordinate of the dim

	public int max;

	/* Loads the dimensions of the field into dimensions map
	 * inputFile - file that contains dimensions.  key:value
	 * l - left field pole
	 * lc - left center 
	 * c  - center
	 * rc - right center
	 * r - right field pole
	 * f - foul territory size
	 * */

	public void loadDimensions (Scanner inputFile) {

		while (inputFile.hasNextLine()) {
			String [] in = inputFile.nextLine().split(":");
			dim.put(in[0], Integer.parseInt(in[1]));
		}

		int maxSize = Math.max(dim.get("r"), dim.get("l")) + 50;
		max = maxSize;

		double xBegin = 0 ;
		double yBegin = dim.get("l");

		double rLC = dim.get("lc");
		double rC = dim.get("c");
		double rRC = dim.get("rc");

		double offsetRads = 0;

		double lcAngle = 3 * Math.PI / 8 + offsetRads;
		double rcAngle = Math.PI / 8 + offsetRads;
		double cAngle = Math.PI / 4 + offsetRads;

		double xLC = rLC*Math.cos(lcAngle);
		double yLC = rLC*Math.sin(lcAngle);


		double xRC = rRC*Math.cos(rcAngle);
		double yRC = rRC*Math.sin(rcAngle);

		double xC = rC*Math.cos(cAngle);
		double yC = rC*Math.sin(cAngle);

		double xR = dim.get("r");
		double yR = 0;

		walls.add(new Wall(xBegin,yBegin,xLC,yLC,10));
		walls.add(new Wall(xLC,yLC,xC,yC,10));
		walls.add(new Wall(xC,yC,xRC,yRC,10));
		walls.add(new Wall(xRC,yRC,xR,yR,10));

	}
	
	public List<Wall> getWalls() {
		return walls;
	}

}
