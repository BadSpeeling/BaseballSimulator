package ratings;
import datatype.PitchType;

/* PitchRatings is a type a ball that can be thrown at a batter
 * To be used in maps that have the key be PitchType
 * */
public class PitchRatings {
	
	public PitchType name;
	public int maxVelo;
	public int minVelo;
	public int stuff;
	public int control;
	
	public PitchRatings (int max, int min, int stuff, int control) {
		maxVelo = max;
		minVelo = min;
		this.stuff = stuff;
		this.control = control;
	}
	
}
