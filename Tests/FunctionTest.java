import utility.Function;

public class FunctionTest {
	
	public static void main (String [] args) {
		
		double [][] bounds = {{-1,1},{1,3}};
		String [] fns = {"2x^1+2x^1", "7+9"};
		Function func = new Function (fns,bounds);
		
		System.out.println(func);
		System.out.println(func.val(2.1));
		
	}
	
}
