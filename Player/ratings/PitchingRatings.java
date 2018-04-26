package ratings;
import java.util.ArrayList;
import java.util.HashMap;

import datatype.PitchType;

/* Eric Frye
 * PitchingRatings represents the pitching stats of a player.  They are as follows:
 * */

public class PitchingRatings {

	public double strike_pct = 60; //percentage of pitches that are a strike
	public HashMap <PitchType,PitchRatings> selection = new HashMap <PitchType,PitchRatings> (); 
	
	public PitchingRatings (String pitches) {
		
		String [] toAdd = pitches.split(";");
		
		for (int i = 0; i < toAdd.length; i++) {
			
			String [] cur = toAdd[i].split(",");
			selection.put(PitchType.convert(cur[0]), new PitchRatings (Integer.parseInt(cur[1]), Integer.parseInt(cur[2]), Integer.parseInt(cur[3]), Integer.parseInt(cur[4])));
			
		}
		
	}
	
	/* adds a pitch to the players pitch selection
	 * type - type of pitch
	 * max - max velocity
	 * min - minimum velocity
	 * stuff - swing and miss ability
	 * control - ability to put ball where player wants
	 * */
	public void addPitch (String type, int max, int min, int stuff, int control) {
		selection.put(PitchType.convert(type), new PitchRatings(max, min, stuff, control));
	}
		
}

