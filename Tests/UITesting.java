import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FileSystem.DataFileWriter;
import FileSystem.FileInfo;
import FileSystem.LocalFile;
import game.Game;
import team.Team;
import ui.TeamBoxScore;
import view.GameContainer;
import view.StatsTable;

public class UITesting {
	
	public static void main (String [] args) {
		
		final LocalFile file = new LocalFile ("D:\\Java_Projects\\BaseballSimulator\\SavedData");
		
		final boolean IS_VISIBLE = false;
		
		final int width = 1600;
		final int height = 1000;
		
		final int team1ID = 1;
		final int team2ID = 2;
		final int leagueID = 1;
		final int year = 2018;
		
		JFrame frame = new JFrame ("Baseball Game");
		frame.setSize(width, height);
		frame.setVisible(IS_VISIBLE);
		
		Team team1 = Team.generateSimpleTeam(team1ID);
		Team team2 = Team.generateSimpleTeam(team2ID);
		
		League testLeague = new League (leagueID, year);
		testLeague.addTeam(team1);
		testLeague.addTeam(team2);
		
		final int GAMES_TO_PLAY = 81;
		
		for (int times = 0; times < GAMES_TO_PLAY; times++) {
			testLeague.playGame(team1ID, team2ID, null);
		}
		
		file.setFileName(FileInfo.playersSeasonBattingStats.getFileName());
		
		DataFileWriter.appendPlayerSeasonBattingStats(file, team1.getBattingSeasonStats());
				
	}
	
}
