package uk.ac.ed.inf.powergrab;

import java.util.Arrays;
import java.util.List;


/**
 * The directions that a drone can choose to move
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
	public static final int NUM_OF_DIRECTIONS = 16;
	

	/**
	 * The degree of angle between this direction and E
	 */
	private double degree;

	/**
	 * Constructor of Direction, calculate the degree
	 * 
	 * @param order
	 */
	private Direction(int index) {
		this.degree = index * UNIT_DEGREE;
	}

	public double getDegree() {
		return degree;
	}
	
	
	
//	public static final List<Direction> DIRECTIONS2 = IntStream.range(0, Direction.NUM_OF_DIRECTIONS)
//			.mapToObj(i -> Direction.valueOf(Integer.toString(i))).collect(Collectors.toList());
	
	public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.values());

}
