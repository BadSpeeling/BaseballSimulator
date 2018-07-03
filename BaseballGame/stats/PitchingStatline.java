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
	
	public void add (PitchingStatline other) {
		incOutsRec(other.getOutsRec());
		incERA(other.getEra());
		incRA(other.getRa());
		incBf(other.getBf());
		super.add(other.getHits(), other.getDoubles(), other.getTriples(), other.getHomeruns(), other.getWalks(), other.getStrikeouts());
	}
	
	public void incOutsRec (int by) {
		outsRec += by;
	}
	
	public void incERA (int by) {
		era += by;
	}
	
	public void incRA (int by) {
		ra += by;
	}
	
	public void incBf (int by) {
		bf += by;
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

	public String toString() {
		return super.toString() + "PitchingStatline [battersFaced=" + battersFaced + ", bf=" + bf + ", outsRec=" + outsRec + ", era=" + era
				+ ", ra=" + ra + "]";
	}
	
}
