/* PitchRatings is a type a ball that can be thrown at a batter
 * To be used in maps that have the key be PitchType
 * */
class PitchRatings {
	
	PitchType name;
	int maxVelo;
	int minVelo;
	int stuff;
	int control;
	
	public PitchRatings (int max, int min, int stuff, int control) {
		maxVelo = max;
		minVelo = min;
		this.stuff = stuff;
		this.control = control;
	}
	
}
