package testing;

import java.util.ArrayList;
import java.util.List;
import datatype.Coordinate3D;

public class OnFieldObjectTracker {
	
	private List <Coordinate3D> locations; //hold all locations for this object
	
	public OnFieldObjectTracker () {
		locations = new <Coordinate3D> ArrayList ();
	}
	
	public void addLocation (Coordinate3D toAdd) {
		
		if (toAdd != null) {
			locations.add(toAdd.copy());
		}
		
		else {
			locations.add(new Coordinate3D (0,0,0));
		}
		
	}
	
	public Coordinate3D getIndex (int i) {
		
		if (locations.size() < i) {
			return null;
		}
		
		return locations.get(i);
	}
	
	public String toString () {
		
		String ret = "";
		
		for (Coordinate3D cur: locations) {
			ret += cur.toStringPretty() + ";";
		}
		
		return ret.substring(0, ret.length()-1);
		
	}
	
}
