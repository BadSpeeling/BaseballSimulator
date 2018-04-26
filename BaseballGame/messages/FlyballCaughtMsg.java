package messages;
import main.Baserunner;
import main.Fielder;

public class FlyballCaughtMsg extends BaserunnerOutMsg {
	
	public FlyballCaughtMsg(Fielder ballCatcher, Baserunner outed) {
		super(ballCatcher, outed);
	
	}
	
	public String toString () {
		return super.toString() + " The catch was made at " + fielder.loc;
	}
	
}
