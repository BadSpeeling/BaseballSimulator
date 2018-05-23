package ratings;
import datatype.PitchType;
import numbers.RandomNumber;

import org.apache.commons.math3.distribution.NormalDistribution;

/* PitchRatings is a type a ball that can be thrown at a batter
 * To be used in maps that have the key be PitchType
 * */
public class PitchRatings {
	
	private RatingPair velocity;
	private RatingPair control; 
	private RatingPair filth;
	
	//statstics must be: 3 by 2 matrix holding values:
	//VELO mean VELO sd
	//CONTROL mean CONTROL sd
	//FILTH mean FILTH sd
	public PitchRatings (double [][] statistics) {
		
		if (statistics.length != 3 && statistics[0].length != 2) {
			throw new IllegalArgumentException("Invalid statistics matrix in PitchRatings");
		}
		
		else {
			this.velocity = new RatingPair(statistics[0][0], statistics[0][1]);
			this.control = new RatingPair(statistics[1][0], statistics[1][1]);
			this.filth = new RatingPair(statistics[2][0], statistics[2][1]);			
		}
		
	}
	
	public RatingPair getVelocity() {
		return velocity;
	}

	public RatingPair getControl() {
		return control;
	}

	public RatingPair getFilth() {
		return filth;
	}
	
}
