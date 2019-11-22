package uk.ac.ed.inf.powergrab;

import uk.ac.ed.inf.powergrab.drone.*;
import uk.ac.ed.inf.powergrab.map.*;
import uk.ac.ed.inf.powergrab.drone.Drone.DroneType;

/**
 * Game engine
 * @author ivy
 *
 */
public class PowerGrab {

	private static final double INITIAL_COINS = 0;
	private static final double INITIAL_POWER = 250;
	private static final int MAX_MOVES = 250;
	public static int count = 0;

	private Drone drone;
	private int numOfMoves;
	private StringBuffer movesTrace = new StringBuffer();

	/*
	 * Initialise a new PowerGrab game with a starting position, drone type, and random seed (for stateless drone only) 
	 */
	public PowerGrab(Position initPosition, DroneType droneType, int randomSeed) {
		this.numOfMoves = 0;
		switch (droneType) {
		case stateful:
			this.drone = new StatefulDrone(initPosition, INITIAL_COINS, INITIAL_POWER);
			break;
		case stateless:
			this.drone = new StatelessDrone(initPosition, INITIAL_COINS, INITIAL_POWER, randomSeed);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Main loop for a powergrab game
	 * @return String of drone's movement trace
	 */
	public String play() {
		Double sum = (Map.getInstance().getchargingStations().stream().map(ChargingStation::getCoins).filter(x -> x > 0)
				.reduce(Double::sum)).get();
		
		/*
		 * Each loop includes deciding move direction, moving, transforming coins and powers from/to Charging station (optional)
		 */
		while (drone.getPower() > 0 && numOfMoves < MAX_MOVES) {
			Position oldPosition = drone.getPosition();
			Direction moveDirection = drone.decideMoveDirection(drone.getPosition().getAllowedDirections());
			Position newPosition = drone.move(moveDirection);
			transfer();
			movesTrace.append(String.format("%s,%s,%s,%.2f,%.2f\n", oldPosition, moveDirection, newPosition,
					drone.getCoins(), drone.getPower()));
			numOfMoves++;
		}

		if ((int) (sum - drone.getCoins()) > 0) {
			System.out.println("Remain Coins: " + (int) (sum - drone.getCoins()));
			count++;
		}
		return movesTrace.toString();
	}

	/**
	 * At each move, transfer the coins and power between charging station and drone if possible
	 */
	private void transfer() {
		ChargingStation nearestStation = Map.getInstance().getNearestStationInRange(drone.getPosition());
		if (nearestStation == null) return;
		
		double coins = drone.getCoins() + nearestStation.getCoins() > 0 ? nearestStation.getCoins()
				: 0 - drone.getCoins();
		double power = drone.getPower() + nearestStation.getPower() > 0 ? nearestStation.getPower()
				: 0 - drone.getPower();
		
		nearestStation.transfer(coins, power);
		drone.transfer(nearestStation, coins, power);
		
//		if (coins < 0) {
//			System.out.println("Crash to negative station");
//		}

	}

}
