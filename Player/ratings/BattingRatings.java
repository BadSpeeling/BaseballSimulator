package ratings;

import numbers.RandomNumber;

/* Eric Frye
 * BattingRatings represents the batting ratings of a player.  They are as follows:
 * contact
 * */

public class BattingRatings {
	
	//these must add up to 100
	private double groundBallPct;
	private double linerPct;
	private double flyBallPct;
	
	private HitBallType hitBallType;
	private SprayType sprayType;
	
	private RatingPair aggressiveness;
	private RatingPair contactRate;
	
	public BattingRatings (int hitBallNum, int sprayNum) {
		hitBallType = HitBallType.convert(hitBallNum);
		sprayType = SprayType.convert(sprayNum);
		aggressiveness = new RatingPair(.6, .10);
		basicInit();
	}
	
	public RatingPair getAggr () {
		return aggressiveness;
	}
	
	public RatingPair getContact () {
		return contactRate;
	}
	
	public void basicInit () {
		
		double percentAvail = 100;
		groundBallPct = RandomNumber.roll(20, 40);
		percentAvail -= groundBallPct;
		
		linerPct = RandomNumber.roll(20, 35);
		percentAvail -= linerPct;
		
		flyBallPct = percentAvail;
		
	}
	
	
	public double getHitSpeed () {
		
		if (RandomNumber.roll(0, 100) > 50) {
			return RandomNumber.roll(130, 165);
		}
		
		return RandomNumber.roll(80, 130);
		
	}
	
}
