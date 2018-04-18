import java.util.LinkedList;
import java.util.List;

//encapsulates pointers to the OnFieldPlayer that in doing something

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
	Fielder receivingBall = null;
	Fielder thrower = null;
	Fielder hasBall = null;
	Baserunner playerOut = null;
	
}