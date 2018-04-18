import java.util.LinkedList;
import java.util.List;

//An on field object is anything moving entity apparent on a baseball field

public class OnFieldObject {
	
	Coordinate3D loc;
	Coordinate3D lastLoc;
	
	private List <LocationTracker> tracker = new LinkedList <LocationTracker> ();
	
	public OnFieldObject (Coordinate3D loc, Coordinate3D lastLoc, List <LocationTracker> tracker) {
		this.loc = loc;
		this.lastLoc = lastLoc;
		this.tracker = new LinkedList <LocationTracker> ();
	}
	
	public void track (LocationTracker toAdd) {
		tracker.add(toAdd);
	}
	
	public List <LocationTracker> getTracker () {
		return tracker;
	}
	
}
