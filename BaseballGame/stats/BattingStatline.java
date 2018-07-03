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
	
	public void incRuns(int by) {
		runs += by;
	}

	public void incRbi(int by) {
		rbi += by;
	}

	public void incAB(int by) {
		ab += by;
	}
	
	public void incPA(int by) {
		pa += by;
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
		super.addPA(toAdd);
		
		Result outcome = toAdd.getOutcome();
		
		if (!outcome.equals(Result.BB)) {
			ab++;
		}
		
	}

	public String toString() {
		return super.toString() + "BattingStatline [plateAppearances=" + plateAppearances + ", runs=" + runs + ", rbi=" + rbi + ", ab="
				+ ab + ", pa=" + pa + "]";
	}

	public void add (BattingStatline other) {
		incRuns(other.getRuns());
		incRbi(other.getRbi());
		incAB(other.getAB());
		incPA(other.getPA());
		super.add(other.getHits(), other.getDoubles(), other.getTriples(), other.getHomeruns(), other.getWalks(), other.getStrikeouts());
	}
	
}
