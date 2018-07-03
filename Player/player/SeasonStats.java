package player;

import stats.BattingStatline;
import stats.PitchingStatline;

public class SeasonStats {
	
	private int games = 0;
	private String year = "2018";
	private BattingStatline seasonBattingStats;
	private PitchingStatline seasonPitchingStats;
	
	public SeasonStats (int id) {
		seasonBattingStats = new BattingStatline (id);
		seasonPitchingStats = new PitchingStatline (id);
	}
	
	public void add (BattingStatline batting, PitchingStatline pitching) {
		
		
		
	}
	
}
