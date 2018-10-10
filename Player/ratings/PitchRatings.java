package ratings;
import numbers.PercentileConverter;
import numbers.RandomNumber;

import org.apache.commons.math3.distribution.NormalDistribution;

/* PitchRatings is a type a ball that can be thrown at a batter
 * To be used in maps that have the key be PitchType
 * */
public class PitchRatings {
	
	private double velocity;
	private double control; 
	private double filth;
	
	//statstics must be: 3 by 2 matrix holding values:
	//VELO mean VELO sd
	//CONTROL mean CONTROL sd
	//FILTH mean FILTH sd
	public PitchRatings (double velocity, double control, double filth) {
		
		this.velocity = velocity;
		this.control = control;
		this.filth = filth;
		
	}
	
	public PitchRatings () {
		
	}
	
	public double getVelocity() {
		return velocity;
	}

	public double getControl() {
		return control;
	}

	public double getFilth() {
		return filth;
	}
	
	public void simpleGeneratePitchRatings () {

		velocity = PercentileConverter.getValue(93, 2);
		control = PercentileConverter.getValue(50, 15);
		filth = PercentileConverter.getValue(50, 15);
		
	}
	
}
