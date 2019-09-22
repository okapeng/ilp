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

	public Position nextPosition(Direction direction) {

		return null;
	}

	public boolean inPlayArea() {
		return this.latitude <= MAX_LATITIUDE && this.latitude >= MIN_LATITIUDE && this.longitude <= MAX_LONGITUDE
				&& this.longitude >= MIN_LONGITUDE;
	}
}