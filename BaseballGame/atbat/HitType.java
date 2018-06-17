package atbat;

import numbers.PercentileConverter;
import numbers.RandomNumber;
import physics.Physics;

public enum HitType {
	DRIBBLER,WEAKGROUNDER,SOLIDGROUNDER,FLARE,SOFTLINER,HARDLINER,POPUP,FLYBALL,DEEPFLYBALL,FOUL;
	
	public double baseHitSpeed () {
				
		switch (this) {
		case DRIBBLER:
			return PercentileConverter.getValue(80, 5);
		case WEAKGROUNDER:
			return PercentileConverter.getValue(100, 10);
		case SOLIDGROUNDER:
			return PercentileConverter.getValue(130, 10);
		case FLARE:
			return PercentileConverter.getValue(90, 5);
		case SOFTLINER:
			return PercentileConverter.getValue(110, 10);
		case HARDLINER:
			return PercentileConverter.getValue(150, 5);
		case POPUP:
			return PercentileConverter.getValue(90, 8);
		case FLYBALL:
			return PercentileConverter.getValue(120, 12);
		case DEEPFLYBALL:
			return PercentileConverter.getValue(155, 12);
		default:
			return -1;
		}
	}
	
	
	//returns in radians
	public double launchAngle () {
		
		double ret = 0;
		
		switch (this) {
		case DRIBBLER:
			ret = PercentileConverter.getValue(0, 2);
			break;
		case WEAKGROUNDER:
			ret = PercentileConverter.getValue(0, 3);
			break;
		case SOLIDGROUNDER:
			ret = PercentileConverter.getValue(0, 2);
			break;
		case FLARE:
			ret = PercentileConverter.getValue(30, 3);
			break;
		case SOFTLINER:
			ret = PercentileConverter.getValue(15, 3);
			break;
		case HARDLINER:
			ret = PercentileConverter.getValue(15, 3);
			break;
		case POPUP:
			ret = PercentileConverter.getValue(40, 8);
			break;
		case FLYBALL:
			ret = PercentileConverter.getValue(30, 4);
			break;
		case DEEPFLYBALL:
			ret = PercentileConverter.getValue(30, 4);
			break;
		default:
			ret = -1;
		}
		
		return Physics.degreesToRads(ret);
		
	}
	
	//returns in radians
	public double launchDir () {
		return Physics.degreesToRads(RandomNumber.roll(0,90));
	}
	
}
