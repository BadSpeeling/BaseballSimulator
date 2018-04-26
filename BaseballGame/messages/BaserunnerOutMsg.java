package messages;
import main.Baserunner;
import main.Fielder;

public class BaserunnerOutMsg extends Message {
	
	public Fielder fielder;
	public Baserunner runner;
	
	public BaserunnerOutMsg(Fielder fielder, Baserunner runner) {
		super();
		this.fielder = fielder;
		this.runner = runner;
	}
	
	public String toString () {
		return runner + " is out. The out was recorded by " + fielder;
	} 
	
}
