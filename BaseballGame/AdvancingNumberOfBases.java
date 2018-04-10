public class AdvancingNumberOfBases extends Message{
	
	private int numBases;

	public AdvancingNumberOfBases (int numBases) {
		super();
		this.numBases = numBases;
	}

	public int getNumBases() {
		return numBases;
	}

	public void setNumBases(int numBases) {
		this.numBases = numBases;
	}
	
}