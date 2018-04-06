
public enum Base {
	HOME,FIRST,SECOND,THIRD,NONE;

	public Base nextBase () {

		switch (this) {
		case FIRST:
			return SECOND;
		case SECOND:
			return THIRD;
		case THIRD:
			return HOME;
		case NONE:
			return FIRST;
		default:
			return null;
		}

	}

	//returns the next base a player should run to
	public Coordinate3D nextDestination () {

		switch (this) {
		case FIRST:
			return FieldConstants.secondBase();
		case SECOND:
			return FieldConstants.thirdBase();
		case THIRD:
			return FieldConstants.homePlate();
		case NONE:
			return FieldConstants.firstBase();
		default:
			return null;
		}

	}
	
	//gets the equivalent base
	public Coordinate3D equiv () {

		switch (this) {
		case SECOND:
			return FieldConstants.secondBase();
		case THIRD:
			return FieldConstants.thirdBase();
		case HOME:
			return FieldConstants.homePlate();
		case FIRST:
			return FieldConstants.firstBase();
		default:
			return null;
		}

	}

}
