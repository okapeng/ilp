package uk.ac.ed.inf.powergrab.map;

/**
 * Position of an object in the map
 * 
 * @author ivy
 *
 */
public class Position {

	private static final double R = 0.0003;
	private static final double MAX_LATITUDE = 55.946233;
	private static final double MIN_LATITUDE = 55.942617;
	private static final double MAX_LONGITUDE = -3.184319;
	private static final double MIN_LONGITUDE = -3.192473;

	/**
	 * The latitude and longitude of a position
	 */
	public double latitude;
	public double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Calculate the new Position after moving towards a given directions
	 *
	 * @param direction to be moved
	 * @return new position. If no direction is given, return current position
	 */
	public Position nextPosition(Direction direction) {
		if (direction == null) {
			return this;
		}
		double newLatitude = latitude + R * Math.sin(direction.getDegree());
		double newLongitude = longitude + R * Math.cos(direction.getDegree());
		return new Position(newLatitude, newLongitude);
	}

	/**
	 * @return true if the position is within the play area
	 */
	public boolean inPlayArea() {
		return this.latitude < MAX_LATITUDE && this.latitude > MIN_LATITUDE && this.longitude < MAX_LONGITUDE
				&& this.longitude > MIN_LONGITUDE;
	}

	/**
	 * Calculate the relative distance between this position and another position
	 * @param position another position
	 * @return the relative distance
	 */
	public double getRelativeDistance(Position position) {
		return Math.sqrt(
				Math.pow((this.latitude - position.latitude), 2) + Math.pow((this.longitude - position.longitude), 2));
	}

	@Override
	public String toString() {
		return (latitude + "," + longitude);
	}

}