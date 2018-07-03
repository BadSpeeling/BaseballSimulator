import java.io.IOException;

import player.Generators;
import player.Player;
import utility.Writer;

public class PlayerSavingTest {
	
	public static void main (String [] args) {
		
		Player [] players = Generators.basicPlayerGenerator(100);
		String metaData [][] = {{"nextID","101"}};
		Writer writer = new Writer ("D:\\Java_Projects\\BaseballSimulator\\SavedData", "testPlayers.txt", metaData, false);
		
		for (Player curPlayer: players) {
			try {
				writer.writeLine(curPlayer.basicToWriter());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
	}
	
}
