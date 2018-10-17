import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import game.Game;
import team.Team;
import ui.TeamBoxScore;
import view.GameContainer;
import view.StatsTable;

public class UITesting {
	
	public static void main (String [] args) {
		
		final int width = 1600;
		final int height = 1000;
		
		final int team1ID = 1;
		final int team2ID = 2;
		final int leagueID = 1;
		final int year = 2018;
		
		JFrame frame = new JFrame ("Baseball Game");
		frame.setSize(width, height);
		frame.setVisible(true);
		
		Team team1 = Team.generateSimpleTeam(team1ID);
		Team team2 = Team.generateSimpleTeam(team2ID);
		
		League testLeague = new League (leagueID, year);
		testLeague.addTeam(team1);
		testLeague.addTeam(team2);
		
		testLeague.playGame(team1ID, team2ID, frame);
		
				
	}
	
}
