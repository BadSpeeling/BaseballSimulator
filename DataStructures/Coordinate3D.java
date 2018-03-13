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
	
	public void add (double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public void multByFactor (double factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}
	
	public String toString () {
		return "[" + x + "," + y + "," + z + "]";
	}
	
}
