package objects;
import java.util.LinkedList;
import java.util.List;

import datatype.Coordinate3D;
import game.Game;
import messages.ForceOutMsg;
import messages.RunScoredMsg;
import messages.RunnerArrivedAtBaseMsg;
import messages.RunnerOutMsg;



public class Base extends OnFieldObject {
	
	private BaseType base;
	public Fielder fielderOn = null;
	public Baserunner runnerOn = null;
	public boolean forceOut = false;
	public List <Baserunner> runnerTo = new LinkedList <Baserunner> ();
	public Baserunner toBeForced = null;
	
	public Base(Coordinate3D loc, BaseType base, int color) {
		super(loc, loc, color);
		this.base = base;
	}

	public BaseType getBase() {
		return base;
	}
	
	public void arriveAtBase (Baserunner copy) {
		
		clearRunnerTo(copy);
		
		if (copy.isEqual(toBeForced)) {
			forceOut = false;
			toBeForced = null;
		}
		
		runnerOn = copy;
		
	}
	
	private void clearRunnerTo (Baserunner clear) {
		
		for (int i = runnerTo.size()-1; i >= 0; i--) {
		
			if (runnerTo.get(i).isEqual(clear)) {
				runnerTo.remove(i);
				return;
			}
			
		}
		
	}
	
	public void clearRunnerOn () {
		runnerOn = null;
	}
	
	public void clearFielderOn () {
		fielderOn = null;
	}
	
	public void reset () {
		fielderOn = null;
		runnerOn = null;
		forceOut = false;
		runnerTo.clear();
		toBeForced = null;
	}
	
	public void nextAtBat () {
		fielderOn = null;
		forceOut = false;
		runnerTo.clear();
		toBeForced = null;
	}
		
	public Fielder getFielderOn() {
		return fielderOn;
	}

	public void setFielderOn(Fielder fielderOn) {
		this.fielderOn = fielderOn;
	}

	public Baserunner getRunnerOn() {
		return runnerOn;
	}
	
	public boolean isRunnerOn () {
		return runnerOn != null;
	}

	public boolean isSomeoneRunningAtMyBase () {
		return !runnerTo.isEmpty();
	}
	
	public void setRunnerOn(Baserunner runnerOn) {
		this.runnerOn = runnerOn;
	}

	public boolean isForceOut() {
		return forceOut;
	}

	public void setForceOut(boolean forceOut) {
		this.forceOut = forceOut;
	}
	
	public void clearForce (Baserunner runner) {
		forceOut = false;
		toBeForced = null;
		removeRunnerTo(runner);
	}
	
	public void clearForNextAB () {
		fielderOn = null;
		forceOut = false;
		runnerTo.clear();
		toBeForced = null;
	}
	
	public void nowIsForceOut (Baserunner toBeForced) {
		this.toBeForced = toBeForced;
		forceOut = true;
	}
	
	public void clearForce () {
		toBeForced = null;
		forceOut = false;
	}
	
	public void clearForNextInning () {
		clearForNextAB();
		runnerOn = null;
	}
	
	public Baserunner getToBeForced() {
		return toBeForced;
	}

	public void setToBeForced(Baserunner toBeForced) {
		this.toBeForced = toBeForced;
	}

	public List<Baserunner> getRunnerTo() {
		return runnerTo;
	}
	
	public void addRunnerTo (Baserunner to) {
		runnerTo.add(to);
	}
	
	public void removeRunnerTo (Baserunner to) {
		runnerTo.remove(to);
	}

	public void setBase(BaseType base) {
		this.base = base;
	}
	
	public void fielderLeave () {
		fielderOn = null;
	}
	
	public void baserunnerLeave () {
		runnerOn = null;
	}
	
	public boolean isHome () {
		return base.equals(BaseType.HOME);
	}

	@Override
	public String toString() {
		
		String runnerOnName = runnerOn == null ? "No Runner" : runnerOn.getName();
		String fielderOnName = fielderOn == null ? "No Runner" : fielderOn.getName();
		String toBeForcedName = toBeForced == null ? "Empty" : toBeForced.getName();
		
		String runnersToName = "";
		
		for (Baserunner runner: runnerTo) {
			runnersToName += runner.getName() + ",";
		}
		
		return "Base [" + "base=" + base + 
				", fielderOn=" + fielderOnName + 
				", runnerOn=" + runnerOnName + 
				", forceOut=" + forceOut
				+ ", runnerTo=" + runnersToName +
				", toBeForced=" + toBeForcedName + "]";
	}

	@Override
	public int getMarkerSize() {
		// TODO Auto-generated method stub
		return 1;
	}
	
}
