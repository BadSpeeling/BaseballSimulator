package utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Reader {

	private Scanner input;
	private Map <String,String> metaData = new HashMap <String,String> ();
	
	public Reader (String path, String fileName) {
		input = new Scanner (path + "\\" + fileName);
		readMetaData();
	}
	
	public Reader (String fileName) {
		input = new Scanner (System.getProperty("user.dir") + "\\" + fileName);
		readMetaData();
	}
	
	private void readMetaData () {
		
		String meta = input.nextLine();
		String [] info = meta.split(",");
		
		for (int i = 0; i < info.length; i++) {
			int split = info[i].indexOf("#");
			metaData.put(info[i].substring(0, split), info[i].substring(split+1));
		}
		
	}
	
	public String readMetaData (String val) {
		return metaData.get(val);
	}
	
	public String readLine () {
		
		if (input.hasNext()) {
			return input.nextLine();
		}
		
		else {
			return null;
		}
		
	}
	
	public void close () {
		close();
	}
	
}
