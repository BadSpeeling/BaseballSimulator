import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class GUITest {

	public static void main (String [] args) {

		Runner cur = new Runner ();
		new Thread(cur).start();
		
		
	}

}

class Runner implements Runnable {
	
	private final BlockingQueue<Void> pause = new ArrayBlockingQueue<Void>(1);

	public void test() throws InterruptedException {
	    pause.poll(100 * 300, TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void run() {

		int playersOnTeam = 9;
		int [] rules = {9,0,0,25};

		Team home = new Team ();
		home.addFakePlayers(playersOnTeam);

		Team away = new Team ();
		away.addFakePlayers(playersOnTeam);

		home.printTeam();
		System.out.println();
		away.printTeam();

		GameTeam homeTeam = home.makeInGameTeam(true);
		GameTeam awayTeam = away.makeInGameTeam(true);

		RuleSet ruleSet = new RuleSet (rules);
		ruleSet.numInnings = 3;

		Scanner input = null;
		try {
			input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Stadium cur = new Stadium ();
		cur.loadDimensions(input);

		Game g = new Game (ruleSet, 1, homeTeam, awayTeam, cur);

		BallInPlay hitBall = new BallInPlay (0,0,3,.3,Math.PI/4+.02,159.23);
		
		g.liveBallDriver(null, hitBall);

	}

}
