package messages;
import main.Base;
import main.Baserunner;
import main.Fielder;

public class RunnerOutMsg extends BaserunnerOutMsg {
	
	public Base runningTo; //base the player was running to

	public RunnerOutMsg(Base runningTo, Baserunner runner, Fielder recOut) {
		super(recOut,runner);
		this.runningTo = runningTo;
	}
	
	public String toString () {
		return super.toString() + " at " + runningTo.getBase();
	}
	
}
