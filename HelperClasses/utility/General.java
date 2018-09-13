package utility;

public class General {
	
	public static String ifExistsElseZero (Integer check) {
		return check == null ? "0" : check.toString();
	}
	
}
