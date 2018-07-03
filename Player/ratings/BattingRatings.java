package ratings;

import numbers.PercentileConverter;
import numbers.RandomNumber;

/* Eric Frye
 * BattingRatings represents the batting ratings of a player.  They are as follows:
 * contact
 * */

public class BattingRatings {
	
	private HitBallType hitBallType;
	private SprayType sprayType;
	
	private double contact;
	private double power;
	private double discipline;
	
	public BattingRatings (int hitBallNum, int sprayNum) {
		hitBallType = HitBallType.convert(hitBallNum);
		sprayType = SprayType.convert(sprayNum);
	}
	
	public double getContact () {
		return contact;
	}
	
	public double getHitSpeed () {
		
		if (RandomNumber.roll(0, 100) > 50) {
			return RandomNumber.roll(130, 165);
		}
		
		return RandomNumber.roll(80, 130);
		
	}
	
	//calculate by a normal distribution
	public void simpleGenerateBattingStats () {
		contact = PercentileConverter.getValue(50, 25);
		power = PercentileConverter.getValue(50, 25);
		discipline = PercentileConverter.getValue(50, 25);
	}
	
	public String toWriter () {
		return contact+","+power+","+discipline+",";
	}
	
}
