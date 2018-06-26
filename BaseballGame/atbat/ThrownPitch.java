package atbat;
import ball.BallInPlay;
import datatype.Coordinate3D;
import game.FieldConstants;
import numbers.PercentileConverter;
import numbers.RandomNumber;
import physics.Physics;
import player.Player;
import ratings.PitchRatings;
import ratings.PitchType;
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
	
	public PitchType getPitchSelection() {
		return pitchSelection;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getFilth() {
		return filth;
	}

	public Player getPitcher() {
		return pitcher;
	}

	public Player getBatter() {
		return batter;
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

				ContactType contactType = getContactType();
				
				if (contactType.notInPlay()) {
					pa.incStrikes();
				}
				
				//we hit the ball, get a hit type
				else {
					
					calc.changeBallCounts(contactType.ballCountChange());
					HitType hitType = calc.getHitType();
					
					return new BallInPlay (new Coordinate3D (0,0,3), 
							hitType.launchAngle(), 
							hitType.launchDir(),
							hitType.baseHitSpeed(), stadium, 0xFF00FF, hitType);


				}


			}

			else {
				pa.incStrikes();
			}

		}

		return null;

	}

	private void randomPitchLocation () {
		x = RandomNumber.roll(-280, 280)/100.0;
		y = RandomNumber.roll(-280, 280)/100.0;
	}

	private ContactType getContactType () {
		
		int num = RandomNumber.roll(0,7);
		
		switch (num) {
			case 0: return ContactType.MISS;
			case 1: return ContactType.FOUL;
			case 2: return ContactType.TERRIBLE;
			case 3: return ContactType.POOR;
			case 4: return ContactType.BELOW_AVG;
			case 5: return ContactType.AVERAGE;
			case 6: return ContactType.GOOD;
			case 7: return ContactType.EXCELLENT;
			default: return null;
		}
		
	}
	
	private boolean pitchIsBall () {
		return x < -2 || x > 2 || y < -2 || y > 2;
	}

	public String toString() {
		return "ThrownPitch [pitchSelection=" + pitchSelection + ", x=" + x + ", y=" + y + ", velocity=" + velocity
				+ ", filth=" + filth + ", pitcher=" + pitcher + ", batter=" + batter + "]";
	}

}
