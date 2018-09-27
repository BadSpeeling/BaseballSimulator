package stats;

public class Statline {

	private final int pID;

	//counting stats
	private int hits = 0;
	private int doubles = 0;
	private int triples = 0;
	private int homeruns = 0;
	private int walks = 0;
	private int strikeouts = 0;
	private boolean isStarter;
	
	public Statline(int pID, boolean isStarter) {
		super();
		this.isStarter = isStarter;
		this.pID = pID;
	}
	
	public void add (int h, int d, int t, int hr, int bb, int k) {
		incHits(h);
		incDoubles(d);
		incTriples(t);
		incHomeruns(hr);
		incWalks(bb);
		incStrikeouts(k);
	}
	
	public void incHits (int by) {
		hits += by;
	}
	
	public void incDoubles (int by) {
		hits += by;
	}
	
	public void incTriples (int by) {
		hits += by;
	}
	
	public void incHomeruns (int by) {
		hits += by;
	}
	
	public void incWalks (int by) {
		hits += by;
	}
	
	public void incStrikeouts (int by) {
		hits += by;
	}

	public void addPA (PlateAppearance toAdd) {

		switch (toAdd.getOutcome()) {
			case S:
				hits++;
				break;
			case D:
				doubles++;
				hits++;
				break;
			case T:
				triples++;
				hits++;
				break;
			case HR:
				homeruns++;
				hits++;
				break;
			case BB:
				walks++;
				break;
			case K:
				strikeouts++;
				break;
			default:
				break;
		}

	}

	public int getpID() {
		return pID;
	}

	public int getHits() {
		return hits;
	}

	public int getDoubles() {
		return doubles;
	}

	public int getTriples() {
		return triples;
	}

	public int getHomeruns() {
		return homeruns;
	}

	public int getWalks() {
		return walks;
	}

	public int getStrikeouts() {
		return strikeouts;
	}

	public void incHits () {
		hits++;
	}

	public void incDouble () {
		doubles++;
	}

	public void incTriples () {
		triples++;
	}

	public void incHomeruns () {
		homeruns++;
	}

	public void incWalks () {
		walks++;
	}

	public void incStrikeouts () {
		strikeouts++;
	}

	public String toString() {
		return "Statline [pID=" + pID + ", hits=" + hits + ", doubles=" + doubles + ", triples=" + triples
				+ ", homeruns=" + homeruns + ", walks=" + walks + ", strikeouts=" + strikeouts + "]";
	}
	

}
