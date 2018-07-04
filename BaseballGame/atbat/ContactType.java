package atbat;

public enum ContactType {
	MISS,FOUL,TERRIBLE,POOR,BELOW_AVG,AVERAGE,ABOVE_AVG,GOOD,EXCELLENT;
	
	public int [] ballCountChange () {
				
		switch (this) {

			case TERRIBLE:
				int [] ret1 = {10,10,-15,15,5,-5,5,0,-5};
				return ret1;
			case POOR:
				int [] ret2 = {6,6,-3,3,4,-2,0,0,-3};
				return ret2;
			case BELOW_AVG:
				int [] ret3 = {2,2,-2,2,5,-2,0,2,0};
				return ret3;
			case AVERAGE:
				int [] ret4 = {-2,-2,2,-2,0,0,0,0,0};
				return ret4;
			case ABOVE_AVG:
				int [] ret7 = {-4,-6,2,-2,4,7,-2,1,1};
				return ret7;
			case GOOD:
				int [] ret5 = {-3,-4,6,-3,0,0,-3,2,3};
				return ret5;
			case EXCELLENT:
				int [] ret6 = {-5,-6,7,0,0,7,-10,4,4};
				return ret6;
			default:
				return null;
				
		}
		
	}
	
	public boolean notInPlay () {
		return this.equals(MISS) || this.equals(FOUL);
	}
	
}
