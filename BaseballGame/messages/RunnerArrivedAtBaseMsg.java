package messages;

import main.BaseType;
import objects.Baserunner;

public class RunnerArrivedAtBaseMsg extends Message{
	
	public BaseType base;
	public Baserunner runner;
	
	public RunnerArrivedAtBaseMsg(BaseType base, Baserunner runner) {
		super();
		this.base = base;
		this.runner = runner;
	}
	
	public String toString () {
		return runner + " has arrived at " + base;
	}
	
}
