import java.io.IOException;

import FileSystem.DataFileWriter;
import FileSystem.LocalFile;
import player.Generators;
import player.Player;
import utility.Writer;

public class PlayerSavingTest {
	
	public static void main (String [] args) {
		
		Player [] players = Generators.basicPlayerGenerator(1000);
		
		LocalFile myFile = new LocalFile ("D:\\Java_Projects\\BaseballSimulator\\SavedData","players.txt");
		DataFileWriter.appendPlayer(myFile, players);
		
	}
	
}
