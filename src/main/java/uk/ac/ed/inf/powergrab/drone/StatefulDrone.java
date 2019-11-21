package uk.ac.ed.inf.powergrab.drone;

import java.util.ArrayList;
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
	private List<Position> visitedPositions;
	private Direction lastMove;
	private boolean reachGoal = false;
	private boolean reachAllGoals = false;
	private Map map;

	private static final double R = 0.0003;

	public StatefulDrone(Position curPosition, double coins, double power) {
		super(curPosition, coins, power);
		this.map = Map.getInstance();

		this.badStations = map.getchargingStations().stream()
				.filter(stations -> stations.getCoins() <= 0 || stations.getPower() <= 0).collect(Collectors.toList());

		this.goals = map.getchargingStations().stream().filter(stations -> stations.getCoins() > 0)
				.collect(Collectors.toList());

		visitedPositions = new ArrayList<Position>();

		searchForGoal();
	}

	@Override
	public Direction decideMoveDirection(List<Direction> directions) {

		if (reachGoal) {
			visitedPositions = new ArrayList<Position>();
			searchForGoal();
			reachGoal = false;
		}
		if (reachAllGoals) {
			return lastMove.getDiagonalDirection();
		}
//		if (lastMove != null) {
//			directions.remove(lastMove.getDiagonalDirection());
//		}
		if (visitedPositions.size() > 8 && visitedPositions.get(visitedPositions.size() - 4)
				.getRelativeDistance(curPosition) < 2 * Map.MAX_TRANSFER_DISTANCE) {
//			System.out.println(visitedPositions.toString());
			ChargingStation unreachableStation = goals.get(0);
//			System.out.println("unreachable" + unreachableStation);
			goals.remove(unreachableStation);
			searchForGoal();
//			badStations.add(new ChargingStation(curPosition, -1, -1));
//			goals.add(unreachableStation);
			if (reachAllGoals) {
				return lastMove.getDiagonalDirection();
			}
		}

		directions = directions.stream()
				.filter(dir -> !badStations.contains(map.getNearestStationInRange(curPosition.nextPosition(dir))))
				.filter(dir -> !isVisited(curPosition.nextPosition(dir))).collect(Collectors.toList());

		Position tempGoalPosition = goals.get(0).getPosition();
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
//			Direction.DIRECTIONS.forEach(
//					x -> System.out.println(x.toString() + map.getNearestStationInRange(curPosition.nextPosition(x))));
			this.badStations.add(new ChargingStation(curPosition, -10, -10));
			return lastMove.getDiagonalDirection();
		}

		return directions.get(0);
	}

	private boolean isVisited(Position position) {
		int numOfClosePos = this.visitedPositions.stream().parallel().filter(
				visitedPositions -> (position.getRelativeDistance(visitedPositions) < Map.MAX_TRANSFER_DISTANCE))
				.collect(Collectors.toSet()).size();
//		if (numOfClosePos > 0) {
//			System.out.println(position + "\t" + Integer.toString(numOfClosePos));
//		}
		return numOfClosePos > 0;
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
			badStations.add(chargingStation);
			reachGoal = true;
		}
		visitedPositions.add(curPosition);
		super.transfer(chargingStation, coins, power);
	}

	private void searchForGoal() {
		reachAllGoals = (goals.size() == 0);
		visitedPositions = new ArrayList<Position>();

		Collections.sort(goals, new Comparator<ChargingStation>() {
			@Override
			public int compare(ChargingStation s1, ChargingStation s2) {
				return Double.compare(curPosition.getRelativeDistance(s1.getPosition()),
						curPosition.getRelativeDistance(s2.getPosition()));
			}
		});
	}

}
