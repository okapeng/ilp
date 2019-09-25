package uk.ac.ed.inf.powergrab;

public enum Direction {

	E(0), ENE(1), NE(2), NNE(3), N(4), NNW(5), NW(6), WNW(7), W(8), 
	WSW(9), SW(10), SSW(11), S(12), SSE(13), SE(14), ESE(15);
	
	private static final double R = 0.0003;
	private static final double UNIT_DEGREE = Math.PI / 8.0;
	
	private double degree;
	private double latitudeChange;
	private double longitudeChange;
	
	private Direction(int order) {
		this.degree = order * UNIT_DEGREE;
		this.latitudeChange =  R * Math.sin(this.degree);
		this.longitudeChange = R * Math.cos(this.degree);
	}

	public double getLatitudeChange() {
		return latitudeChange;
	}

	public double getLongitudeChange() {
		return longitudeChange;
	}

}
