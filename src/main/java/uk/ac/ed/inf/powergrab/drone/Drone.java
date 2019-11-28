package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Skeleton for a valid drone class
 * 
 * @author Ivy Wang
 *
 */
public abstract class Drone {

	/**
	 * Drone type supported by PowerGrab
	 *
	 * @author Ivy Wang
	 *
	 */
	public enum DroneType {
		stateless("uk.ac.ed.inf.powergrab.drone.StatelessDrone"),
		stateful("uk.ac.ed.inf.powergrab.drone.StatefulDrone"),
		statefulOld("uk.ac.ed.inf.powergrab.drone.StatefulDroneOld");

		private Class<Drone> droneClass;

		DroneType(String className) throws IllegalArgumentException {
			try {
				this.droneClass = (Class<Drone>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException();
			}
		}

		/**
		 * Initiate a new instance of drone of the given type with the parameters passed in
		 *
		 * @param initPosition initial position of the Drone
		 * @param coin         initial coin of the drone
		 * @param power        initial power of the drone
		 * @param seed         random seed
		 * @return an instance of Drone with the given type
		 * @throws Exception NoSuchMethodException infers that this drone type is not supported
		 */
		public Drone newInstance(Position initPosition, double coin, double power, int seed) throws Exception {
			return droneClass.getConstructor(Position.class, double.class, double.class, int.class)
					.newInstance(initPosition, coin, power, seed);
		}
	}

	// Amount of power a drone requires to make a move
	private static final double POWER_CONSUMED_PER_MOVE = 1.25;

	Position curPosition;
	protected double coins;
	protected double power;

	public Drone(Position curPosition, double coins, double power, int seed) {
		this.curPosition = curPosition;
		this.coins = coins;
		this.power = power;
	}

	public Position getPosition() {
		return curPosition;
	}

	public double getCoins() {
		return coins;
	}

	public double getPower() {
		return power;
	}

	/**
	 * Find all the possible directions the drone can move within the
	 * play area
	 *
	 * @return List of directions within range
	 */
	public List<Direction> getAllowedDirections() {
		List<Direction> allowedDirections = new ArrayList<>(Direction.DIRECTIONS);
		return allowedDirections.stream().filter(dir -> curPosition.nextPosition(dir).inPlayArea())
				.collect(Collectors.toList());
	}

	/**
	 * Abstract method to be implemented based on the drone's strategy
	 *
	 * @param directions All the possible directions a drone can choose from
	 * @return the direction the drone is going to move
	 */
	public abstract Direction decideMoveDirection(List<Direction> directions);

	/**
	 * Move the drone based on a give direction
	 * 
	 * @param direction
	 * @return true if the drone has enough power to make the move
	 */
	public boolean move(Direction direction) {
		this.curPosition = curPosition.nextPosition(direction);
		this.power -= POWER_CONSUMED_PER_MOVE;
		return (this.power >= 0);
	}

	public void transfer(ChargingStation chargingStation, double coins, double power) {
		this.coins += coins;
		this.power += power;
	}

}
