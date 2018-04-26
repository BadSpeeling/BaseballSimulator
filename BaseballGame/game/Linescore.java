package game;

public class Linescore {
	
	public boolean homeTeam;
	public int hits;
	public int runs;
	public int errors;
	public int [] inning_scores = new int [18]; //inning_scores is the score for each half inning in chronological order. 
	
	public Linescore (boolean home) {
		homeTeam = home;
		hits = 0;
		runs = 0;
		errors = 0;
	}
	
	public Linescore (Linescore copy) {
		this.hits = copy.hits;
		this.runs = copy.runs;
		this.errors = copy.errors;
	}
	
	public String toString () {
		return runs + " " + hits + " " + errors;
	}
	
}
