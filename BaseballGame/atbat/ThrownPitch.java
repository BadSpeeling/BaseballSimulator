package atbat;
import ball.BallInPlay;
import datatype.Coordinate3D;
import game.FieldConstants;
import numbers.PercentileConverter;
import numbers.RandomNumber;
import objects.GamePlayer;
import physics.Physics;
import ratings.Modifier;
import ratings.PitchRatings;
import ratings.PitchType;
import ratings.UniversalRatings;
import stadium.Stadium;
import stats.PlateAppearance;
import utility.Function;

/* ThrownPitch is a ball the has been pitched by a pitcher to a hitter
 * */

public class ThrownPitch {

	private final int missBoundry = -50;
	private final int terribleBoundry = -35;
	private final int poorBoundry = -20;
	private final int belowAvgBoundry = -5;
	private final int avgBoundry = 0;
	private final int aboveBoundry = 10;
	private final int goodBoundry = 30;
	private final int excellentBoundry = 45;

	private PitchType pitchSelection;
	
	private Modifier contactMod;
	
	//coordinate vals
	private double x; 
	private double y;

	private double velocity; //speed of pitch measured in mph
	private double filth; //movement of pitch

	//players involved in event
	private GamePlayer pitcher;
	private GamePlayer batter;

	public ThrownPitch (GamePlayer pitcher, GamePlayer batter) {
		this.pitcher = pitcher;
		this.batter = batter;
		this.contactMod = new Modifier(0,2.5);
		this.contactMod.setMean(100);
		this.contactMod.setSd(30);
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

	public GamePlayer getPitcher() {
		return pitcher;
	}

	public GamePlayer getBatter() {
		return batter;
	}

	private ContactType determineContactType (double pitchQuality, double swingQuality) {

		double diff = swingQuality - pitchQuality;

		if (diff < missBoundry) {
			return ContactType.MISS;
		}

		else if (diff < terribleBoundry) {
			return ContactType.TERRIBLE;
		}

		else if (diff < poorBoundry) {
			return ContactType.POOR;
		}

		else if (diff < belowAvgBoundry) {
			return ContactType.BELOW_AVG;
		}

		else if (diff < avgBoundry) {
			return ContactType.AVERAGE;
		}

		else if (diff < aboveBoundry){
			return ContactType.ABOVE_AVG;
		}

		else if (diff < goodBoundry) {
			return ContactType.GOOD;
		}
		
		else {
			return ContactType.EXCELLENT;
		}

	}

	//returns the quality of a pitch
	private double calculatePitchQuality () {

		double [][] bounds = {{0,1},{1,2},{2,3}};
		String [] function = {"-5.5+5x^2","3.5x^2","-3.3x"};
		Function func = new Function (function,bounds);
		
		double val = func.val(Math.abs(x)) + func.val(Math.abs(y));
		
		return val;

	}
	
	//returns quality of swing
	private double calculateSwingQuality () {		
		return (RandomNumber.roll(-400, 400)/10) + contactMod.changeBy(batter.getbRatings().getContact());
	}

	//generates a BallInPlay, or null if the ThrownPitch is not put in play.
	//post condition: balls and strikes have been updated
	public BallInPlay throwBall (PlateAppearance pa, Stadium stadium, HitTypeCalculator calc) {

		PitchType catchersCall;

		catchersCall = PitchType.FB;

		PitchRatings curPitchRatings = pitcher.getpRatings().selection.get(catchersCall);
		
		/*
		 * calculate the location of the pitch
		 * */
		randomPitchLocation();
		double pitchQuality = calculatePitchQuality();

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
				
				double swingQuality = calculateSwingQuality();
				
				ContactType contactType = determineContactType(pitchQuality, swingQuality);

				if (contactType.notInPlay()) {
					pa.incStrikes();
				}

				//we hit the ball, get a hit type
				else {

					calc.changeBallCounts(contactType.ballCountChange());
					HitType hitType = calc.getHitType();

					BallInPlay ret = null;
					
					do {
						ret =  new BallInPlay (new Coordinate3D (0,0,3), 
								hitType.launchAngle(), 
								hitType.launchDir(),
								hitType.baseHitSpeed(), stadium, 0xFF00FF, hitType);
					}//prevent infinity from being a value - likely a result of the .jar file being used for percentile calc
					while (Double.isInfinite(ret.launchAngle) || Double.isInfinite(ret.launchDir) || Double.isInfinite(ret.launchSpeed));
					
					return ret;
					
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

	private boolean pitchIsBall () {
		return x < -2 || x > 2 || y < -2 || y > 2;
	}

	public String toString() {
		return "ThrownPitch [pitchSelection=" + pitchSelection + ", x=" + x + ", y=" + y + ", velocity=" + velocity
				+ ", filth=" + filth + ", pitcher=" + pitcher + ", batter=" + batter + "]";
	}

}
