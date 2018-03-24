import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//a player that trys to record outs
public class Fielder extends OnFieldObject {
	
	FieldingRatings fRats;
	GeneralRatings gRats;
	Coordinate3D lastLoc;
	Position position; //number from 1-9, standard baseball numbering
	Coordinate3D destination = null; //the coordinate the fielder wants to get to. should be null if the decision needs to be made
	FielderDecision action = FielderDecision.UNKNOWN;
	boolean hasBall = false;
	String fullName;
	LinkedList <Coordinate3D> dimensions; //TODO make static
	static List <Object> gameEvents = new LinkedList <Object> ();
	int lastEventRead = 0;
	
	
	public Fielder (Coordinate3D loc, GamePlayer player, LinkedList <Coordinate3D> dimensions) {
		super(loc);
		fRats = player.fRatings;
		gRats = player.gRatings;
		lastLoc = new Coordinate3D(0,0,0);
		this.position = player.pos;
		fullName = player.fullName();
		this.dimensions = dimensions;
	}
	
	//determine what the fielder should do for a hitball
	public void decideInitAction (BallInPlay curBall) {
		
	}
	
	//controls all decisions that a fielder needs to make
	public void brain (BallInPlay curBall, Stadium stad, Map <String, BallInPlay> model, GameLogger log, FieldEvent status) {
		
		double runSpeed = gRats.runSpeed(); //speed of player.  in ft/s
		
		//System.out.println(toGo);
		//System.out.println(this.loc);
		
		Coordinate3D toGo = null;
		double xDisplacement = 0;
		double yDisplacement = 0;
		double angleToSpot = 0;
		
		//if the player doesnt know where they should be running to, decide
		if (destination == null) {
						
			//determine where we want the player to run to
			if (curBall.type.equals(InPlayType.FLYBALL)) {
				
				if (this.position.equals(Position.CATCHER)) {
					destination = FieldConstants.homePlate;
					status.fOnHome = this;
				}
				
				else if (this.position.equals(Position.FIRST)) {
					destination = FieldConstants.firstBase;
					status.fOnFirst = this;
				}
				
				else if (this.position.equals(Position.SECOND)) {
					
					//cutoff
					if (Physics.radsToDegrees(curBall.launchDir) <= 45) {
						destination = FieldConstants.std2BCutoff;
						status.fCutoff = this;
					}
					
					else {
						destination = FieldConstants.secondBase;
						status.fOnSecond = this;
					}
				
				}
				
				else if (this.position.equals(Position.THIRD)) {
					destination = FieldConstants.thirdBase;
					status.fOnThird = this;
				}
				
				else if (this.position.equals(Position.SHORT)) {
					
					//cutoff
					if (Physics.radsToDegrees(curBall.launchDir) > 45) {
						destination = FieldConstants.stdSSCutoff;
						status.fCutoff = this;
					}
					
					else {
						destination = FieldConstants.secondBase;
						status.fOnSecond = this;
					}
					
				}
				
				else if (this.position.equals(Position.PITCHER)) {
					destination = FieldConstants.pitchersMound;
				}
				
				//outfielders
				else {
					
					if (canReachFlyBall(model.get("aM"))) {
						destination = model.get("aM").loc;
					}
					
					else {
						destination = closestSpot(model.get("fM").tracker, model.get("fM").airTime);
					}
					
				}
				
			}
			
			
			
		}
		
		toGo = this.destination.diff(this.loc);
		
		//the player does not need to move if they are within a half foot of the target location. also makes sure player is not colliding with a wall
		if (Physics.calcPythag(toGo.x, toGo.y) > .5 && Physics.handleCollision(dimensions, this.loc) == 0) {
			angleToSpot = Physics.angleFromXAxis(toGo);
			yDisplacement = runSpeed * Math.sin(angleToSpot) * Physics.tick;
			xDisplacement = runSpeed * Math.cos(angleToSpot) * Physics.tick;
			
			//move the player
			lastLoc.x = this.loc.x;
			lastLoc.y = this.loc.y;
			this.loc.add(xDisplacement, yDisplacement, 0);
		}
		
		//if player is close enough to ball, try to grab it
		if (Physics.distanceBetween(this.loc, curBall.loc) < 1) {
			curBall.grabBall(this);
			
			//out recorded
			if (curBall.canRecordOut) {
				log.add(GameEvent.caughtFlyBall(fullName, loc));
				curBall.state = BallStatus.DEAD;
			}
			
			//ball is picked up
			else {
							
				if (curBall.state.equals(BallStatus.THROWN)) {
					curBall.grabBall(this);
				}
				
				else {
					log.add(GameEvent.fieldedBall(fullName, loc));
					this.hasBall = true;
					curBall.state = BallStatus.FIELDED;
					curBall.throwBall(this, status.fOnSecond.loc);
					status.beingThrownTo = status.fOnSecond;
				}
					
			}
				
		} 
			
	}
	
	//true if the player can reach a fly ball
	private boolean canReachFlyBall (BallInPlay airModel) {
		
		Coordinate3D distanceRun = airModel.loc.diff(this.loc);
		double speed = gRats.runSpeed(); //running speed
		
		return distanceRun.mag() < (speed * airModel.airTime);
		
	}
	
	//returns the closest spot the fielder can get to given location, time and speed
	private Coordinate3D closestSpot (List <LocationTracker> locs, double airTime) {
		
		double speed = gRats.runSpeed();
		Coordinate3D ret = locs.get(locs.size()-1).loc; //if no ball is reachable in time, return the last one
		double bestSpaceTimeDist = 0;
		
		for (LocationTracker cur: locs) {
			
			double physicalDistance = Physics.distanceBetween(cur.loc,this.loc);
			
			if (position.equals(Position.CENTER)) {
				System.out.println(physicalDistance);
			}
			
			//the player can reach this ball in an appropriate amount of time
			if (speed * (cur.time + airTime) >= physicalDistance) {
				
				//finds the space time distance
				double spaceTimeDist = Physics.spaceTimeDistance(cur, this.loc, 0);
				
				if (spaceTimeDist > bestSpaceTimeDist) {
					bestSpaceTimeDist = spaceTimeDist;
					ret = cur.loc;
				}
				
			}
			
		}
		
		return ret;
		
	}
	
}
