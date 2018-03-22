
public class GameEvent {
	
	static String becameCutoffMan (String pName, Coordinate3D loc) {
		return pName + " is the cutoff man located at " + loc;
	}
	
	static String caughtFlyBall (String pName, Coordinate3D loc) {
		return pName + " caught a fly ball at " + loc;
	}
	
	static String fieldedBall (String pName, Coordinate3D loc) {
		return pName + " fielded a ball at " + loc;
	}
	
}
