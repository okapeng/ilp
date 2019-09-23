package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;

	private final double R = 0.0003;
	private final double MAX_LATITIUDE = 55.942617;
	private final double MIN_LATITIUDE = 55.946233;
	private final double MAX_LONGITUDE = -3.184319;
	private final double MIN_LONGITUDE = -3.192473;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private void moveInDirection(Direction direction) {
		this.longitude += Math.cos(direction.getDegree());
		this.latitude += Math.sin(direction.getDegree());
	}

	public Position nextPosition(Direction direction) {
		Position newPos = new Position(latitude + direction.getLatitudeChange(), longitude + direction.getLongitudeChange());
		return newPos.inPlayArea() ? newPos : null;
	}

	public boolean inPlayArea() {
		return this.latitude <= MAX_LATITIUDE && this.latitude >= MIN_LATITIUDE && this.longitude <= MAX_LONGITUDE
				&& this.longitude >= MIN_LONGITUDE;
	}
}