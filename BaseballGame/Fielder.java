//a player that trys to record outs
public class Fielder extends OnFieldObject {
	
	FieldingRatings fRats;
	GeneralRatings gRats;
	Coordinate3D lastLoc;
	int position; //number from 1-9, standard baseball numbering
	Coordinate3D destination; //the coordinate the fielder wants to get to
	FielderDecision action;
	
	public Fielder (double x, double y, double z, GamePlayer player, int position) {
		super(x,y,z);
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = position;
	}
	
	//determine what the fielder should do for a hitball
	public void decideInitAction (BallInPlay curBall) {
		
	}
	
	//performs all actions a fielder needs to for a tick
	public void brain (BallInPlay curBall, Stadium stad, Coordinate3D toGo) {
		
		double runSpeed = 22; //speed of player.  in ft/s
		Coordinate3D landingVector = toGo.diff(this.loc); //vector pointing in direction player needs to run
		
		//System.out.println(toGo);
		//System.out.println(this.loc);
		
		//calculate displacement
		double angleToSpot = Physics.angleFromXAxis(landingVector);
		double yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
		double xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;
		
		//move the player
		lastLoc.x = this.loc.x;
		lastLoc.y = this.loc.y;
		this.loc.add(xDisplacement, yDisplacement, 0);
		
	}
	
}
