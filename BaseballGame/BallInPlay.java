
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

//a batted ball that obeys basic laws of physics.  measurments in feet and seconds
public class BallInPlay extends OnFieldObject {

	final double launchSpeed;
	final double launchAngle; //angle the ball was hit wrt ground
	final double launchDir; //xy plane direction
	Coordinate3D velocity; //velocity of ball in 3 directions
	Coordinate3D lastLoc; //last spot of ball. used to clear out the graphics
	BallStatus state = BallStatus.IN_AIR; //if the ball is in the air.  if true subject to air resistance.  if false, subject to friction with field
	double airDistance;
	double airTime;
	boolean canRecordOut = true;
	InPlayType type;
	LinkedList <Coordinate3D> allVals;
	List <LocationTracker> tracker; 
	
	//TODO add a tracker that remembers previous locations and time of locations of the ball.  to be used for finding the best route to take.
	
	public BallInPlay (Coordinate3D loc, double launchAngle, double launchDir, double launchSpeed, Stadium stad) {
		super(loc);
		this.launchSpeed = launchSpeed;
		this.launchAngle = launchAngle;
		this.launchDir = launchDir;
		this.velocity = Physics.calculateInitalVelo(launchSpeed, launchAngle, launchDir);
		this.lastLoc = new Coordinate3D(0,0,0);

		//determine what kind of ball was hit
		if (launchSpeed <= 20) {
			type = InPlayType.BUNT;
		}

		else if (Physics.radsToDegrees(launchAngle) <= 10) {
			type = InPlayType.GROUNDER;
		}

		else if (Physics.radsToDegrees(launchAngle) <= 20) {
			type = InPlayType.LINER;
		}

		else {
			type = InPlayType.FLYBALL;
		}

		//loads all the walls that the ball could hit
		allVals = new LinkedList <Coordinate3D> ();
		allVals.add(stad.dimCoors.get("l"));
		allVals.add(stad.dimCoors.get("lc"));
		allVals.add(stad.dimCoors.get("c"));
		allVals.add(stad.dimCoors.get("rc"));
		allVals.add(stad.dimCoors.get("r"));

	}

	public BallInPlay (BallInPlay copy) {

		super(new Coordinate3D (copy.loc.x, copy.loc.y, copy.loc.z));
		this.launchSpeed = copy.launchSpeed;
		this.launchAngle = copy.launchAngle;
		this.launchDir = copy.launchDir;
		this.velocity = new Coordinate3D (copy.velocity.x, copy.velocity.y, copy.velocity.z);
		this.lastLoc = new Coordinate3D (copy.lastLoc.x, copy.lastLoc.y, copy.lastLoc.z);
		this.state = copy.state;
		this.allVals = copy.allVals;

	}

	//returns a BallInPlay that's loc is either the place it makes contact with ground or last resting spot
	public BallInPlay modelBallDistance (Stadium stad, boolean inAir) {
		
		if (inAir) {
			BallInPlay copy = new BallInPlay (this);
	
			do {
				copy.tick(stad, true, null);
			} while (copy.canRecordOut);
	
			return copy;
		}
		
		else {
			
			BallInPlay copy = new BallInPlay (this);
			copy.tracker = new LinkedList <LocationTracker> ();
			
			do {
				copy.tick(stad, true, null);
			} while (copy.canRecordOut);
			
			double time = 0;
			final int split = 10; //time in between recordings
			int ctr = 0;
			
			do {
				
				copy.tick(stad, true, null);
				time += Physics.tick;
				ctr++;
								
				if (ctr % split == 0) {
					copy.tracker.add(new LocationTracker(copy.loc, time));
				}
				
			} while (copy.inMotion());
			
			return copy;
		}
			
	}

