import java.util.LinkedList;
import java.util.List;

//encapsulates data all fielders need to know about state of field

public class FieldEvent {
	
	int outs = 0;
	List <Fielder> fielders = new LinkedList <Fielder> ();
	Fielder fOnFirst = null;
	Fielder fOnSecond = null;
	Fielder fOnThird = null;
	Fielder fOnHome = null;
	Fielder fCutoff = null;
	Baserunner bOnFirst = null;
	Baserunner bOnSecond = null;
	Baserunner bOnThrid = null;
	Fielder beingThrownTo = null;
	Fielder pickingUpBall = null;
	Fielder thrower = null;
	Fielder hasBall = null;
	boolean reModelBall = false;
	boolean newFielderDecisions = true;
	boolean newBaserunnerDecisions = true;
	int basesAttempt = 0; //stores the amount of bases the batter will attempt to take
	
}