
public class PhysicsTest {

	public static void main (String [] args) {
		
		/*
		double x1 = 100;
		double y1 = 100;
		double x2 = 200;
		double y2 = 50;
		double z1 = 0;
		double z2 = 0;
		
		double angle = Physics.angleBetween(new Coordinate3D(x1,y1,z1), new Coordinate3D(x2,y2,z2));
		
		System.out.println(angle);
		*/
		
		BallInPlay hitBall = new BallInPlay (0,0,3,-.0855,.785,170.577);
		
		//Coordinate3D ball = Physics.calculateInitalVelo(152, .834, .785);
		
		/*
		System.out.println("Loc: " + hitBall.loc);
		System.out.println("Velocity: " + hitBall.velocity);
		*/
		
		double time = 0;
		
		while (hitBall.inMotion()) {
			hitBall.tick(null);
			time += Physics.tick;
			System.out.println("Loc: " + hitBall.loc);
			System.out.println("Velocity: " + hitBall.velocity);
		}
		
		System.out.println(hitBall.airDistance);
		System.out.println(Physics.calculateHorizontalDist(hitBall.loc));
		System.out.println(time);
		System.out.println(hitBall.airTime);
		
	}
	
}
