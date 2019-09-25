package uk.ac.ed.inf.powergrab;

public enum Direction {
	/**
	 * Enumerate 16 directions from E to ESE anti-clockwise. Let E be the positive axes
	 */
	E(0), ENE(1), NE(2), NNE(3), N(4), NNW(5), NW(6), WNW(7), W(8), 
	WSW(9), SW(10), SSW(11), S(12), SSE(13), SE(14), ESE(15);
	
	/**
	 * The angle between two consecutive direction
	 */
	private static final double UNIT_DEGREE = Math.PI / 8;
	
	/**
	 *  The unit position difference by moving in the direction
	 */
	private double latitudeChange;
	private double longitudeChange;
	
	private Direction(int order) {
		double degree = order * UNIT_DEGREE;
		this.latitudeChange = Math.sin(degree);
		this.longitudeChange = Math.cos(degree);
	}

	public double getLatitudeChange() {
		return latitudeChange;
	}

	public double getLongitudeChange() {
		return longitudeChange;
	}

}