	//batted: true if ball is hit by a bat, false if the ball is thrown by a fielder
	public void tick (Stadium stad, boolean batted, FieldEvent status) {
		
		//controls a ball that is not being thrown by fielders
		if (batted) {
			//deals with colliding with floor
			Physics.handleGroundCollision(this);
	
			if (canRecordOut) {
				airTime += Physics.tick;
			}
	
			else {
				//System.out.println(airTime);
			}
	
			//this can be improved.  we clip into slack slightly, but it should never go through wall unless the tick is very high
			int res = Physics.handleCollision(allVals, loc);
			//handleCollision(stad.dimCoors);
			
			if (res == 1) {
				this.velocity.y *= -1;
				this.loc.y -= Physics.slack*2; 
				canRecordOut = false;
			}
	
			else if (res == 2) {
				this.velocity.x *= -1;
				this.loc.x -= Physics.slack*2;
				canRecordOut = false;
			}
	
			Coordinate3D newPos = Physics.tickPos(loc, velocity);
	
			Coordinate3D accl = Physics.calcAccel(this);
			Coordinate3D newVelo = Physics.tickVelo(velocity, accl);
	
			if (Math.abs(newVelo.x) < .005) {
				newVelo.x = 0;
			}
	
			if (Math.abs(newVelo.y) < .005) {
				newVelo.y = 0;
			}
	
			lastLoc.x = loc.x;
			lastLoc.y = loc.y;
			loc = newPos;
			velocity = newVelo;
	
			//System.out.println(velocity);
			//System.out.println(loc);
		}
		
		//ball being thrown by fielders
		else {
			Coordinate3D newPos = Physics.tickPos(loc, velocity);
			lastLoc = loc;
			loc = newPos;
		}
		
	}

	//true if the ball is still in motion
	public boolean inMotion () {
		return velocity.x != 0 || velocity.y != 0 || velocity.z != 0;
	}

	//set the balls velocity to zero.  return true if the ball was successfully picked up
	public boolean grabBall (Fielder fielder) {

		velocity.x = 0;
		velocity.y = 0;
		velocity.z = 0;
		return true;

	}
	
	public void throwBall (Fielder thrower, Coordinate3D target) {
		
		System.out.println(target);
		
		double throwSpeed = 100; //in ft/s
		Coordinate3D spot = target.diff(thrower.loc);
		double angle = Physics.angleFromXAxis(spot);
		velocity = new Coordinate3D(throwSpeed*Math.cos(angle), throwSpeed*Math.sin(angle), 0);
		
		state = BallStatus.THROWN;
		
	}

	//handles a wall collision
	//can be solved by making sure the ball is within the x coordiantes of the end points
	public void handleCollision (HashMap <String, Coordinate3D> dims) {

		double slack = .5;  //how close the ball must be to the wall for it to count as a collision

		LinkedList <Coordinate3D> allVals = new LinkedList <Coordinate3D> ();
		allVals.add(dims.get("l"));
		allVals.add(dims.get("lc"));
		allVals.add(dims.get("c"));
		allVals.add(dims.get("rc"));
		allVals.add(dims.get("r"));

		//iterate over all connecting sets of walls
		for (int i = 0; i < allVals.size()-1; i++) {

			Coordinate3D p1 = allVals.get(i);
			Coordinate3D p2 = allVals.get(i+1);

			//check if ball is reasonably close to wall
			if (Physics.calcPythag(loc.x-p1.x, loc.y-p1.y) <= 200 && (loc.x > p1.x && loc.x < p2.x)) {

				double m = calculateSlope(p1.x, p1.y, p2.x, p2.y);
				double targetY = 0;

				if (p1.x < p2.x)
					targetY = m*(loc.x-p1.x)+p1.y;
				else {
					targetY = p2.y+m*(loc.x-p2.x);
				}				

				//we have collided with the wall {p1,p2}.  we will now flip the velocity and leave function. walls are 10 feet high
				if (Math.abs(targetY-loc.y) < slack && loc.z < 10) {
					/*
					System.out.println("Wall " + (i+1) + " ");
					System.out.println(loc.x);
					System.out.println(loc.y);
					System.out.println(p1.x);
					System.out.println(p1.y);
					System.out.println(targetY);
					System.out.println((loc.x-p1.x));
					System.out.println(m);
					 */

					if (Math.abs(m) < 1) {
						velocity.y *= -1;
					}

					else {
						velocity.x *= -1;
					}

					return;

				}

			}

		}

	}

	//calculates slope
	private double calculateSlope (double x1, double y1, double x2, double y2) {
		/*System.out.println(y2);
			System.out.println(x2);
			System.out.println(y1);
			System.out.println(x1);
			System.out.println();*/
		return (y2-y1)/(x2-x1);
	}

	
}

//tracks the location of the ball after the initial bounce.  to be used to determine the optimal way to play the ball 
class LocationTracker {
		
		Coordinate3D loc; //location of the ball
		double time; //time 
		
		public LocationTracker (Coordinate3D loc, double time) {
			this.loc = loc;
			this.time = time;
		}
		
}
