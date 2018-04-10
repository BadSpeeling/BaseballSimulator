public class Base extends OnFieldObject {
	
	private BaseType base;
	private Fielder fielderOn = null;
	private Baserunner runnerOn = null;
	private Baserunner runnerAdvancingTo = null;
	private boolean forceOut = false;
	
	public void setRunnerOn(Baserunner runnerOn) {
		this.runnerOn = runnerOn;
	}
	
	public void setAdvanceing (Baserunner runner) {
		this.runnerAdvancingTo = runner;
	}
	
	public Baserunner getAdvancingRunner () {
		return runnerAdvancingTo;
	}
	
	public boolean runnerOn() {
		return runnerOn != null;
	}

	public void setBase(BaseType base) {
		this.base = base;
	}

	public boolean isForceOut() {
		return forceOut;
	}

	public void setForceOut(boolean forceOut) {
		this.forceOut = forceOut;
	}

	public Base(Coordinate3D loc, BaseType base) {
		super(loc, null, null);
		this.base = base;
	}
	
	public void arriveAtBase (Fielder fielderOn) {
		this.fielderOn = fielderOn;
	}
	
	//return true if the base runner reached base safely, false if not
	public boolean arriveAtBase (Baserunner runnerOn) {
		
		if (fielderOn == null) { 
			this.runnerOn = runnerOn;
			return true;
		}
		
		return false;
			
	}
	
	public void clear () {
		fielderOn = null;
	}
	
}
