package ratings;

//a modifier is a entity attatched to an EventValue that changes the likelihood of the event happening
//based on a player's rating percentile
public class Modifier {
	
	private ModifierType type;
	private double changeByZScore;
	
	//calculated one time before going into a game.  
	private double mean;
	private double sd;
	
	public Modifier (int id, double changeByZScore) {
		this.changeByZScore = changeByZScore;
		this.type = getType(id);
	}
	
	//NOTE --- this method must be called before all calculations are performed
	//computes the mean and sd for the based in ratings
	public void computeStatistics (double [] ratings) {
		
		int count = ratings.length;
		mean = 0;
		
		for (int i = 0; i < ratings.length; i++) {
			mean += ratings[i];
		}
		
		mean /= count;
		
		for (int i = 0; i < ratings.length; i++) {
			sd = Math.pow(mean-ratings[i], 2.0);
		}
		
		sd /= count;
		
		sd = Math.sqrt(sd);
		
	}
	
	//NOTE ---- computeStatistics must be called first
	//compute the value to increment percentile by
	public double changeBy (double rating) {
		return ((rating-mean)/sd) * changeByZScore;
	}
	
	private ModifierType getType (int id) {
		switch (id) {
			case 0:
				return ModifierType.CONTACT;
			case 1:
				return ModifierType.DISCIPLINE;
			case 2:
				return ModifierType.POWER;
			case 3:
				return ModifierType.CONTROL;
			case 4:
				return ModifierType.VELOCITY;
			default:
				return null;
		}
	}
	
}
