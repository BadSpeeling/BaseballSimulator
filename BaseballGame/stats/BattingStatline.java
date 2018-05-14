package stats;

import java.util.LinkedList;
import java.util.List;

public class BattingStatline extends Statline {
	
	private List <PlateAppearance> plateAppearances = new LinkedList <PlateAppearance> (); //list of PAs

	private int runs = 0;
	private int rbi = 0;
	private int ab = 0;	
	private int pa = 0;
	
	public BattingStatline(int pID) {
		super(pID);
	}

	public int getRuns() {
		return runs;
	}

	public int getRbi() {
		return rbi;
	}

	public int getAB() {
		return ab;
	}
	
	public int getPA() {
		return pa;
	}
	
	public void incRuns () {
		runs++;
	}
	
	public void incRBI () {
		rbi++;
	}
	
	public void incAB () {
		ab++;
	}
	
	public void addPA (PlateAppearance toAdd) {
		
		pa++;
		plateAppearances.add(toAdd);
		
		Result outcome = toAdd.getOutcome();
		
		if (!outcome.equals(Result.BB)) {
			ab++;
		}
		
	}

	public String toString() {
		return super.toString() + "BattingStatline [plateAppearances=" + plateAppearances + ", runs=" + runs + ", rbi=" + rbi + ", ab="
				+ ab + ", pa=" + pa + "]";
	}

	
}
