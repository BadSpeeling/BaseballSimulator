package stats;

import java.util.LinkedList;
import java.util.List;

public class PitchingStatline extends Statline {
	
	private List <PlateAppearance> battersFaced = new LinkedList <PlateAppearance> ();
	
	private int bf = 0; //batters faced
	private int outsRec = 0; //num outs recorded
	private int era = 0; //earned runs allowed
	private int ra = 0; //runs allowd
	
	public PitchingStatline(int pID) {
		super(pID);
	}
	
	public void incOutsRec () {
		outsRec++;
	}
	
	public void incERA () {
		era++;
	}
	
	public void incRA () {
		ra++;
	}

	public List<PlateAppearance> getBattersFaced() {
		return battersFaced;
	}

	public int getBf() {
		return bf;
	}

	public int getOutsRec() {
		return outsRec;
	}

	public int getEra() {
		return era;
	}

	public int getRa() {
		return ra;
	}
	
	public void addPA (PlateAppearance toAdd) {
		
		bf++;
		
		super.addPA(toAdd);
		
		if (toAdd.getOutcome().equals(Result.OUT)) {
			outsRec++;
		}
		
	}
	
}
