package atbat;

import java.util.LinkedList;
import java.util.List;

import numbers.RandomNumber;

public class HitTypeCalculator {
	
	private List <PingPongBall> lottery = new LinkedList <PingPongBall> ();
	
	//init values for ball counts
	private final int weakGrounderBalls = 15;
	private final int dribblerBalls = 10;
	private final int solidGrounderBalls = 20;
	private final int flareBalls = 10;
	private final int softlinerBalls = 10;
	private final int hardLinerBalls = 15;
	private final int popupBalls = 10;
	private final int flyBalls = 10;
	private final int deepFlyBalls = 6;
	private int totalBalls = 0;
		
	public void addBall (HitType type, int balls) {
		lottery.add(new PingPongBall(type,balls));
		totalBalls += balls;
	}
	
	public void init () {
		addBall(HitType.DRIBBLER,dribblerBalls);
		addBall(HitType.WEAKGROUNDER,weakGrounderBalls);
		addBall(HitType.SOLIDGROUNDER,solidGrounderBalls);
		addBall(HitType.FLARE,flareBalls);
		addBall(HitType.SOFTLINER,softlinerBalls);
		addBall(HitType.HARDLINER,hardLinerBalls);
		addBall(HitType.POPUP,popupBalls);
		addBall(HitType.FLYBALL,flyBalls);
		addBall(HitType.DEEPFLYBALL,deepFlyBalls);
	}
	
	//determines the quality of the contact.  to be called when it has been determined that a swing will take place
	//the type of contact that is returned will determine how to change the ball counts
	public ContactType determineContactType (ThrownPitch pitch) {
		
		return null;
		
	}
	
	//generates a random number which is then used to run the lottery to determine the type of hit
	public HitType getHitType () {
		
		int chosenNum = RandomNumber.roll(0,amountOfBalls());
		
		for (PingPongBall curBall: lottery) {
			
			chosenNum -= curBall.getNumBalls();
			
			//check if this hittype won
			if (chosenNum <= 0) {
				return curBall.getType();
			}
			
		}
		
		return lottery.get(lottery.size()-1).getType();
		
	}
	
	public int amountOfBalls () {
		
		int ret = 0;
		
		for (PingPongBall ball: lottery) {
			ret += ball.getNumBalls();
		}
		
		return ret;
		
	}
	
	//changes the counts of the PingPongBall from the array passed in, must be in order that they were added in for init()
	public void changeBallCounts (int [] changeBy) throws IllegalArgumentException {
		
		if (changeBy.length != lottery.size()) {
			throw new IllegalArgumentException("changeBy is not the correct dimensions");
		}
		
		for (int i = 0; i < lottery.size(); i++) {
			lottery.get(i).changeNumBallsBy(changeBy[i]);
		}
		
	}
	
	class PingPongBall {
		
		private HitType type;
		private int numBalls;
		
		public PingPongBall(HitType type, int numBalls) {
			this.type = type;
			this.numBalls = numBalls;
		}

		public HitType getType() {
			return type;
		}

		public int getNumBalls() {
			return numBalls;
		}
		
		public void changeNumBallsBy (int count) {
			numBalls += count;
			
			if (numBalls < 0) {
				numBalls = 0;
			}
			
		}
		
		public boolean equals (HitType check) {
			return check == type;
		}
		
	}
	
}
