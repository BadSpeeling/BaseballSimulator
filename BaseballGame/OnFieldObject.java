//An on field object is anything moving entity apparent on a baseball field
public class OnFieldObject {
	
	Coordinate3D loc;
	
	public OnFieldObject (double x, double y, double z) {
		loc = new Coordinate3D (x,y,z);
	}
	
}
