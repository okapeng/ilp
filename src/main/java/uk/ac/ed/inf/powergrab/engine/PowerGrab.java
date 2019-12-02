package uk.ac.ed.inf.powergrab.engine;

import uk.ac.ed.inf.powergrab.drone.Drone;
import uk.ac.ed.inf.powergrab.drone.Drone.DroneType;
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
	private static final int MAX_NUMBER_OF_MOVES = 250;
	public static int count = 0;

	private Drone drone;
	private int numOfMoves = 0;
	private StringBuffer movesTrace = new StringBuffer();

	/*
	 * Initialise a new PowerGrab game with a starting position, drone type, and
	 * random seed (for stateless drone only)
	 */
	public PowerGrab(double initLatitude, double initLongitude, String droneTypeStr, int randomSeed) throws IllegalArgumentException {
		try {
			Position initPosition = new Position(initLatitude, initLongitude);
			DroneType droneType = DroneType.valueOf(droneTypeStr);
			this.drone = droneType.newInstance(initPosition, INITIAL_COINS, INITIAL_POWER, randomSeed);
			Map.getInstance().addDronePosition(initPosition);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Unsupported drone type, please choose from: " + Arrays.toString(DroneType.values()));
		}
	}

	/**
	 * Main loop for the powergrab game i.e. the life cycle of a drone.
	 * Each loop includes finding all possible directions, deciding on the direction to move next,
	 * moving the drone, transforming coins and powers between the charging station and drone(optional)
	 *
	 * @return String of drone's movement trace log
	 */
	public String play() {
		Double sum = (Map.getInstance().getChargingStations().stream().map(ChargingStation::getCoins).filter(x -> x > 0)
				.reduce(Double::sum)).get();

		while (drone.getPower() > 0 && numOfMoves < MAX_NUMBER_OF_MOVES) {
			Position oldPosition = drone.getPosition();
			Direction moveDirection = drone.decideMoveDirection(drone.getAllowedDirections());
			if (!drone.move(moveDirection))
				break;
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
	 * charging station within the range of minimum transfer distance if it exists.
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
		drone.transfer(coins, power);

		if (coins < 0) {
			System.out.println(nearestStation);
			System.out.println("Crash to negative station");
		}

	}

}
