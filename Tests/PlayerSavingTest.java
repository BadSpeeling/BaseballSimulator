import java.io.IOException;

import objects.GamePlayer;
import player.Generators;
import utility.Writer;

public class PlayerSavingTest {
	
	public static void main (String [] args) {
		
		GamePlayer [] players = Generators.basicPlayerGenerator(100);
		String metaData [][] = {{"nextID","101"}};
		Writer writer = new Writer ("D:\\Java_Projects\\BaseballSimulator\\SavedData", "testPlayers.txt", metaData, false);
		
		for (GamePlayer curPlayer: players) {
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
