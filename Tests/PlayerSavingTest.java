import java.io.IOException;

import FileSystem.DataFileWriter;
import FileSystem.LocalFile;
import player.Generators;
import player.Player;
import utility.Writer;

public class PlayerSavingTest {
	
	public static void main (String [] args) {
		
		Player [] players = Generators.basicPlayerGenerator(1000);
		
		LocalFile playersFile = new LocalFile ("D:\\Java_Projects\\BaseballSimulator\\SavedData","players.txt");
		LocalFile playersBattingRatingsFile = new LocalFile ("D:\\Java_Projects\\BaseballSimulator\\SavedData","playersBatting.txt");
		LocalFile playersPitchingRatingsFile = new LocalFile ("D:\\Java_Projects\\BaseballSimulator\\SavedData","playersPitching.txt");

		DataFileWriter.appendPlayer(playersFile, players,null,null);
		DataFileWriter.appendPlayerBattingRatings(playersBattingRatingsFile, players);
		DataFileWriter.appendPlayerPitchingRatings(playersPitchingRatingsFile, players);
		
	}
	
}
