package ratings;

import numbers.RandomNumber;

/* Eric Frye
 * BattingRatings represents the batting ratings of a player.  They are as follows:
 * contact
 * */

public class BattingRatings {
	
	private HitBallType hitBallType;
	private SprayType sprayType;
	
	public BattingRatings (int hitBallNum, int sprayNum) {
		hitBallType = HitBallType.convert(hitBallNum);
		sprayType = SprayType.convert(sprayNum);
	}
	
	public double getHitSpeed () {
		
		if (RandomNumber.roll(0, 100) > 50) {
			return RandomNumber.roll(130, 165);
		}
		
		return RandomNumber.roll(80, 130);
		
	}
	
}
