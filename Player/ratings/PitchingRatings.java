package ratings;
import java.util.ArrayList;
import java.util.HashMap;

import datatype.PitchType;

/* Eric Frye
 * PitchingRatings represents the pitching stats of a player.  They are as follows:
 * */

public class PitchingRatings {

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
		
}

