package uk.ac.ed.inf.powergrab.engine;

import uk.ac.ed.inf.powergrab.drone.Drone;
import uk.ac.ed.inf.powergrab.drone.StatefulDrone;
import uk.ac.ed.inf.powergrab.drone.StatelessDrone;
import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.Arrays;

/**
 * Game engine
 * 
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

	/**
	 * Drone type supported
	 *
	 * @author Ivy Wang
	 *
	 */
	public enum DroneType {
		stateless, stateful
	}

	/*
	 * Initialise a new PowerGrab game with a starting position, drone type, and
	 * random seed (for stateless drone only)
	 */
	public PowerGrab(Position initPosition, String droneTypeStr, int randomSeed) throws IllegalArgumentException {
		try {
			this.numOfMoves = 0;
			DroneType droneType = DroneType.valueOf(droneTypeStr);
			switch (droneType) {
				case stateful:
					this.drone = new StatefulDrone(initPosition, INITIAL_COINS, INITIAL_POWER);
					break;
				case stateless:
					this.drone = new StatelessDrone(initPosition, INITIAL_COINS, INITIAL_POWER, randomSeed);
					break;
			}
			Map.getInstance().addDronePosition(initPosition);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Note unsupported drone type, please choose from: " + Arrays.toString(DroneType.values()));
		}
	}

	/**
	 * Main loop for a powergrab game Each loop includes deciding move direction,
	 * moving, transforming coins and powers from/to Charging station (optional)
	 * 
	 * @return String of drone's movement trace
	 */
	public String play() {
		Double sum = (Map.getInstance().getChargingStations().stream().map(ChargingStation::getCoins).filter(x -> x > 0)
				.reduce(Double::sum)).get();

		while (drone.getPower() > 0 && numOfMoves < MAX_MOVES) {
			Position oldPosition = drone.getPosition();
			Direction moveDirection = drone.decideMoveDirection(drone.getPosition().getAllowedDirections());
			if (!drone.move(moveDirection)) {
				break;
			}
			transfer();
			Map.getInstance().addDronePosition(drone.getPosition());
			movesTrace.append(String.format("%s,%s,%s,%.2f,%.2f\n", oldPosition, moveDirection, drone.getPosition(),
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
	 * At each move, transfer the coins and power between the drone and the nearest
	 * charging station within range
	 */
	private void transfer() {
		ChargingStation nearestStation = Map.getInstance().getNearestStationInRange(drone.getPosition());
		if (nearestStation == null)
			return;

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
