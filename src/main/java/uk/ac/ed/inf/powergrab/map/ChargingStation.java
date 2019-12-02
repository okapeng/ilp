package uk.ac.ed.inf.powergrab.map;

/**
 * Charging station
 * 
 * @author Ivy Wang
 *
 */
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

	/**
	 * Transfer coins and power away from the station
	 *
     * @param coins amount to be transferred
     * @param power amount to be transferred
	 */
	public void transfer(double coins, double power) {
		this.coins -= coins;
		this.power -= power;
	}
}
