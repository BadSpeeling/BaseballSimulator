package messages;
import objects.Base;
import objects.Fielder;

public class ForceOutMsg extends Message {
	
	public Fielder fielder;
	public Base outAt;
	
	public ForceOutMsg(Fielder fielder, Base outAt) {
		this.fielder = fielder;
		this.outAt = outAt;
	}
	
	public String toString () {
		return "The force was recorded at " + outAt.getBase() + " by " + fielder;
	}

}
