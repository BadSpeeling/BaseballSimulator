
public class RunningToBase extends Message {
	
	private Baserunner runner;
	private BaseType runningTo;
	
	public Baserunner getRunner() {
		return runner;
	}
	public void setRunner(Baserunner runner) {
		this.runner = runner;
	}
	
	public BaseType getRunningTo() {
		return runningTo;
	}
	
	public RunningToBase(Baserunner runner, BaseType runningTo) {
		super();
		this.runner = runner;
		this.runningTo = runningTo;
	}
	
	public void setRunningTo(BaseType runningTo) {
		this.runningTo = runningTo;
	}
	
}
