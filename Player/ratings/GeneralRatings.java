package ratings;
/* Eric Frye
 * GeneralRatings represents the general ratings of a baseball player; ratings that are not batting, pitching and fielding/
 * */

public class GeneralRatings {
	
	private double speed = 22; // ft/s
	
	public double runSpeed () {
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
	
}
