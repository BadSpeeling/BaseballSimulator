package ratings;

import org.apache.commons.math3.distribution.NormalDistribution;

import numbers.RandomNumber;

public class RatingPair {

	private static final int PRECISION = 10000; //used to calculate statistics.  powers of 10 only. higher value means more precision 
	private double mean;
	private final double sd;
	
	public RatingPair(double mean, double sd) {
		this.mean = mean;
		this.sd = sd;
	}
	
	public void shiftMean (double by) {
		mean -= by;
	}
	
	public double getValue () {
		double percent = RandomNumber.roll(0, PRECISION)/(double)(PRECISION);
		return new NormalDistribution(mean,sd).inverseCumulativeProbability(percent);
	}
	
}
