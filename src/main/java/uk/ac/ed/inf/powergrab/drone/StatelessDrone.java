package uk.ac.ed.inf.powergrab.drone;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.*;

public class StatelessDrone extends Drone {

	private Random rand;

	public StatelessDrone(Position curPosition, double coins, double power, int seed) {
		super(curPosition, coins, power);
		this.rand = new Random(seed);
	}

	@Override
	public Direction decideMoveDirection(List<Direction> directions) {

		Collections.sort(directions, new Comparator<Direction>() {
			@Override
			public int compare(Direction d1, Direction d2) {
				ChargingStation s1 = Map.getInstance().getNearestStationInRange(curPosition.nextPosition(d1));
				ChargingStation s2 = Map.getInstance().getNearestStationInRange(curPosition.nextPosition(d2));
				return (int) (s2.getCoins() - s1.getCoins());
			}
		});

		Direction firstDirection = directions.get(0);
		if (Map.getInstance().getNearestStationInRange(curPosition.nextPosition(firstDirection)).getCoins() > 0) {
			return firstDirection;
		} else {
			directions = directions.stream().filter(dir -> {
				ChargingStation nearestStation = Map.getInstance()
						.getNearestStationInRange(curPosition.nextPosition(dir));
				return nearestStation.getCoins() >= 0;
			}).collect(Collectors.toList());

			return directions.get(rand.nextInt(directions.size()));
		}
	}

}
