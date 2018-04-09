
public class RunningToBase {
	
	private Baserunner runner;
	private Base runningTo;
	
	public Baserunner getRunner() {
		return runner;
	}
	public void setRunner(Baserunner runner) {
		this.runner = runner;
	}
	
	public Base getRunningTo() {
		return runningTo;
	}
	
	public RunningToBase(Baserunner runner, Base runningTo) {
		super();
		this.runner = runner;
		this.runningTo = runningTo;
	}
	
	public void setRunningTo(Base runningTo) {
		this.runningTo = runningTo;
	}
	
}
