
public enum PitchType {
	FB,CH,CB;
	
	public static PitchType convert (String convert) {
		
		if (convert.equals("FB")) {
			return PitchType.FB;
		}
		
		else if (convert.equals("CH")) {
			return PitchType.CH;
		}
		
		else {
			return PitchType.CB;
		}
		
	}
	
}
