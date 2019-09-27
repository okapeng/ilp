package uk.ac.ed.inf.powergrab;

public class Position {

	private static final double R = 0.0003;
	private static final double MAX_LATITIUDE = 55.946233;
	private static final double MIN_LATITIUDE = 55.942617;
	private static final double MAX_LONGITUDE = -3.184319;
	private static final double MIN_LONGITUDE = -3.192473;

	public double latitude;
	public double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Calculate the new Position after moving in a given direction
	 * 
	 * @param direction
	 * @return valid new position i.e. within the playing area
	 */
	public Position nextPosition(Direction direction) {
		if (direction == null) {
			return null;
		}
		double newLatitude = latitude + R * direction.getLatitudeChange();
		double newLongitude = longitude + R * direction.getLongitudeChange();
		return new Position(newLatitude, newLongitude);
	}

	/**
	 * Check whether the position is within the playing area
	 * 
	 * @return
	 */
	public boolean inPlayArea() {
		return this.latitude < MAX_LATITIUDE && this.latitude > MIN_LATITIUDE && this.longitude < MAX_LONGITUDE
				&& this.longitude > MIN_LONGITUDE;
	}
}