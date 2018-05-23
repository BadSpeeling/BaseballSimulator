package game;
import ball.BallInPlay;
import datatype.PitchType;
import numbers.RandomNumber;
import physics.Physics;
import player.Player;
import ratings.PitchRatings;
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
	
	//generates a BallInPlay, or null if the ThrownPitch is not put in play
	public BallInPlay run (PlateAppearance pa, Stadium stadium) {
		
		PitchType catchersCall;
		
		catchersCall = PitchType.FB;
		
		PitchRatings curPitchRatings = pitcher.pRatings.selection.get(catchersCall);
		
		//get value for velocity and filth
		velocity = curPitchRatings.getVelocity().getValue();
		filth = curPitchRatings.getFilth().getValue();
		
		//change location of 
		randomPitchLocation();
		double xMutator = curPitchRatings.getControl().getValue();
		double yMutator = curPitchRatings.getControl().getValue();
		x += xMutator;
		y += yMutator;
		
		//the pitch is outside the strikezone
		if (x < -2 || x > 2 || y < -2 || y >= 2) {
			pa.incBalls();
		}
		
		else {
			
			double swingPct = (batter.bRatings.getAggr().getValue())*100;
			System.out.println(swingPct);
			
			//swinging
			if (RandomNumber.roll(0, 100) >= swingPct) {
				return new BallInPlay (FieldConstants.newPitch(),Physics.degreesToRads(RandomNumber.roll(0, 30)),Physics.degreesToRads(RandomNumber.roll(0, 90)),140,stadium,0x000000);
			}
			
			else {
				pa.incStrikes();
			}
			
		}
		
		return null;
		
	}
	
	private void randomPitchLocation () {
		x = RandomNumber.roll(-2, 2);
		y = RandomNumber.roll(-2, 2);
	}

	public String toString() {
		return "ThrownPitch [pitchSelection=" + pitchSelection + ", x=" + x + ", y=" + y + ", velocity=" + velocity
				+ ", filth=" + filth + ", pitcher=" + pitcher + ", batter=" + batter + "]";
	}

}
