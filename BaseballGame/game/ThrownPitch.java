package game;
import datatype.PitchType;
import datatype.RandomNumber;
import player.Player;
import ratings.PitchRatings;

/* ThrownPitch is a ball the has been pitched by a pitcher to a hitter
 * */

public class ThrownPitch {
	
	public PitchType thrown; //type of pitch thrown
	public int x;
	public int y;
	public int velo; //velocity of the pitch
	public int filth; //how hard to hit it will be to hit the pitch
	
	/* ----------------------------------------|
	 * |(-2,+2)|(-1,+2)|(+0,+2)|(+1,+2)|(+2,+2)|
	 * |---------------------------------------|
	 * |(-2,+1)|(-1,+1)|(+0,+1)|(+1,+1)|(+2,+1)|
	 * |---------------------------------------|
	 * |(-2,+0)|(-1,+0)|(+0,+0)|(+1,+0)|(+2,+0)|
	 * |---------------------------------------|
	 * |(-2,-1)|(-1,-1)|(+0,-1)|(+1,-1)|(+2,-1)|
	 * |---------------------------------------|
	 * |(-2,-2)|(-1,-2)|(+0,-2)|(+1,-2)|(+2,-2)|
	 * |---------------------------------------|
	 * */
	
	/* thrown - enum for type of pitch thrown
	 * x - x coordinate for zone
	 * y - y coordinate for zone
	 * velo - the velocity the ball was thrown at
	 * */
	public ThrownPitch (PitchType thrown, int x, int y, int velo) {

		this.x = x;
		this.y = y;
		this.thrown = thrown;
		this.velo = velo;
		
	}
	
	/* Calculates where the ball will end up being located using probability
	 * pitcher - the player that threw the ball
	 * rats - the ratings of the type of pitch for the given pitcher
	 * */
	public void generateFinalLocation (Player pitcher, PitchRatings rats) {
		
		x += RandomNumber.roll(-1, 1);
		y += RandomNumber.roll(-1, 1);
		filth = rats.stuff;
		
	}
	
}
