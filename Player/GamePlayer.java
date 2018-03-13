import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class GamePlayer extends Player {
	
	Hashtable <String, Double> stats = new Hashtable <String, Double> ();
	
	public GamePlayer (String f, String l, Position p) {
		
		super(f,l,p);
		
		Scanner input = null;
		
		try {
			input = new Scanner (new File ("Player\\Stats\\stats.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String [] vals = input.nextLine().split(",");
		
		for (int i = 0; i < vals.length; i++) {
			stats.put(vals[i], 0.0);
		}
		
	}
	
	/* Throw the pitch.  Alters the arriving location of the pitch based on pitchers ratings
	 * batter - the player at the plate
	 * catcher - the player calling the pitch
	 * returns: Thrown pitch with appropriate x and y coordiantes 
	 * */
	public ThrownPitch throwPitch (GamePlayer batter, GamePlayer catcher) {
		
		CatcherCall calledPitch = catcher.callPitch(batter, this);
		return new ThrownPitch (calledPitch.ratings.name, calledPitch.x, calledPitch.y, RandomNumber.roll(calledPitch.ratings.minVelo, calledPitch.ratings.maxVelo));
		
	}
	
	/* The catcher decides what pitch should be called based on aspects such as pitchers skill, batters tendencies, and his own catching ability.
	 * batter - the player at the plate
	 * pitcher - the player throwing the pitch
	 * */
	public CatcherCall callPitch (GamePlayer batter, GamePlayer pitcher) {
		
		PitchRatings selection = pitcher.pRatings.selection.get(0);
		int x;
		int y;
		
		if (RandomNumber.roll(0, 100) < 30) {
			x = RandomNumber.roll(-1, 1);
			y = RandomNumber.roll(-1, 1);
		}
		
		else {
			x = RandomNumber.roll(-2, 2);
			y = RandomNumber.roll(-2, 2);
		}
		
		return new CatcherCall (selection, x, y);

	}
 
	
	//representation of the pitch and location that the catcher called for
	private class CatcherCall {
		PitchRatings ratings;
		int x;
		int y;
		
		public CatcherCall (PitchRatings selection, int x, int y) {
			this.ratings = selection;
			this.x = x;
			this.y = y;
		}
		
	}
	
}
