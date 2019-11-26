package uk.ac.ed.inf.powergrab.drone;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

/**
 * Stateless drone
 * 
 * @author ivy
 *
 */
public class StatelessDrone extends Drone {

	// Random instance to generate pseudo random numbers based on a seed
	private Random rand;

	public StatelessDrone(Position curPosition, double coins, double power, int seed) {
		super(curPosition, coins, power);
		this.rand = new Random(seed);
	}

	/**
	 * Implement the strategy for stateless drone to decide next moving direction
	 */
	@Override
	public Direction decideMoveDirection(List<Direction> directions) {
		// Filter out negative charging stations
		directions.removeIf(
				dir -> Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins() < 0);

		// Compute the potential coin gain after moving in each direction
		List<Double> coinGains = directions.stream()
				.map(dir -> Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins())
				.collect(Collectors.toList());

		/*
		 * Moves to the direction resulting in the maximum coins. Otherwise, randomly
		 * choosing a direction without crashing into a negative station
		 */
		Double maxGain = Collections.max(coinGains);
		return maxGain > 0 ? directions.get(coinGains.indexOf(maxGain))
				: directions.get(rand.nextInt(directions.size()));

	}

}
