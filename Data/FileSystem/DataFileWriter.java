package FileSystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import player.Player;

public class DataFileWriter {
	
	//adds the array of players to the end of filePath
	public static boolean appendPlayer (LocalFile filePath, Player [] toAdd) {
		
		try(
				FileWriter fw = new FileWriter(filePath.getFullPath(), true); 
				BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			
				for (Player curToAdd: toAdd) {
					bw.write(curToAdd.getPlayerDataToSaveInfo(null, null));
				}
			
				return true;
			
			} catch (IOException e) {
				return false;
			}
		
	}
	
}
