package datatype;

//used to build strings.  can check for gramatical correctness
public class StringBuilder {
	
	private String val = "";
	
	public String getValue () {
		return val;
	}
	
	public void concat (String toAdd) {
		val += toAdd;
	}
	
}
