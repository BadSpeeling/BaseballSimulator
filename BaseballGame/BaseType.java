
public enum BaseType {
	FIRST,SECOND,THIRD,HOME,NONE,CUTOFF;

	public BaseType nextBase () {

		switch (this) {
		case FIRST:
			return SECOND;
		case SECOND:
			return THIRD;
		case THIRD:
			return HOME;
		case HOME:
			return FIRST;
		default:
			return null;
		}

	}

	public int num () {
		switch (this) {
		case FIRST:
			return 0;
		case SECOND:
			return 1;
		case THIRD:
			return 2;
		case NONE:
			return -1;
		case HOME:
			return 3;
		default:
			return -1;
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
		case HOME:
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
