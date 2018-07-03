package messages;
import objects.Baserunner;
import objects.Fielder;

public class FlyballCaughtMsg extends BaserunnerOutMsg {
	
	public FlyballCaughtMsg(Fielder ballCatcher, Baserunner outed) {
		super(ballCatcher, outed);
	
	}
	
	public String toString () {
		return super.toString() + " The catch was made at " + fielder.getLoc();
	}
	
}
