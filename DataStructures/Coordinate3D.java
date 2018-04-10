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
