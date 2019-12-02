package uk.ac.ed.inf.powergrab.map;

import java.util.Arrays;
import java.util.List;

/**
 * Directions a drone can choose to move
 * 
 * @author ivy
 *
 */
public enum Direction {
	/**
	 * Enumerate 16 directions from E to ESE anti-clockwise. Let E be the positive
	 * axes
	 */
	E(0), ENE(1), NE(2), NNE(3), N(4), NNW(5), NW(6), WNW(7), W(8), WSW(9), SW(10), SSW(11), S(12), SSE(13), SE(14),
	ESE(15);

	/**
	 * The fixed angle between two consecutive direction
	 */
	private static final double UNIT_DEGREE = Math.PI / 8;

	/**
	 * a list of all the possible direction a drone can move
	 */
	public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.values());

	// The angle between this direction and E
	private double degree;

	/**
	 * Constructor of Direction, calculate the degree
	 * 
	 * @param index the index of a direction, starting from E
	 */
    Direction(int index) {
		this.degree = index * UNIT_DEGREE;
	}

	public double getDegree() {
		return degree;
	}

	/**
	 * 
	 * @return the direction opposites to this one
	 */
	public Direction getOppositeDirection() {
        int indexOfDiagonal = (DIRECTIONS.indexOf(this) + (DIRECTIONS.size() / 2)) % DIRECTIONS.size();
		return DIRECTIONS.get(indexOfDiagonal);
	}

}
