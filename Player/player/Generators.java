package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import numbers.RandomNumber;

public class Generators {
	
	public static Player [] basicPlayerGenerator (int num) {
		
		Player [] toRet = new Player [num];
		String [] fNames = randomFirstNames(num);
		String [] lNames = randomLastNames(num);
		Position [] pos = randomPosition(num);
		int nextID = 1;
		
		for (int i = 0; i < num; i++) {
			
			Player curBasicPlayer = new Player (pos[i],fNames[i],lNames[i],nextID);
			nextID++;
			toRet[i] = curBasicPlayer;
			
		}
		
		return toRet;
		
	}
	

	private static String [] randomFirstNames (int num) {
		
		Scanner in = null;
		Random r = new Random ();
		String [] ret = new String [num];
		
		try {
			in = new Scanner (new File ("D:\\Java_Projects\\BaseballSimulator\\Player\\Names\\firstNames"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String [] allFirstNames = in.nextLine().split(",");

		for (int i = 0; i < num; i++) {
			ret[i] = allFirstNames[r.nextInt(allFirstNames.length)];
		}
		
		return ret;
		
	}
	
	private static String [] randomLastNames (int num) {
		
		Scanner in = null;
		Random r = new Random ();
		String [] ret = new String [num];
		
		try {
			in = new Scanner (new File ("D:\\Java_Projects\\BaseballSimulator\\Player\\Names\\lastNames"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String [] allLastNames = in.nextLine().split(",");

		for (int i = 0; i < num; i++) {
			ret[i] = allLastNames[r.nextInt(allLastNames.length)];
		}
		
		return ret;
		
	}
	
	private static Position [] randomPosition (int num) {
		
		Position [] ret = new Position [num];
		
		for (int i = 0; i < num; i++) {
			
			if (RandomNumber.coinFlip()) {
				ret[i] = Position.PITCHER;
			}
			
			else {
				ret[i] = Position.getValue(RandomNumber.roll(2, 9));
			}
		
		}
		
		return ret;
			
	}

	
}
