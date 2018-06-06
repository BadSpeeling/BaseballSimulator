package atbat;
import ball.BallInPlay;
import datatype.Coordinate3D;
import datatype.PitchType;
import game.FieldConstants;
import numbers.PercentileConverter;
import numbers.RandomNumber;
import physics.Physics;
import player.Player;
import ratings.PitchRatings;
import ratings.UniversalRatings;
import stadium.Stadium;
import stats.PlateAppearance;

/* ThrownPitch is a ball the has been pitched by a pitcher to a hitter
 * */

public class ThrownPitch {
	
	private PitchType pitchSelection;
	
	//coordinate vals
	private double x; 
	private double y;
	
	private double velocity; //speed of pitch measured in mph
	private double filth; //movement of pitch
	
	//players involved in event
	private Player pitcher;
	private Player batter;
	
	public ThrownPitch (Player pitcher, Player batter) {
		this.pitcher = pitcher;
		this.batter = batter;
	}
	
	//generates a BallInPlay, or null if the ThrownPitch is not put in play.
	//post condition: balls and strikes have been updated
	public BallInPlay throwBall (PlateAppearance pa, Stadium stadium, HitTypeCalculator calc) {
		
		PitchType catchersCall;
		
		catchersCall = PitchType.FB;
		
		PitchRatings curPitchRatings = pitcher.pRatings.selection.get(catchersCall);
		
		/*
		 * calculate the location of the pitch
		 * */
		randomPitchLocation();
		
		if (pitchIsBall()) {
			
			if (UniversalRatings.swingAtBallPercent.success()) {
				pa.incStrikes();
			}
			
			else {
				pa.incBalls();
			}
			
		}
		
		else {
			
			if (UniversalRatings.swingAtStrikePercent.success()) {
				
				//we hit the ball, get a hit type
				if (UniversalRatings.contactPercent.success()) {
					
					HitType hitType = calc.getHitType();
					
					if (hitType.equals(HitType.FOUL)) {
						pa.foul();
					}
					
					else {
						return new BallInPlay (new Coordinate3D (0,0,0), Physics.degreesToRads(RandomNumber.roll(0, 25)), Physics.degreesToRads(RandomNumber.roll(0,90)), batter.bRatings.getHitSpeed(), stadium, 0xFF00FF);
					}
					
				}
				
				else {
					pa.incStrikes();
				}
				
			}
			
			else {
				pa.incStrikes();
			}
			
		}
		
		return null;
		
	}
	
	private void randomPitchLocation () {
		x = RandomNumber.roll(-275, 275)/100.0;
		y = RandomNumber.roll(-275, 275)/100.0;
	}
	
	private boolean pitchIsBall () {
		return x < -2 || x > 2 || y < -2 || y > 2;
	}

	public String toString() {
		return "ThrownPitch [pitchSelection=" + pitchSelection + ", x=" + x + ", y=" + y + ", velocity=" + velocity
				+ ", filth=" + filth + ", pitcher=" + pitcher + ", batter=" + batter + "]";
	}

}
