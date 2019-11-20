package uk.ac.ed.inf.powergrab.drone;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

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

//	public Direction move() {
//		Direction moveDirection;
//		TreeMap<ChargingStation, Direction> potentialDirections = new TreeMap<ChargingStation, Direction>() {
//			@Override
//			public Comparator<? super ChargingStation> comparator() {
//				return new Comparator<ChargingStation>() {
//					@Override
//					public int compare(ChargingStation s1, ChargingStation s2) {
//						return (int) (s1.getCoins() - s2.getCoins());
//					}
//				};
//			}
//		};
//
//		for (Integer i = 0; i < Direction.NUM_OF_DIRECTIONS; i++) {
//			Position tempPosition = curPosition.nextPosition(Direction.valueOf(i.toString()));
//			if (tempPosition.inPlayArea()) {
//				ChargingStation nearestStation = Map.getInstance().getNearestStation(tempPosition);
//				if (nearestStation.getCoins() >= 0)
//					potentialDirections.put(nearestStation, Direction.valueOf(i.toString()));
//			}
//		}
//
//		this.power--;
//		if (potentialDirections.firstKey().getCoins() > 0) {
//			moveDirection = potentialDirections.firstEntry().getValue();
//			this.curPosition = curPosition.nextPosition(moveDirection);
//		} else {
//			moveDirection = ((List<Direction>) potentialDirections.values())
//					.get(rand.nextInt(potentialDirections.size()));
//			this.curPosition = curPosition.nextPosition(moveDirection);
//		}
//
//		return moveDirection;
//	}
//	Collections.sort(directionStationMap.entrySet(), new Comparator<Map.Entry<Direction, ChargingStation>>() {
//	@Override
//	public int compare(Entry<Direction, ChargingStation> o1, Entry<Direction, ChargingStation> o2) {
//		return (int) (o1.getValue().getCoins() - o2.getValue().getCoins());
//	}
//});
	//
//	directions = directions.stream().filter(dir -> {
//		ChargingStation nearestStation = Map.getInstance()
//				.getNearestStationInRange(curPosition.nextPosition(dir));
//		return nearestStation.getCoins() >= 0;
//	}).collect(Collectors.toList());

//	directions.stream().forEach(dir -> {
//	System.out.print(
//			Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins() + " ");
//});
//System.out.println();

}
