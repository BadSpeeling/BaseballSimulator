package FileSystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import player.Player;

public class DataFileWriter {
	
	//adds the array of players to the end of filePath
	public static boolean appendPlayer (LocalFile filePath, Player [] toAdd, Integer teamID, Integer leagueID) {
		
		try(
				FileWriter fw = new FileWriter(filePath.getFullPath(), true); 
				BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			
				for (Player curToAdd: toAdd) {
					bw.write(curToAdd.getPlayerDataToSaveInfo(teamID, leagueID));
				}
			
				return true;
			
			} catch (IOException e) {
				return false;
			}
		
	}
	
	//adds the array of players batting ratings to the end of filePath
	public static boolean appendPlayerBattingRatings (LocalFile filePath, Player [] toAdd) {
		
		try(
				FileWriter fw = new FileWriter(filePath.getFullPath(), true); 
				BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			
				for (Player curToAdd: toAdd) {
					bw.write(curToAdd.getPlayerBattingRatingsDataToSaveInfo());
				}
			
				return true;
			
			} catch (IOException e) {
				return false;
			}
		
	}
	
	//append the players pitching ratings to the LocalFile
	public static boolean appendPlayerPitchingRatings (LocalFile filePath, Player [] toAdd) {
		
		try(
				FileWriter fw = new FileWriter(filePath.getFullPath(), true); 
				BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			
				for (Player curToAdd: toAdd) {
					bw.write(curToAdd.getPlayerPitchingRatingsDataToSaveInfo());
				}
			
				return true;
			
			} catch (IOException e) {
				return false;
			}
		
	}
	
}
