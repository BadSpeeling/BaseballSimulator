import java.util.*;

import FileSystem.LocalFile;
import ID.Serialized;
import team.Team;

public class League extends Serialized {
	
	private Map <Integer, Team> teams;
	
	public League (int id) {
		
		super(id);
		teams = new HashMap <Integer, Team> ();
		
	}
	
	public void addTeam (Team toAdd) {
		teams.put(toAdd.getID(), toAdd);
	}
	
	public void addTeam (int id, LocalFile fileDir) {
		
		//TODO
		
		
	}
	
}
