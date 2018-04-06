//An on field object is anything moving entity apparent on a baseball field
public class OnFieldObject {
	
	Coordinate3D loc;
	Coordinate3D lastLoc;
	
	public OnFieldObject (Coordinate3D loc, Coordinate3D lastLoc) {
		this.loc = loc;
		this.lastLoc = lastLoc;
	}
	
}
