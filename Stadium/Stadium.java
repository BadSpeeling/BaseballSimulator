import java.util.HashMap;
import java.util.Scanner;
import java.math.*;

/* Stadium represents a place that a baseball game is played in.  A stadium can have different
 * dimensions
 * 
 * */

public class Stadium {

	HashMap <String, Integer> dim = new HashMap <String, Integer> ();
	//FieldMatrix field;
	HashMap <String, Coordinate3D> dimCoors = new HashMap <String, Coordinate3D> (); //stores the xy coordinate of the dims
	int max;

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

		dimCoors.put("l", new Coordinate3D(xBegin, yBegin, 0));
		dimCoors.put("lc", new Coordinate3D(xLC, yLC, 0));
		dimCoors.put("c", new Coordinate3D(xC, yC, 0));
		dimCoors.put("rc", new Coordinate3D(xRC, yRC, 0));
		dimCoors.put("r", new Coordinate3D(xR, yR, 0));

	}

}
