import utility.Function;

public class FunctionTest {
	
	public static void main (String [] args) {
		

		double [][] bounds = {{0,1},{1,2},{2,3}};
		String [] function = {"-10+10x^2","5x^2","-3.3x"};
	
		Function func = new Function (function,bounds);
		
		func.testVals(0, 3, .2);
		
	}
	
}
