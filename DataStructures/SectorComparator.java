import java.util.Comparator;

public class SectorComparator implements Comparator {

	public int compare(Object s1, Object s2) {
		return SectorT.tier((SectorT)s1) - SectorT.tier((SectorT)s2);
	}

	
}
