package game;
import java.util.LinkedList;
import java.util.List;

public class GameLogger {
	
	private List <String> events = new LinkedList <String> ();
	
	public void add (String event) {
		events.add(event);
	}
	
}
