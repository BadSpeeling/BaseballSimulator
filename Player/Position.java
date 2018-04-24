/* Eric Frye
 * Position enum represents the positions in a baseball diamond.  Starters and relievers are grouped int one category - Pitcher
 * */

public enum Position {
	PITCHER,CATCHER,FIRST,SECOND,THIRD,SHORT,LEFT,CENTER,RIGHT;
	
	
	/*
	 * Returns the enum of the position
	 * pos: numerical value of position to return
	 */
	public static Position getValue (int pos) {
		
		switch (pos) {
			
			case 1: return Position.PITCHER;
			case 2: return Position.CATCHER;
			case 3: return Position.FIRST;
			case 4: return Position.SECOND;
			case 5: return Position.THIRD;
			case 6: return Position.SHORT;
			case 7: return Position.LEFT;
			case 8: return Position.CENTER;
			case 9: return Position.RIGHT;
			default: return null;
		
		}
			
	}
	
	//true if the player is an outfielder
	public boolean isOutField () {
		return this.equals(LEFT) || this.equals(CENTER) || this.equals(RIGHT);
	}
	
	/* 
	 * Returns array of Positions enums passed on numerical positions
	 * poss: array of numerical positions to get Position enum of
	 * */
	public static Position [] getAllEnums (int [] poss) {
		
		Position [] toRet = new Position [poss.length];
		
		for (int i = 0; i < poss.length; i++) {
			toRet[i] = getValue(poss[i]);
		}
		
		return toRet;
		
	}
	
}
