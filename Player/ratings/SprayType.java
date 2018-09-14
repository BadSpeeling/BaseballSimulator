package ratings;

public enum SprayType {
	OPPOSITE,NEUTRAL,PULL,HEAVY_PULL;
	
	public static SprayType convert (int num) {
		
		if (num == -1) {
			return OPPOSITE;
		}
		
		else if (num == 0) {
			return NEUTRAL;
		}
		
		else if (num == 1) {
			return PULL;
		}
		
		else if (num == 2) {
			return HEAVY_PULL;
		}
		
		else {
			return null;
		}
		
	}
	
	public int num () {
		switch (this) {
		case OPPOSITE: return -1;
		case NEUTRAL: return 0;
		case PULL: return 1;
		case HEAVY_PULL: return 2;
		default: return 0;
		}
	}
	
}
