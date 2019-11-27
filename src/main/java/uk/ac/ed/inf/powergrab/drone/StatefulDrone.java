package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateful drone
 * 
 * @author ivy
 *
 */
public class StatefulDrone extends Drone {

	private List<ChargingStation> goals; // List of all the unvisited positive charging stations in the map.
	private List<Position> visitedPositions; // List of positions visited since last charging.
	private List<Position> badPositions; // List of positions result in dead end
	private Direction backwardsDirection; // Direction to go back to last position

	private static final double BAD_RANGE = 0.00015; // Maximum distance for a drone to be considered as close to a bad
														// position

	/**
	 * Initialize the Drone state and store the charging stations' information
	 * extracted from the map
	 */
	public StatefulDrone(Position curPosition, double coins, double power, int seed) {
		super(curPosition, coins, power, seed);

		this.goals = Map.getInstance().getChargingStations().stream().filter(stations -> stations.getCoins() > 0)
				.collect(Collectors.toList());

		visitedPositions = new ArrayList<>();
		badPositions = new ArrayList<>();

		searchForGoal(); // Search for initial goal
	}

	/**
	 * Implement the strategy for stateful drone to decide next moving direction
	 */
	@Override
	public Direction decideMoveDirection(List<Direction> directions) {

		if (backwardsDirection != null) {
			/*
			 * If finish visiting all the positive charging station in the map, stop moving
			 * to new position and jump between last and current position which are
			 * guaranteed to be safe
			 */
			if (goals.isEmpty())
				return backwardsDirection;
			// Otherwise avoid moving backwards
			directions.remove(backwardsDirection);
		}

		/*
		 * Detect if the drone get suck in a small area and if so change the goal
		 */
		correctNonProgressiveMoves();
		Position tempGoal = goals.get(0).getPosition();

		// Filter out directions crashing into negative charging stations
		directions.removeIf(
				dir -> Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)).getCoins() < 0);

		// Sort the directions according to whether it is moving towards the goal
		directions.sort((d1, d2) -> {
			Position p1 = curPosition.nextPosition(d1);
			Position p2 = curPosition.nextPosition(d2);
			return Double.compare(p1.getRelativeDistance(tempGoal), p2.getRelativeDistance(tempGoal));
		});

		// Filter out directions close to bad positions i.e. dead end
		List<Direction> niceDirections = directions.stream().filter(dir -> !isNearBad(curPosition.nextPosition(dir)))
				.collect(Collectors.toList());

		// If no direction remains after the second filter, relax the constraint and
		// mark current position as bad
		if (niceDirections.isEmpty()) {
			this.badPositions.add(curPosition);
			return directions.isEmpty() ? backwardsDirection : directions.get(0);
		}

		return niceDirections.get(0);
	}

	/**
	 * In addition to adjust the drone state, record the direction moving backwards
	 */
	@Override
	public boolean move(Direction direction) {
		this.backwardsDirection = direction.getOppositeDirection();
		return super.move(direction);
	}

	/**
	 * Transfer coins and power from charging station. If the drone reaches a
	 * goal/positive charging station remove it from the list of goals and search
	 * for a new one.
	 */
	@Override
	public void transfer(ChargingStation chargingStation, double coins, double power) {
		if (goals.contains(chargingStation)) {
			goals.remove(chargingStation);
			searchForGoal();
		}
		// Keep a record of positions visited since last time reaching a goal
		visitedPositions.add(curPosition);
		super.transfer(chargingStation, coins, power);
	}

	/**
	 * If the drone get stuck during its attempt to reach a goal station, try to
	 * break the tie by changing the goal to another station. Also add the current
	 * position to the list of bad positions as it doesn't help with reaching the
	 * goal
	 */
	private void correctNonProgressiveMoves() {
		if (visitedPositions.size() > 6
				&& visitedPositions.get(visitedPositions.size() - 4).getRelativeDistance(curPosition) < 3 * BAD_RANGE) {
			ChargingStation unreachableStation = goals.get(0);
			goals.remove(unreachableStation);
			searchForGoal();
			badPositions.add(curPosition);
			// Still keeps this station as a goal, as it might be possible to reach later
			goals.add(unreachableStation);
		}
	}

	/**
	 * Check if position is near one of the bad positions. A position is bad if the
	 * drone ends up in dead end or making non-progressive moves
	 * 
	 * @param position The position to be checked
	 * @return Whether it is within the range closer enough to one of the bad
	 *         stations
	 */
	private boolean isNearBad(Position position) {
		long numOfClosePos = this.badPositions.stream().parallel()
				.filter(visitedPositions -> (position.getRelativeDistance(visitedPositions) < BAD_RANGE)).count();
		return numOfClosePos > 0;
	}

	/**
	 * Sort the goal list according to its relative distance to the drone.
	 * Reset positions visited since last time reach a goal.
	 */
	private void searchForGoal() {
		visitedPositions = new ArrayList<>();
		goals.sort(Comparator.comparingDouble(g -> curPosition.getRelativeDistance(g.getPosition())));
	}

}
