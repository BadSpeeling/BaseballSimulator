import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class OnFieldPlayer extends OnFieldObject{

	GeneralRatings gRats;
	String fName;
	static Queue <Message> messages = new LinkedList <Message> ();
	
	private List <LocationTracker> tracker = new LinkedList <LocationTracker> ();
	
	public OnFieldPlayer(Coordinate3D loc, GeneralRatings gRats, String fName, List <LocationTracker> tracker) {
		super(loc, loc.copy(), tracker);
		this.gRats = gRats;
		this.fName = fName;
	}
	
	
}
