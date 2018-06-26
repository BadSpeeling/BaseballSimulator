package utility;

import java.util.Scanner;

public class Reader {

	private Scanner input;
	
	public Reader (String path, String fileName) {
		input = new Scanner (path + "\\" + fileName);
	}
	
	public Reader (String fileName) {
		input = new Scanner (System.getProperty("user.dir") + "\\" + fileName);
	}
	
	public String readLine () {
		
		if (input.hasNext()) {
			return input.nextLine();
		}
		
		else {
			return null;
		}
		
	}
	
}
