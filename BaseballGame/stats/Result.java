package stats;

//single,double,triple,homerun,walk,strikout,sacbunt,sacfly,fielders choice,intentional walk
public enum Result {
	OUT,S,D,T,HR,BB,K,SACB,SACF,FC,IBB;

	public String toString () {
		switch(this) {
		case OUT:
			return "out";
		case S:
			return "single";
		case D:
			return "double";
		case T:
			return "triple";
		case HR:
			return "homerun";
		case BB:
			return "walk";
		case K:
			return "strikeout";
		default:
			return "unimplemented";
		}
	}
	
	public boolean wasAHit () {
		switch(this) {
		case OUT:
			return false;
		case BB:
			return false;
		case K:
			return false;
		case SACB:
			return false;
		case SACF:
			return false;
		case FC:
			return false;
		case IBB:
			return false;
		default:
			return true;
		}
	}

}
