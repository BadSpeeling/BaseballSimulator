package ratings;

import ball.HitBallType;
import datatype.RandomNumber;

/* Eric Frye
 * BattingRatings represents the batting ratings of a player.  They are as follows:
 * contact
 * */

public class BattingRatings {
	
	//these must add up to 100
	private double groundBallPct;
	private double linerPct;
	private double flyBallPct;
	
	public BattingRatings () {
		basicInit();
	}
	
	public void basicInit () {
		
		double percentAvail = 100;
		groundBallPct = RandomNumber.roll(20, 40);
		percentAvail -= groundBallPct;
		
		linerPct = RandomNumber.roll(20, 35);
		percentAvail -= linerPct;
		
		flyBallPct = percentAvail;
		
	}
	
	public HitBallType hitType () {
		
		double rNum = RandomNumber.roll(0, 100);
		
		rNum -= groundBallPct;
		
		if (rNum <= 0) {
			return HitBallType.GB;
		}
		
		rNum  -= linerPct;
		
		if (rNum <= 0) {
			return HitBallType.LINER;
		}
		
		return HitBallType.FLY;
		
	}
	
	public double getHitSpeed () {
		
		if (RandomNumber.roll(0, 100) > 50) {
			return RandomNumber.roll(130, 165);
		}
		
		return RandomNumber.roll(80, 130);
		
	}
	
}
