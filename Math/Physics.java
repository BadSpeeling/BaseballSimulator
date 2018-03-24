import java.util.HashMap;
import java.util.LinkedList;

/* Physics handles all needed physics calculations
 * */


public class Physics {

	private final static double degrees90 = Math.PI/2;
	private final static double degrees180 = Math.PI;
	private final static double degrees270 = 3*Math.PI/2;
	public final static double accelGrav = 32.2; //ft/s^2
	private final static double mass = .32; //lbs
	public final static double tick = (double)1/360; //seconds
	private final static double dragCoef = .3; //unitless
	private final static double surfaceArea = 0.045; //ft^2
	private final static double airdensity = 0.0765; //lb/ft^3
	private final static double mu = .8; //unitless.  this number is unrealistically high to account for other slowing factors that are too complex
	public final static double slack = .5; //how close an object can get to a wall. ft
	
	/* Calculates the angle a line makes with the x axis
	 * from - first point
	 * to - second point 
	 * */
	public static double angleBetween (Coordinate3D from, Coordinate3D to) {
		
		double y = to.y-from.y;
		double x = to.x-from.x;
		double z = to.z-from.z;
		
		return angleFromXAxis(new Coordinate3D(x,y,z));
	}
	
	public static double radsToDegrees (double rad) {
		return rad / Math.PI * 180;
	}
	
	public static double degreesToRads (double deg) {
		return deg * Math.PI / 180;
	}

	public static double calculateBounceAngleWithGround (BallInPlay inPlay) {

		double magGroundVelo = calcPythag(inPlay.velocity.x, inPlay.velocity.y);
		return Math.atan(inPlay.velocity.z/magGroundVelo);

	}

	public static double calcPythag (double a, double b) {
		return Math.sqrt(Math.pow(a, 2.0) + Math.pow(b, 2.0));
	}

	//performs all operations pertaining to checking if a ball has collided with the ground.  velocity updated oppropiatley
	public static void handleGroundCollision (BallInPlay ball) {

		//only need to do work if the ball has hit the ground
		if (goingToHitGround(ball)) {
			
			ball.canRecordOut = false;
			
			//the ball will stop bouncing now
			if (ball.state.equals(BallStatus.IN_AIR) && Math.abs(ball.velocity.z) < .2) {
				ball.velocity.z = 0;
				ball.loc.z = .75;
				ball.state = BallStatus.ON_GROUND;
				ball.airDistance = calcPythag(ball.loc.x,ball.loc.y);
			}

			ball.velocity.z = ball.velocity.z/-5;

		}

	}
	
	//computes the space-time distance 
	public static double spaceTimeDistance (LocationTracker trck, Coordinate3D loc, double time) {
		return Math.sqrt(Math.pow(trck.time-time, 2.0) + Math.pow(trck.loc.x-loc.x, 2.0) + Math.pow(trck.loc.y-loc.y, 2.0) + Math.pow(trck.loc.z-loc.z, 2.0));
	}
	
	//calculates slope
	private static double calculateSlope (double x1, double y1, double x2, double y2) {
			/*System.out.println(y2);
			System.out.println(x2);
			System.out.println(y1);
			System.out.println(x1);
			System.out.println();*/
			return (y2-y1)/(x2-x1);
	}
	
	//generic collision handler. dest is the location the OnFieldObject would like to get to
	//returns: 0 if not change in direction
	//0 no collision
	//1 if flip y direction
	//2 if flip x direction
	public static int handleCollision (LinkedList <Coordinate3D> allVals, Coordinate3D dest) {
		
		//double slack = .5;  //how close the ball must be to the wall for it to count as a collision

		//iterate over all connecting sets of walls
		for (int i = 0; i < allVals.size()-1; i++) {
			
			Coordinate3D p1 = allVals.get(i);
			Coordinate3D p2 = allVals.get(i+1);
						
			//check if ball is reasonably close to wall
			if (Physics.calcPythag(dest.x-p1.x, dest.y-p1.y) <= 200 && (dest.x > p1.x && dest.x < p2.x)) {
								
				double m = calculateSlope(p1.x, p1.y, p2.x, p2.y);
				double targetY = 0;
				
				if (p1.x < p2.x)
					targetY = m*(dest.x-p1.x)+p1.y;
				else {
					targetY = p2.y+m*(dest.x-p2.x);
				}				
				
				//we have collided with the wall {p1,p2}.  we will now flip the velocity and leave function. walls are 10 feet high
				if (Math.abs(targetY-dest.y) < slack && dest.z < 10) {
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
						return 1;
					}
					
					else {
						return 2;
					}
									
				}
				
			}
			
		}
		
