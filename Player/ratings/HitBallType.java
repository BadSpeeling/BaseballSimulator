package ratings;

public enum HitBallType {
	
	//to be used to alter hitball types by a percentage
	HEAVY_GROUNDBALL,GROUND_BALL,NEUTRAL,FLYBALL,HEAVY_FLYBALL;
	
	//must pass in values [-2,2]
	public static HitBallType convert (int num) {
		
		if (num == -2) {
			return HEAVY_GROUNDBALL;
		}
		
		else if (num == -1) {
			return GROUND_BALL;
		}
		
		else if (num == 0) {
			return NEUTRAL;
		}
		
		else if (num == 1) {
			return FLYBALL;
		}
		
		else if (num == 2){
			return HEAVY_FLYBALL;
		}
		
		else {
			return null;
		}
		
	}
	
	
	public int num () {
		switch(this) {
		case HEAVY_GROUNDBALL: return -2;
		case GROUND_BALL: return -1;
		case NEUTRAL: return 0;
		case FLYBALL: return 1;
		case HEAVY_FLYBALL: return 2;
		default: return 0;
		}
	}
	
}
