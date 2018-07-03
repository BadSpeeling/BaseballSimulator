package messages;
import objects.Baserunner;
import objects.Fielder;

public class ForceOutMsg extends Message {
	
	public Fielder fielder;
	public Baserunner outAt;
	
	public ForceOutMsg(Fielder fielder, Baserunner outAt) {
		this.fielder = fielder;
		this.outAt = outAt;
	}
	
	public String toString () {
		return "The force was recorded at " + outAt.attempt + " by " + fielder;
	}

}
