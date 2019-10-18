package uk.ac.ed.inf.powergrab;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatefulDrone extends Drone {

	private List<ChargingStation> goals;
	private List<ChargingStation> badStations;
	private Position tempGoal;
	private Direction lastMove;

	public StatefulDrone(Position curPosition, double coins, double power) {
		super(curPosition, coins, power);

		this.badStations = MapUtils.getInstance().getchargingStations().stream()
				.filter(stations -> stations.getCoins() < 0 || stations.getPower() < 0).collect(Collectors.toList());

		this.goals = MapUtils.getInstance().getchargingStations().stream().filter(stations -> stations.getCoins() > 0)
				.collect(Collectors.toList());
		searchForGoal();
	}

	@Override
	public Direction decideMoveDirection(List<Direction> directions) {
		if (lastMove != null) {
			directions.remove(lastMove.getDiagonalDirection());
		}

		if (tempGoal == null) {
			return lastMove.getDiagonalDirection();
		}

		directions = directions.stream()
				.filter(dir -> !badStations
						.contains(MapUtils.getInstance().getNearestStationInRange(curPosition.nextPosition(dir))))
				.collect(Collectors.toList());

		Collections.sort(directions, new Comparator<Direction>() {
			@Override
			public int compare(Direction d1, Direction d2) {
				Position p1 = curPosition.nextPosition(d1);
				Position p2 = curPosition.nextPosition(d2);
				return Double.compare(p1.getRelativeDistance(tempGoal), p2.getRelativeDistance(tempGoal));
			}
		});

		return directions.get(0);
	}

	@Override
	public Position move(Direction direction) {
		super.move(direction);
		this.lastMove = direction;
		return curPosition;
	}

	@Override
	public void transfer(ChargingStation chargingStation, double coins, double power) {
		goals.remove(chargingStation);
		searchForGoal();
		super.transfer(chargingStation, coins, power);
	}

	private void searchForGoal() {
		if (goals.size() == 0) {
			tempGoal = null;
			return;
		}

		Collections.sort(goals, new Comparator<ChargingStation>() {
			@Override
			public int compare(ChargingStation s1, ChargingStation s2) {
				return Double.compare(curPosition.getRelativeDistance(s1.getPosition()),
						curPosition.getRelativeDistance(s2.getPosition()));
			}
		});
		this.tempGoal = goals.get(0).getPosition();
	}

//	@Override
//	public Direction move() {
//		Direction moveDirection;
//		TreeMap<ChargingStation, Direction> potentialDirections = new TreeMap<ChargingStation, Direction>(){
//			@Override
//			public Comparator<? super ChargingStation> comparator() {
//				return new Comparator<ChargingStation>() {
//					@Override
//					public int compare(ChargingStation s1, ChargingStation s2) {
//						return (int)(s1.getCoins() - s2.getCoins());
//					}
//				};
//			}
//		};
//		
//		for (Integer i = 0; i < Direction.NUM_OF_DIRECTIONS; i++) {
//			Position tempPosition = curPosition.nextPosition(Direction.valueOf(i.toString()));
//			if (tempPosition.inPlayArea()) {
//				ChargingStation nearestStation = MapUtils.getInstance().getNearestStation(tempPosition);
//				if (nearestStation.getCoins() >= 0)
//					potentialDirections.put(nearestStation, Direction.valueOf(i.toString()));
//			}
//		}
//		
////		this.power --;
////		if (potentialDirections.firstKey().getCoins() > 0) {
////			moveDirection = potentialDirections.firstEntry().getValue();
////			this.curPosition = curPosition.nextPosition(moveDirection);
////			MapUtils.getInstance().getNearestStation(curPosition).transfer(this);
////		} else {
////			moveDirection = ((List<Direction>) potentialDirections.values())
////					.get(rand.nextInt(potentialDirections.size()));
////			this.curPosition = curPosition.nextPosition(moveDirection);
////		}
////		
////		return moveDirection;
//		return null;
//	}

}
