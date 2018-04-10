public class CaughtBall extends Message {
	
	private Fielder ballCatcher;

	public Fielder getBallCatcher() {
		return ballCatcher;
	}

	public void setBallCatcher(Fielder ballCatcher) {
		this.ballCatcher = ballCatcher;
	}

	public CaughtBall(Fielder ballCatcher) {
		super();
		this.ballCatcher = ballCatcher;
	}
	
}
