import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class StadiumCreator {
	
	public static void main (String [] args) {
		
		Scanner input = null;
		try {
			input = new Scanner (new File (System.getProperty("user.dir") + "/Stadium/Data/stadium_data"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Stadium cur = new Stadium ();
		cur.loadDimensions(input);
		
	}
	
}
