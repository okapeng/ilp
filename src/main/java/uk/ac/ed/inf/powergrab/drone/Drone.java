package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Skeleton for a valid drone class
 * @author Ivy Wang
 */
public abstract class Drone {

	/**
     * Valid drone type supported by PowerGrab
	 * @author Ivy Wang
	 */
	public enum DroneType {
		stateless("uk.ac.ed.inf.powergrab.drone.StatelessDrone"),
        stateful("uk.ac.ed.inf.powergrab.drone.StatefulDrone");

		private Class<Drone> droneClass;

        /**
		 * Given the name of the subclass with the implementation
		 * of a given type of drone, find the corresponding java class
         *
         * @param className the fully qualified name of class extending Drone
         * @throws IllegalArgumentException if the class with that name doesn't exist
         */
        DroneType(String className) throws IllegalArgumentException {
            try {
                this.droneClass = (Class<Drone>) Class.forName(className);
				if (!this.droneClass.getSuperclass().equals(Drone.class)) throw new IllegalArgumentException();
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
         * @return an instance of Drone of the given class
		 * @throws IllegalArgumentException indicates the drone type passed by user is not supported
		 */
		public Drone newInstance(Position initPosition, double coin, double power, int seed) throws IllegalArgumentException {
			try {
				return droneClass.getConstructor(Position.class, double.class, double.class, int.class)
						.newInstance(initPosition, coin, power, seed);
			} catch (Exception e) {
				throw new IllegalArgumentException();
			}
		}
	}

	// Amount of power a drone requires to make a move
	private static final double POWER_CONSUMED_PER_MOVE = 1.25;

	protected Position curPosition;
	protected double coins;
    protected double power;
    protected Random rand;

	public Drone(Position curPosition, double coins, double power, int seed) {
		this.curPosition = curPosition;
		this.coins = coins;
        this.power = power;
        this.rand = new Random(seed);
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
     * @param directions All the possible directions a drone can choose
	 * @return the direction the drone is going to move
	 */
	public abstract Direction decideMoveDirection(List<Direction> directions);

	/**
	 * Move the drone based on a give direction
     *
     * @param direction that the drone decided to move towards
     * @return true if the drone has enough power to make that move
	 */
	public boolean move(Direction direction) {
		this.curPosition = curPosition.nextPosition(direction);
		this.power -= POWER_CONSUMED_PER_MOVE;
        return (this.power >= 0);
    }

    /**
     * Add the coins and power to the drone if they are positive,
     * otherwise deduct them.
     *
     * @param coins the amount of coins to be transferred (can be negative or positive)
     * @param power the amount of power to be transferred (can be negative or positive)
     */
    public void transfer(double coins, double power) {
		this.coins += coins;
		this.power += power;
	}

}
