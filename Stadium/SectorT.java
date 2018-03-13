public enum SectorT {
	INPLAY,HR,FOUL,NONE,BASELINE,BASE;
	
	public static int tier (SectorT check) {
		
		//dimension
		if (check.equals(INPLAY) || check.equals(HR) || check.equals(FOUL)) {
			return 1;
		}
		
		//background color
		else if (check.equals(BASELINE) || check.equals(BASE)) {
			return 2;
		}
		
		//moving obj
		else {
			return 3;
		}
		
	} 
	
}
