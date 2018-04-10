import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Baserunner extends OnFieldPlayer {
	
	Queue <Coordinate3D> destinations;
	Coordinate3D destination = null;
	Coordinate3D lastLoc = new Coordinate3D (0,0,0);
	BaseType baseOn = BaseType.NONE;
	FieldEvent status;
	GameLogger log;
	Base attempt;
	
	Baserunner (FieldEvent status, GameLogger log, GeneralRatings gRatings, String fName) {
		super (FieldConstants.homePlate(), gRatings, fName, new LinkedList <LocationTracker> ());
		destinations = new LinkedList <Coordinate3D> ();
		this.fName = fName;
		this.status = status;
		this.log = log;
	}
	
	public Baserunner (GamePlayer other,FieldEvent status, GameLogger log) {
		super(FieldConstants.homePlate(), other.gRatings, other.fullName(), new LinkedList <LocationTracker> ());
		destinations = new LinkedList <Coordinate3D> ();
		this.status = status;
		this.log = log;
	}
	
	public void baserunnerBrain (int basesTake) {
		
		BaseType temp = baseOn;
		
		for (int i = 0; i < basesTake; i++) {
			destinations.add(temp.nextDestination());
			temp = temp.nextBase();
		}
		
	}
	
	public void setBaseOn (BaseType set) {
		baseOn = set;
		loc = baseOn.equiv();
	}
	
	//determines which base the batter can get to
	public void batterBaseBrain (Map <String, BallInPlay> models, List <Fielder> fielders, BallInPlay curBall) {
		
		int basesTake = 0;
		
		if (curBall.type.equals(InPlayType.FLYBALL)) {
			
			//find the time it will take for the ball to be fielded
			double timeTaken = Double.MAX_VALUE;
			
			for (int i = Game.LEFTNUM; i < Game.RIGHTNUM; i++) {
				
				double ret = fielders.get(i).timeToBall(models);
				timeTaken = Math.min(timeTaken, ret);
				
			}
			
			double distanceRunnerCanCover = timeTaken * gRats.runSpeed();
			
			//determine what base to run to
			if (distanceRunnerCanCover < 75) {
				basesTake = 1;
			}
			
			else if (distanceRunnerCanCover < 140) {
				basesTake = 2;
			}
			
			else {
				basesTake = 3;
			}
			
		}
		
		//baserunnerBrain(status,log,basesTake);
		OnFieldPlayer.messages.add(new AdvancingNumberOfBases(basesTake));
		//status.newBaserunnerDecisions = false;
				
	}
	
	//run to the destination, clear for next destination if reached
	public void run (List <Base> bases) {
		
		if (destination == null && !this.destinations.isEmpty()) {
			this.destination = this.destinations.poll();
			
			//get pointer to base attemping
			int baseGoingTo = baseOn.nextBase().num();
			attempt = bases.get(baseGoingTo);
			attempt.setAdvanceing(this);
			
			log.add(GameEvent.runToBase(fName, this.destination.toString()));
		}
		
		if (destination != null) {
			Coordinate3D toGo = this.destination.diff(this.loc);
					
			//not close enough to destination, keep on going
			if (toGo.mag() > 1) {
				double angle = Physics.angleFromXAxis(toGo);
				double speed = gRats.runSpeed();
							
				this.lastLoc.x = this.loc.x;
				this.lastLoc.y = this.loc.y;
				
				this.loc.x += Physics.tick*speed*Math.cos(angle);
				this.loc.y += Physics.tick*speed*Math.sin(angle);
			}
			
			//clear the destination
			else {
				attempt.arriveAtBase(this);
				if (destination.equals(FieldConstants.firstBase())) {baseOn = BaseType.FIRST;}
				else if (destination.equals(FieldConstants.secondBase())) {baseOn = BaseType.SECOND;}
				else if (destination.equals(FieldConstants.thirdBase())) {baseOn = BaseType.THIRD;} 
				else {baseOn = BaseType.HOME;}
				destination = null; 
			}
			
		}
		
	}
	
	//gives the next base in order
	public Coordinate3D nextBase (Coordinate3D at) {
		
		if (at.equals(FieldConstants.firstBase())) {
			return FieldConstants.secondBase();
		}
		
		else if (at.equals(FieldConstants.secondBase())) {
			return FieldConstants.thirdBase();
		}
		
		else {
			return FieldConstants.homePlate();
		}
		
	}
	
}
