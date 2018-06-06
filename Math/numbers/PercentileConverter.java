package numbers;

import org.apache.commons.math3.distribution.NormalDistribution;

public class PercentileConverter {
	
	private static final int PRECISION = 10000; //used to calculate statistics.  powers of 10 only. higher value means more precision 
	
	public static double getValue (double mean, double sd) {
		double percent = RandomNumber.roll(0, PRECISION)/(double)(PRECISION);
		return new NormalDistribution(mean,sd).inverseCumulativeProbability(percent);
	}
	
}
