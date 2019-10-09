package uk.ac.ed.inf.powergrab;

/**
 * The position of the drone
 * @author ivy
 *
 */
public class Position {

	private static final double R = 0.0003;
	private static final double MAX_LATITIUDE = 55.946233;
	private static final double MIN_LATITIUDE = 55.942617;
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
	 * Calculate the new Position after moving in a given directions
	 * 
	 * @param direction
	 * @return new position
	 */
	public Position nextPosition(Direction direction) {
		if (direction == null) {
			return null;
		}
		double newLatitude = latitude + R * Math.sin(direction.getDegree());
		double newLongitude = longitude + R * Math.cos(direction.getDegree());
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
	
	public double getRelativeDistance(Position position) {
		return Math.sqrt(Math.pow((this.latitude - position.latitude), 2) + Math.pow((this.longitude - position.longitude), 2));
	}

	@Override
	public String toString() {
		return "Position [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	
	
}