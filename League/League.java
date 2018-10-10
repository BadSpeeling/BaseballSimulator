import java.util.*;

import ID.Serialized;
import team.Team;

public class League extends Serialized {
	
	private Map <Integer, Team> teams;
	
	public League (int id) {
		
		super(id);
		teams = new HashMap <Integer, Team> ();
		
	}
	
	
	
}
