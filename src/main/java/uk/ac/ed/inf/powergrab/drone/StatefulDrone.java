package uk.ac.ed.inf.powergrab.drone;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

public class StatefulDrone extends Drone {

	private List<ChargingStation> goals;
	private List<ChargingStation> badStations;
	private Direction lastMove;
	private boolean reachAllGoals = false;
	private Map map;

	private final int MAX_DEEPTH = 3;
	private final double RANGE_APPLY_SEARCH = 0.0006;
	private static final double R = 0.0003;

	public StatefulDrone(Position curPosition, double coins, double power) {
		super(curPosition, coins, power);
		this.map = Map.getInstance();

		this.badStations = map.getchargingStations().stream()
				.filter(stations -> stations.getCoins() < 0 || stations.getPower() < 0).collect(Collectors.toList());

		this.goals = map.getchargingStations().stream().filter(stations -> stations.getCoins() > 0)
				.collect(Collectors.toList());
		searchForGoal();
	}

	@Override
	public Direction decideMoveDirection(List<Direction> directions) {

		if (map.getNearestStationInRange(curPosition) != null) {
			searchForGoal();
		}
		if (reachAllGoals) {
			return lastMove.getDiagonalDirection();
		}

		if (lastMove != null) {
			directions.remove(lastMove.getDiagonalDirection());
		}
		Position tempGoalPosition = goals.get(0).getPosition();

		directions = directions.stream()
				.filter(dir -> !badStations.contains(map.getNearestStationInRange(curPosition.nextPosition(dir))))
				.collect(Collectors.toList());

		Collections.sort(directions, new Comparator<Direction>() {
			@Override
			public int compare(Direction d1, Direction d2) {
				Position p1 = curPosition.nextPosition(d1);
				Position p2 = curPosition.nextPosition(d2);
				return Double.compare(p1.getRelativeDistance(tempGoalPosition),
						p2.getRelativeDistance(tempGoalPosition));
			}
		});

		if (directions.isEmpty()) {
			Direction.DIRECTIONS.forEach(
					x -> System.out.println(x.toString() + map.getNearestStationInRange(curPosition.nextPosition(x))));
			this.badStations.add(new ChargingStation(curPosition, -1, -1));
			return lastMove.getDiagonalDirection();
		}

		Direction tempNextDirection = directions.get(0);

		boolean canReach = curPosition.nextPosition(tempNextDirection).getRelativeDistance(tempGoalPosition) < R;
		boolean canTransform = map.getNearestStationInRange(curPosition.nextPosition(tempNextDirection)) == goals
				.get(0);

		if (canReach && !canTransform) {
			directions.remove(tempNextDirection);
			return decideMoveDirection(directions);
		}

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
		if (goals.contains(chargingStation)) {
			goals.remove(chargingStation);
		}
		super.transfer(chargingStation, coins, power);
	}

	private void searchForGoal() {
		if (goals.size() == 0) {
			reachAllGoals = true;
			return;
		}

		Collections.sort(goals, new Comparator<ChargingStation>() {
			@Override
			public int compare(ChargingStation s1, ChargingStation s2) {
				return Double.compare(curPosition.getRelativeDistance(s1.getPosition()),
						curPosition.getRelativeDistance(s2.getPosition()));
			}
		});
	}

}
