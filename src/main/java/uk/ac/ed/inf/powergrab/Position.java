package uk.ac.ed.inf.powergrab;

public class Position {
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

	public Position nextPosition(Direction direction) {
<<<<<<< HEAD
		
		return null;
=======
		Position newPos = new Position(latitude + direction.getLatitudeChange(), 
				longitude + direction.getLongitudeChange());
		return newPos.inPlayArea() ? newPos : null;
>>>>>>> refs/heads/develop
	}

	public boolean inPlayArea() {
		return this.latitude < MAX_LATITIUDE 
				&& this.latitude > MIN_LATITIUDE 
				&& this.longitude < MAX_LONGITUDE
				&& this.longitude > MIN_LONGITUDE;
	}
}