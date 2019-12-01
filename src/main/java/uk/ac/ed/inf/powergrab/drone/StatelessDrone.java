package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless drone
 * 
 * @author ivy
 *
 */
public class StatelessDrone extends Drone {

	/**
	 * Use the default Constructor, as it doesn't requires additional attribute
	 */
	public StatelessDrone(Position curPosition, double coins, double power, int seed) {
		super(curPosition, coins, power, seed);
	}

	/**
	 * Stateless drone first filters out all the negative charging stations.
	 * It chooses to the direction resulting in most coins.
	 * If all the remaining directions doesn't reach any stations , randomly
	 * choose a direction without crashing into a negative station
	 * @param directions All the possible directions the drone can choose
	 * @return the direction the drone is going to move towards
	 */
	@Override
	public Direction decideMoveDirection(List<Direction> directions) {
		directions.removeIf(
				dir -> Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins() < 0);

		// Compute the potential coin gain after moving in each valid direction
		List<Double> coinGains = directions.stream()
				.map(dir -> Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins())
				.collect(Collectors.toList());

		Double maxGain = Collections.max(coinGains);
		return maxGain > 0 ? directions.get(coinGains.indexOf(maxGain))
				: directions.get(rand.nextInt(directions.size()));

	}

}
