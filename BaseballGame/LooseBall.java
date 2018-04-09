//contains data about where the loose ball will travel to 

import java.util.List;

public class LooseBall extends Message {
	
	public LooseBall(List<LocationTracker> locs) {
		super();
		this.locs = locs;
	}

	public List<LocationTracker> getLocs() {
		return locs;
	}

	public void setLocs(List<LocationTracker> locs) {
		this.locs = locs;
	}

	private List <LocationTracker> locs; 
	
}
