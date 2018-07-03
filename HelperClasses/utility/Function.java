package utility;

import java.util.LinkedList;
import java.util.List;

//defines a polynomial function
public class Function {
	
	private List <Equation> pieceWiseFn = new LinkedList <Equation> ();
	
	//domain is a n-by-2 matrix where the enter n(0,1) is the domain of n in eqs 
	public Function (String [] eqs, double [][] domain) throws IllegalArgumentException {
		
		if (eqs.length != domain.length) {
			throw new IllegalArgumentException ("eqs and domain must match in dimensions");
		}
		
		for (int i = 0; i < eqs.length; i++) {
			
			if (domain[i][0] > domain[i][1]) {
				throw new IllegalArgumentException ("lower bound cannot be greater than upper: " + domain[i][0] + ","+ domain[i][1]);
			}
			
			pieceWiseFn.add(new Equation(eqs[i],domain[i][0], domain[i][1]));
		}
		
	}
	
	public double val (double x) throws IllegalArgumentException {
		
		String print = "";
		
		for (Equation curEq: pieceWiseFn) {
			
			print += curEq.toString();
			double ret = curEq.calc(x);
			
			if (ret != Double.MAX_VALUE) {
				return ret;
			}
			
		}
		
		throw new IllegalArgumentException (x + " is not a valid domain for: " + print);
		
	}
	
	public String toString () {
		
		String ret = "";
		
		for (Equation curEq: pieceWiseFn) {
			ret += curEq.toString();
		}
		
		return ret;
		
	}
	
	//an equation is of the form [0-9]+x^[0-9]+
	private class Equation {
		
		List <Part> fullEquation = new LinkedList <Part> ();
		Double leftBounds;
		Double rightBounds;	
		
		public Equation (String equation, double left, double right) {
			
			String [] split = equation.split("\\+");
			
			for (String curPart: split) {
				
				String [] parts = curPart.split("x\\^");
				
				double constant = 1;
				double expo = 0;
				
				if (parts.length != 2) {
					
					if (curPart.indexOf("x") == -1) {
						constant = Double.parseDouble(curPart);
						expo = 0;
					}
					
					else {
						constant = Double.parseDouble(curPart.substring(0, curPart.length()-1));
						expo = 1;
					}
					
				}
				
				else {
					constant = Double.parseDouble(parts[0]);
					expo = Double.parseDouble(parts[1]);
				}
					
				fullEquation.add(new Part (expo, constant));
				
			}
			
			leftBounds = (left);
			rightBounds = (right);
			
		}
		
		public double calc (double x) {
			
			if (!(x >= leftBounds && x <= rightBounds)) {
				return Double.MAX_VALUE;
			}
			
			double ret = 0;
			
			for (int i = 0; i < fullEquation.size(); i++) {
				ret += fullEquation.get(i).value(x);
			}
			
			return ret;
			
		}
		
		public String toString () {
			
			String ret = "";
			
			for (int i = 0; i < fullEquation.size(); i++) {
				ret += fullEquation.get(i)+"+";
			}
			
			return ret.substring(0, ret.length()-1)+" ["+leftBounds+","+rightBounds+"]\n";
			
		}
		
		private class Part {
			
			private double degree;
			private double constant;
			
			public Part (double degree, double constant) {
				this.degree = degree;
				this.constant = constant;
			}
			
			public double value (double val) {
				return constant *Math.pow(val, degree);
			}
			
			public void setDegree (int degree) {
				this.degree = degree;
			}
			
			public void setConstant (double constant) {
				this.constant = constant;
			}
			
			public String toString () {
				return constant + "x^" + degree; 
			}
			
		}
		
	}
	
	public void testVals (double start, double end, double by) {
		
		double curNum = start;
		
		while (curNum < end) {
			System.out.println(curNum +": "+val(curNum));
			curNum += by;
		}
		
	}
	
}
