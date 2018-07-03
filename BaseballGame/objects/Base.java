package objects;
import java.util.List;

import datatype.Coordinate3D;
import game.Game;
import messages.ForceOutMsg;
import messages.RunScoredMsg;
import messages.RunnerArrivedAtBaseMsg;
import messages.RunnerOutMsg;

public class Base extends OnFieldObject {
	
	private BaseType base;
	private Fielder fielderOn = null;
	private Baserunner runnerOn = null;
	private boolean forceOut = false;
	private Baserunner runnerTo = null;
	
	public Base(Coordinate3D loc, BaseType base, int color) {
		super(loc, loc, color);
		this.base = base;
	}

	public BaseType getBase() {
		return base;
	}
	
	public void clearRunnerTo () {
		runnerTo = null;
	}

	public Baserunner getRunnerTo() {
		return runnerTo;
	}

	public void setRunnerTo(Baserunner runnerTo) {
		this.runnerTo = runnerTo;
	}

	public void setBase(BaseType base) {
		this.base = base;
	}

	public Fielder getFielderOn() {
		return fielderOn;
	}
	
	public void leaveBase (Fielder blank) {
		fielderOn = null;
	}
	
	public void clearBase () {
		fielderOn = null;
		forceOut = false;
		runnerTo = null;
		runnerOn = null;
	}
	
	public int getMarkerSize () {
		return 1;
	}
	
	public void leaveBase (Baserunner blank) {
		runnerOn = null;
	}
	
	public void clearForNextAB () {
		fielderOn = null;	
		runnerTo = null;
		forceOut = false;
	}
	
	public void arriveAtBase (Fielder arriving) {
		fielderOn = arriving;
				
		if (arriving.hasBall() && runnerOn == null && forceOut) {
			forceOut = false;
			Game.messages.add(new ForceOutMsg(arriving,runnerTo));
		}
		
	}
	
	//baserunner reaching the base.  sends message if the baserunner is out
	//returns false if the player should determine if they should take another base
	//forceOut is flipped since anytime a runner reaches a base safely a forceout cannot occur any longer
	public boolean arriveAtBase (Baserunner arriving) {
		
		//fielder 
		if (fielderOn != null && fielderOn.hasBall()) {
			Game.messages.add(new RunnerOutMsg(this,arriving,fielderOn));
			return false;
		}
		
		else {
			
			forceOut = false;
			
			arriving.setBestBaseAchieved(Math.max(arriving.getBestBaseAchieved(), this.base.num()));
			
			Game.messages.add(new RunnerArrivedAtBaseMsg(base,arriving));
			
			if (base.equals(BaseType.HOME) && arriving.attempt != null) {
				Game.messages.add(new RunScoredMsg(arriving));
				return false;
			}
			
			else {
				runnerOn = arriving; 
			}
			
			return true;
			
		}
 		
	}
	
	public boolean runnerOn () {
		return runnerOn != null;
	}

	public Baserunner getRunnerOn() {
		return runnerOn;
	}

	public boolean isForceOut() {
		return forceOut;
	}

	public void setForceOut(boolean forceOut) {
		this.forceOut = forceOut;
	}

	@Override
	public String toString() {
		
		String runnerOnName = runnerOn == null ? "No Runner" : runnerOn.getName();
		String fielderOnName = fielderOn == null ? "No Runner" : fielderOn.getName();
		String runnerToName = runnerTo == null ? "No Runner" : runnerTo.getName();
		
		
		return "Base [loc=" + getLoc() + "base=" + base + 
				", fielderOn=" + fielderOnName + 
				", runnerOn=" + runnerOnName + 
				", forceOut=" + forceOut
				+ ", runnerTo=" +
				runnerToName + "]";
	}
	
}