		return 0;
		
	}

	//true if the coordinate is hitting the ground. the ball must end up within an inch of hitting the ground to be considered a collision
	public static boolean goingToHitGround (BallInPlay ball) {
		return ball.velocity.z*tick+ball.loc.z <= (double)1/12;
	}

	//returns a new Coordinate3D that has been dispalced by curVelo*tick amount
	public static Coordinate3D tickPos (Coordinate3D curPos, Coordinate3D curVelo) {
		return new Coordinate3D (curPos.x+curVelo.x*tick, curPos.y+curVelo.y*tick, curPos.z+curVelo.z*tick);
	}

	//returns a new Coordinate3D corresponding to a new velocity changed by curAccl*tick
	public static Coordinate3D tickVelo (Coordinate3D curVelo, Coordinate3D curAccl) {
		return new Coordinate3D (curVelo.x+curAccl.x*tick, curVelo.y+curAccl.y*tick, curVelo.z+curAccl.z*tick);
	}
	
	//the angle made with respect to the x axis
	public static double angleFromXAxis (Coordinate3D velo) {
		
		double angle = 0;
		
		if (velo.x != 0) {
			angle = Math.atan(velo.y/velo.x);
		}
			
		if (velo.x > 0 && velo.y > 0) {
			return angle;
		}
		
		else if (velo.x < 0 && velo.y < 0) {
			return angle + degrees180;
		}
		
		else if (velo.x > 0 && velo.y < 0) {
			return angle;
		}
		
		else if (velo.x < 0 && velo.y > 0){
			return angle + degrees180;
		}
		
		else if (velo.x == 0 && velo.y > 0) {
			return degrees90;
		}
		
		else if (velo.x == 0 && velo.y < 0) {
			return degrees270;
		}
		
		else if (velo.x < 0 && velo.y == 0) {
			return degrees180;
		}
		
		else {
			return 0;
		}
		
	}

	//calculates the resistant force of an individual component
	//velo - the velocity of the given component
	public static double calculateAirResistanceAccl (double velo) {

		if (velo > 0)
			return -1*airdensity*dragCoef*surfaceArea*.5*Math.pow(velo, 2.0)/mass;
		else 
			return airdensity*dragCoef*surfaceArea*.5*Math.pow(velo, 2.0)/mass;
	}

	//calculates frictional acceleration.  if returns 0, then ball should stop moving
	public static double calculateFrictionAccl (double velo) {

		//if the ball is moving less than 1/20th of foot a second, we assume stopping of motion
		if (Math.abs(velo) <= .1 && Math.abs(velo) >= 0) {
			return 0;
		}

		return mu*accelGrav;
		
	}

	//testing method to see how far a baseball will fly
	public static double calculateHorizontalDist (Coordinate3D check) {
		return Math.sqrt(Math.pow(check.x, 2.0) + Math.pow(check.y, 2.0));
	}

	/* calculates the initial velocity of the three components of a hit baseball. to be called when a BallInPlay is created
	 * hitSpeed - how fast the ball is hit (ft/s)
	 * launchAgle - angle made with z-axis (rad)
	 * hitDir - angle made with 1st base line (rad)
	 * */
	public static Coordinate3D calculateInitalVelo (double hitSpeed, double launchAngle, double hitDir) {

		double zVelo = hitSpeed*Math.sin(launchAngle);
		double groundVelo = hitSpeed*Math.cos(launchAngle);

		double xVelo = groundVelo*Math.cos(hitDir);
		double yVelo = groundVelo*Math.sin(hitDir);

		return new Coordinate3D (xVelo, yVelo, zVelo);

	}
	
	//finds the euclidean dist in 2d plane
	public static double groundDistanceBetween (Coordinate3D one, Coordinate3D two) {
		return Math.sqrt(Math.pow((one.x-two.x), 2.0) + Math.pow(one.y-two.y, 2.0));
	}
	
	//find euclidean distance in 3d plane
	public static double distanceBetween (Coordinate3D one, Coordinate3D two) {
		return Math.sqrt(Math.pow((one.x-two.x), 2.0) + Math.pow(one.y-two.y, 2.0) + Math.pow(one.z-two.z,2.0));
	}

	//calculates the distance of a homerun
	public static double basicEstimateHRDistance (double time, double groundVelo) {
		return time * groundVelo;
	}

	//special case of kinematics equation where distance displacement is 0
	public static double calcTime (double initialVelo, double acceleration) {
		return initialVelo/(acceleration/2);
	}
	
	private static double dotProduct (Coordinate3D p1, Coordinate3D p2) {
		return p1.x*p2.x+p1.y*p2.y;
	}

	//calculates the resulting acceleration due to current forces
	public static Coordinate3D calcAccel (BallInPlay ball) {

		double xAccel = 0;
		double yAccel = 0;
		double zAccel = 0;

		//in air
		if (ball.state.equals(BallStatus.IN_AIR)) {

			xAccel = calculateAirResistanceAccl(ball.velocity.x);
			yAccel = calculateAirResistanceAccl(ball.velocity.y);
			zAccel = calculateAirResistanceAccl(ball.velocity.z) - accelGrav;

		}

		//ball on ground
		else {

			//ball under influence of friction
			double fricAccl = calculateFrictionAccl(calcPythag(ball.velocity.x, ball.velocity.y));
			
			//System.out.println(fricAccl);
			
			if (fricAccl == 0) {
				ball.velocity.x = 0;
				ball.velocity.y = 0;
			}

			else {
								
				double angle = angleFromXAxis(ball.velocity) + degrees180;
				
				//System.out.println(angle);
				
				xAccel = fricAccl * Math.cos(angle);
				yAccel = fricAccl * Math.sin(angle);
				zAccel = 0;
				
			}

		}

		return new Coordinate3D (xAccel, yAccel, zAccel);

	}

}
