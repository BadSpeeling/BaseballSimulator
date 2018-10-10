package ratings;

import numbers.PercentileConverter;
import numbers.RandomNumber;

/* Eric Frye
 * GeneralRatings represents the general ratings of a baseball player; ratings that are not batting, pitching and fielding/
 * */

public class GeneralRatings {
	
	private double speed; // ft/s
	
	public GeneralRatings () {
		
	}
	
	public GeneralRatings (double speed) {
		this.speed = speed;
	}
	
	public void simpleGenerateGeneralRatings () {
		speed = PercentileConverter.getValue(25, 1.5);
		
		speed = Math.max(speed, 18);
		
	}
	
	public double getSpeed () {
		return speed;
	}
	
	public double gloveToHandTime () {
		return .5;
	}
	
	public double windUpTime () {
		return .8;
	}
	
	//in ft/s
	public double throwSpeed () {
		return 100;
	}
	
	public double reactionTime () {
		return .25;
	}
	
	public String toWriter () {
		return speed + ",";
	}
	
}
