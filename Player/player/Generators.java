package player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import numbers.RandomNumber;
import objects.Base;
import objects.Baserunner;
import objects.GamePlayer;

public class Generators {
	
	public static GamePlayer [] basicPlayerGenerator (int num) {
		
		GamePlayer [] toRet = new GamePlayer [num];
		String [] fNames = randomFirstNames(num);
		String [] lNames = randomLastNames(num);
		Position [] pos = randomPosition(num);
		int nextID = 1;
		
		for (int i = 0; i < num; i++) {
			
			GamePlayer curBasicPlayer = new GamePlayer (pos[i],fNames[i],lNames[i],nextID);
			curBasicPlayer.generateSimpleStats();
			nextID++;
			toRet[i] = curBasicPlayer;
			
		}
		
		return toRet;
		
	}
	
	public static Baserunner generateRunner (Base on) {
		
		GamePlayer [] temp = basicPlayerGenerator(1);
		Baserunner runner = new Baserunner(temp[0],0xFFFFFF);
		runner.placeOnBase(on);
		return runner;
		
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
