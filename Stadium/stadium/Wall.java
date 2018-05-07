package stadium;

import datatype.Coordinate3D;

public class Wall {
	
	private final Coordinate3D p1; //first point
	private final Coordinate3D p2; //second point
	
	private final double height; //how high the wall is
	
	private double slope; //slope of point
	private final boolean p1BeforeP2; //true if p1.x < p2.x
	
	public Wall (double x1, double y1, double x2, double y2, double height) {
		
		p1 = new Coordinate3D(x1,y1,0);
		p2 = new Coordinate3D(x2,y2,0);
		this.height = height;
		
		slope = ((double)(y2-y1))/(x2-x1);
		p1BeforeP2 = (x1 < x2);
				
	}

	public Coordinate3D getP1() {
		return p1;
	}

	public Coordinate3D getP2() {
		return p2;
	}

	public double getHeight() {
		return height;
	}

	public double getSlope() {
		return slope;
	}

	public boolean isP1BeforeP2() {
		return p1BeforeP2;
	}
	
	
	
}
