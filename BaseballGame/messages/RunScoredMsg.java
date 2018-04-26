package messages;
import main.Baserunner;

public class RunScoredMsg extends Message {
	
	public Baserunner scorer;
	
	public RunScoredMsg(Baserunner scorer) {
		super();
		this.scorer = scorer;
		
	}
	
	public String toString () {
		return scorer + " has scored a run.";
	}
	
}
