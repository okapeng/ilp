package uk.ac.ed.inf.powergrab;

public class ChargingStation {
	
	private Position position;
	private double coins;
	private double power;
	
	public ChargingStation(Position location, Number coins, Number power) {
		this.position = location;
		this.coins = coins.doubleValue();
		this.power = power.doubleValue();
	}

	public Position getPosition() {
		return position;
	}

	public double getCoins() {
		return coins;
	}

	public double getPower() {
		return power;
	}
	
	public void transfer(Drone drone) {
		drone.setCoins(this.coins - drone.getCoins() > 0 ? this.coins - drone.getCoins() : 0);
		
	}
	
}
