package atbat;

import java.util.LinkedList;
import java.util.List;

import numbers.RandomNumber;

public class HitTypeCalculator {
	
	private List <PingPongBall> lottery = new LinkedList <PingPongBall> ();
	
	//init values for ball counts
	private int dribblerBalls = 5;
	private int weakGrounderBalls = 15;
	private int solidGrounderBalls = 15;
	private int flareBalls = 10;
	private int softlinerBalls = 15;
	private int hardLinerBalls = 20;
	private int popupBalls = 10;
	private int flyBalls = 15;
	private int deepFlyBalls = 10;
	private int foulBalls = 25;
	private int totalBalls;
	
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
		addBall(HitType.FOUL,foulBalls);
		
	}
	
	public HitType getHitType () {
		
		int chosenNum = RandomNumber.roll(0,totalBalls-1);
		
		for (PingPongBall curBall: lottery) {
			
			chosenNum -= curBall.getNumBalls();
			
			if (chosenNum <= 0) {
				return curBall.getType();
			}
			
		}
		
		return null;
		
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
		
	}
	
}
