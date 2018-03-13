import java.util.HashMap;
import java.util.Scanner;
import java.math.*;

/* Stadium represents a place that a baseball game is played in.  A stadium can have different
 * dimensions
 * 
 * */

public class Stadium {

	HashMap <String, Integer> dim = new HashMap <String, Integer> ();
	FieldMatrix field;
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
		int foulSize = dim.get("f");
		max = maxSize;
		field = new FieldMatrix (maxSize, foulSize);
		createOutfieldWalls(foulSize);
		createFoulTerritory();
		//field.print();
		
	}
	
	//adds the foul territory.  foul territory is any negative value
	private void createFoulTerritory () {
		
		for (int i = -1; i < Math.max(dim.get("r"), dim.get("l")) + 10; i++) {
			
				field.set(i, -1, SectorT.FOUL);
				field.set(-1, i, SectorT.FOUL);
			
		}
		
	}
	
	//draws the outfield walls
	private void createOutfieldWalls (int foulSize) {
		
		double xBegin = 0 + foulSize;
		double yBegin = dim.get("l") + foulSize;

		double rLC = dim.get("lc");
		double rC = dim.get("c");
		double rRC = dim.get("rc");

		double offsetRads = 0;

		double lcAngle = 3 * Math.PI / 8 + offsetRads;
		double rcAngle = Math.PI / 8 + offsetRads;
		double cAngle = Math.PI / 4 + offsetRads;

		double xLC = rLC*Math.cos(lcAngle) + foulSize;
		double yLC = rLC*Math.sin(lcAngle) + foulSize;

		//System.out.println(xLC + " " + yLC);

		double xRC = rRC*Math.cos(rcAngle) + foulSize;
		double yRC = rRC*Math.sin(rcAngle) + foulSize;

		//System.out.println(xRC + " " + yRC);

		double xC = rC*Math.cos(cAngle) + foulSize;
		double yC = rC*Math.sin(cAngle) + foulSize;

		double xR = dim.get("r") + foulSize;
		double yR = 0 + foulSize;
		
		dimCoors.put("l", new Coordinate3D(xBegin, yBegin, 0));
		dimCoors.put("lc", new Coordinate3D(xLC, yLC, 0));
		dimCoors.put("c", new Coordinate3D(xC, yC, 0));
		dimCoors.put("rc", new Coordinate3D(xRC, yRC, 0));
		dimCoors.put("r", new Coordinate3D(xR, yR, 0));
		
		
		double firstSlope = calculateSlope(xBegin, yBegin, xLC, yLC);
		Tuple <Double, Double> ret = moveToNextCorner (firstSlope, xBegin, yBegin, xLC, yLC, true);
		
		xBegin = ret.x;
		yBegin = ret.y;
		double secondSlope = calculateSlope(xBegin, yBegin, xC, yC);
		ret = moveToNextCorner (secondSlope, xBegin, yBegin, xC, yC, true);
		
		xBegin = ret.x;
		yBegin = ret.y;
		double thirdSlope = calculateSlope(xBegin, yBegin, xRC, yRC);
		ret = moveToNextCorner (thirdSlope, xBegin, yBegin, xRC, yRC, true);
				
		xBegin = ret.x;
		yBegin = ret.y;
		double fourthSlope = calculateSlope(xBegin, yBegin, xR, yR);
		ret = moveToNextCorner (fourthSlope, xBegin, yBegin, xR, yR, false);
		
					
	}

	//calculates slope
	private double calculateSlope (double x1, double y1, double x2, double y2) {
		/*System.out.println(y2);
		System.out.println(x2);
		System.out.println(y1);
		System.out.println(x1);
		System.out.println();*/
		return (y2-y1)/(x2-x1);
	}

	/* Draws the walls of the field.
	 * slope - the slope from the beginning and end of wall
	 * xBegin - beginning x coordinate
	 * yBegin - beginning y coordinate
	 * xEnd - ending x coordinate
	 * yEnd - ending y coordinate
	 * returns - the x and y coordinate the drawing ended at
	 * */
	private Tuple<Double, Double> moveToNextCorner (double slope, double xBegin, double yBegin, double xEnd, double yEnd, boolean leftRight) {

		double curX = xBegin;
		double curY = yBegin;
		
		/*
		System.out.println(xBegin + " " + yBegin);
		System.out.println(xEnd + " " + yEnd);
		System.out.println(slope);
		System.out.println();
		*/
		
		if (leftRight) {

			while (curX < xEnd) {

				//if slope is greater than 1 then we must fill in the extra spaces that would be skipped
				if (Math.abs(slope) > 1) {

					double val = curY;
										
					for (val = curY; val > curY+slope; val--) {
						if (val <= field.field.length)	
							field.set((int)curX, (int)val, SectorT.HR);
					}
					
					curX += 1;
					curY = val;

				}

				else {
					field.set((int)curX, (int)curY, SectorT.HR);
					curX += 1;
					curY += slope;
				}

			}

		}

		//special case of having to move from right to left
		else {

			while (curX > xEnd) {

				//if slope is greater than 1 then we must fill in the extra spaces that would be skipped
				if (Math.abs(slope) > 1) {

					double val = curY;

					for (val = curY; val > curY-slope; val--) {
						if (val >= 0)
							field.set((int)curX, (int)val, SectorT.HR);
					}

					curX -= 1;
					curY = val;

				}

				else {
					field.set((int)curX, (int)curY, SectorT.HR);
					curX -= 1;
					curY -= slope;
				}

			}

		}

		return new Tuple <Double, Double> (curX, curY);

	}	

	class Tuple <X, Y> {
		X x;
		Y y;

		public Tuple (X a, Y b) {
			x=a;
			y=b;
		}

	}

}
