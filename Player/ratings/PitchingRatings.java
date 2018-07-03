package ratings;
import java.util.ArrayList;
import java.util.HashMap;

import numbers.PercentileConverter;

/* Eric Frye
 * PitchingRatings represents the pitching stats of a player.  They are as follows:
 * */

public class PitchingRatings {

	private double velocity;
	private double control; 
	private double filth;
	
	public HashMap <PitchType,PitchRatings> selection = new HashMap <PitchType,PitchRatings> (); 
	
	/* adds a pitch to the players pitch selection
	 * type - type of pitch
	 * max - max velocity
	 * min - minimum velocity
	 * stuff - swing and miss ability
	 * control - ability to put ball where player wants
	 * */
	
	public void basicAddFastball () {
		PitchRatings toAdd = new PitchRatings();
		selection.put(PitchType.FB, toAdd);
	}
		
	public void simpleGeneratePitchRatings () {

		velocity = PercentileConverter.getValue(93, 2);
		control = PercentileConverter.getValue(50, 15);
		filth = PercentileConverter.getValue(50, 15);
		
	}
	
	public String toWriter () {
		return velocity + "," + control + "," + filth;
	}
	
}

