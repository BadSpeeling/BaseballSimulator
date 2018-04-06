import java.util.LinkedList;
import java.util.Queue;

public class OnFieldPlayer extends OnFieldObject{

	GeneralRatings gRats;
	String fName;
	Queue <Message> messages = new LinkedList <Message> ();
	
	public OnFieldPlayer(Coordinate3D loc, GeneralRatings gRats, String fName) {
		super(loc, loc.copy());
		this.gRats = gRats;
		this.fName = fName;
	}
	
	
	
}
