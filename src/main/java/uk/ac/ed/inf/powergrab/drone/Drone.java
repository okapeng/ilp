package uk.ac.ed.inf.powergrab.drone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Position;

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
		stateless, stateful
	}

	// Amount of power a drone requires to make a move
	private static final double POWER_CONSUMED_PER_MOVE = 1.25;

	protected Position curPosition;
	protected double coins;
	protected double power;

	public Drone(Position curPosition, double coins, double power) {
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

	/**
	 * Find all the possible directions the drone can move within the
	 * play area
	 *
	 * @return List of directions within range
	 */
	public List<Direction> getAllowedDirections() {
		List<Direction> allowedDirections = new ArrayList<Direction>(Direction.DIRECTIONS);
		return allowedDirections.stream().filter(dir -> curPosition.nextPosition(dir).inPlayArea())
				.collect(Collectors.toList());
	}

}
