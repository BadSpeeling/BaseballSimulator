package objects;

public enum BaserunnerStatus {
	RunningToBase,OnABase,StoppedOnBasePaths,Init;
	
	public boolean isOnBase () {
		return this == BaserunnerStatus.OnABase;
	}
	
}
