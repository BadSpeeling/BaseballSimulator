package ratings;

public class Modifier {
	
	private ModifierType type;
	private double mean; //the mean for this rating
	private double changeByZScore;
	
	public Modifier (int id, double mean, double changeByZScore) {
		this.mean = mean;
		this.changeByZScore = changeByZScore;
		this.type = getType(id);
	}
	
	private ModifierType getType (int id) {
		switch (id) {
			case 0:
				return ModifierType.CONTACT;
			case 1:
				return ModifierType.DISCIPLINE;
			case 2:
				return ModifierType.POWER;
			case 3:
				return ModifierType.CONTROL;
			case 4:
				return ModifierType.VELOCITY;
			default:
				return null;
		}
	}
	
}
