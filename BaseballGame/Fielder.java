//a player that trys to record outs
public class Fielder extends OnFieldObject {
	
	FieldingRatings fRats;
	GeneralRatings gRats;
	Coordinate3D lastLoc;
	int position; //number from 1-9, standard baseball numbering
	Coordinate3D destination; //the coordinate the fielder wants to get to
	FielderDecision action;
	boolean cutOffMan = false;
	boolean hasBall = false;
	String fullName;
	
	public Fielder (double x, double y, double z, GamePlayer player, int position, String name) {
		super(x,y,z);
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = position;
		fullName = name;
	}
	
	//determine what the fielder should do for a hitball
	public void decideInitAction (BallInPlay curBall) {
		
	}
	//TODO fielders have no regards to outfield walls
	//performs all actions a fielder needs to for a tick
	public void brain (BallInPlay curBall, Stadium stad, Coordinate3D landingSpot, GameLogger log) {
		
		double runSpeed = 22; //speed of player.  in ft/s
		
		//System.out.println(toGo);
		//System.out.println(this.loc);
		
		Coordinate3D toGo = null;
		double xDisplacement = 0;
		double yDisplacement = 0;
		double angleToSpot = 0;
		
		//if (curBall.inAir.equals(BallStatus.IN_AIR)) {
		if (true) {
			//outfielders
			if (this.position >= 7) {
				//calculate displacement
				toGo = landingSpot.diff(this.loc); //vector pointing in direction player needs to run
			
			}
			
			//third base
			else if (this.position == 5) {
				toGo = new Coordinate3D (0, 90, 0).diff(this.loc);
			}
			
			//first base
			else if (this.position == 3) {
				toGo = new Coordinate3D (90, 0, 0).diff(this.loc);
			}
			
			//catcher
			else if (this.position == 2) {
				toGo = new Coordinate3D (0,0,0).diff(this.loc);
			}
			
			//2nd base
			else if (this.position == 4) {
				
				//hit to right field, is cutoff man
				if (Physics.radsToDegrees(curBall.launchDir) <= 45) {
					this.cutOffMan = true;
					Coordinate3D cutOffLoc = new Coordinate3D (130,90,0);
					toGo = cutOffLoc.diff(this.loc);
					log.add(GameEvent.becameCutoffMan(this.fullName, cutOffLoc));
				}
				
				//guard second
				else {
					toGo = new Coordinate3D (90,90,0).diff(this.loc);
				}
				
			}
			
		}
		
		else if (curBall.state.equals(BallStatus.ON_GROUND)) {
			
		}
		
		//the player does not need to move if they are within a half foot of the target location 
		if (Physics.calcPythag(toGo.x, toGo.y) > .5) {
			angleToSpot = Physics.angleFromXAxis(toGo);
			yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
			xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;
			
			//move the player
			lastLoc.x = this.loc.x;
			lastLoc.y = this.loc.y;
			this.loc.add(xDisplacement, yDisplacement, 0);
		}
		
		if (Physics.groundDistanceBetween(this.loc, curBall.loc) < 1) {
			curBall.grabBall(this);
			
			if (curBall.state.equals(BallStatus.IN_AIR)) {
				log.add(GameEvent.caughtFlyBall(fullName, loc));
				curBall.state = BallStatus.DEAD;
			}
			
			else {
				log.add(GameEvent.fieldedBall(fullName, loc));
				this.hasBall = true;
				curBall.state = BallStatus.FIELDED;
			}
				
		} 
			
	}
	
}
