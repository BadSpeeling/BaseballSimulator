import java.util.LinkedList;
import java.util.List;

//3 tuple of components in a 3d space

public class Coordinate3D {
	double x;
	double y;
	double z;
	
	public Coordinate3D (double x, double y, double z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	//computs the differnce between the two coors
	public Coordinate3D diff (Coordinate3D other) {
		return new Coordinate3D(this.x-other.x, this.y-other.y, this.z-other.z);
	}
	
	/*
	 * returns a list of the bases in ascending order
	 */
	public static List <Coordinate3D> basesInOrder () {
		
		List <Coordinate3D> ret = new LinkedList <Coordinate3D> ();
		ret.add(FieldConstants.homePlate());
		ret.add(FieldConstants.firstBase());
		ret.add(FieldConstants.secondBase());
		ret.add(FieldConstants.thirdBase());
		return ret;
		
	}
	
	/*
	 * returns the closest distacne from a list of locations
	 * */
	public Coordinate3D closest (List <Coordinate3D> lst) {
		
		Coordinate3D ret = null;
		double dist = Double.MAX_VALUE;
		
		for (Coordinate3D cur: lst) {
			
			double curDist = Physics.groundDistanceBetween(cur, this);
			
			if (curDist < dist) {
				dist = curDist;
				ret = cur;
			}
			
		}
		
		return ret;
		
	}
	
	public double mag () {
		return Physics.calculateHorizontalDist(this);
	}
	
	public Coordinate3D copy () {
		return new Coordinate3D (x,y,z);
	}
	
	public void add (double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public boolean within (Coordinate3D other, double epsilon) {
		return this.diff(other).mag() < epsilon;
	}
	
	public boolean equals (Coordinate3D other) {
		return x == other.x && y == other.y && z == other.z; 
	}
	
	public void multByFactor (double factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}
	
	public String toString () {
		return "[" + x + "," + y + "," + z + "]";
	}
	
	//gives the location of a position
	public static Coordinate3D standardPos (Position cur) {
		
		switch (cur) {
			case CATCHER:
				return FieldConstants.stdCatcher();
			case FIRST:
				return FieldConstants.stdFirst();
			case SECOND:
				return FieldConstants.stdSecond();
			case THIRD:
				return FieldConstants.stdThird();
			case SHORT:
				return FieldConstants.stdShort();
			case LEFT:
				return FieldConstants.stdLeft();
			case CENTER:
				return FieldConstants.stdCenter();
			case RIGHT:
				return FieldConstants.stdRight();
			case PITCHER:
				return FieldConstants.stdPitcher();
			default:
				return null;
		}
		
	}
	
}
