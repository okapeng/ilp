package uk.ac.ed.inf.powergrab.drone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ed.inf.powergrab.map.*;

/**
 * Stateful drone implementation
 * @author ivy
 *
 */
public class StatefulDrone extends Drone {

	private List<ChargingStation> goals; // List of all the unvisited positive charging stations in the map.
	private List<ChargingStation> badStations; // List of all the negative charging stations.
	private List<Position> visitedPositions; // List of positions visited since last charging.
	private List<Position> badPositions; // List of positions resulted in negative charging station or bad positions in all valid directions
	private Direction lastMove; // Direction of previous movement

	private static final double BAD_RANGE = 0.00015; // Maximum distance for a drone to be considered as close to a bad position 

	/**
	 * Initialise the Drone state and store the charging stations' information extracted from the map
	 */
	public StatefulDrone(Position curPosition, double coins, double power) {
		super(curPosition, coins, power);

		this.badStations = Map.getInstance().getchargingStations().stream()
				.filter(stations -> stations.getCoins() <= 0 || stations.getPower() <= 0).collect(Collectors.toList());

		this.goals = Map.getInstance().getchargingStations().stream().filter(stations -> stations.getCoins() > 0)
				.collect(Collectors.toList());

		visitedPositions = new ArrayList<Position>();
		badPositions = new ArrayList<Position>();

		searchForGoal(); // Sort the positive charging station according to their relative distance to the drone
	}

	/**
	 * Decide the direction for next movement, override the abstract method in Drone class
	 */
	@Override
	public Direction decideMoveDirection(List<Direction> directions) {

		if (lastMove != null) {
			directions.remove(lastMove.getDiagonalDirection());
			if (goals.isEmpty()) return lastMove.getDiagonalDirection();
		}
		
		if (visitedPositions.size() > 6 && visitedPositions.get(visitedPositions.size() - 4)
				.getRelativeDistance(curPosition) < 3 * BAD_RANGE) {
			resetGoal();
		}
		
		directions = directions.stream()
				.filter(dir -> !badStations.contains(Map.getInstance().getNearestStationInRange(curPosition.nextPosition(dir)))).collect(Collectors.toList());
		List<Direction> niceDirections = directions.stream()
				.filter(dir -> !isNearBad(curPosition.nextPosition(dir))).collect(Collectors.toList());

		Position tempGoalPosition = goals.get(0).getPosition();
		Collections.sort(niceDirections, new DirectionComparator(tempGoalPosition));
		
		if (niceDirections.isEmpty()) {
			Collections.sort(directions, new DirectionComparator(tempGoalPosition));
			this.badPositions.add(curPosition);
			return directions.isEmpty() ? lastMove.getDiagonalDirection() : directions.get(0);
		}

		return niceDirections.get(0);
	}
	
	/**
	 * If the drone get stuck during its attempt to reach a charging station, try to break the tie by changing the goal to another station
	 * Also add the current position to the list of bad positions as it doesn't help with reaching a goal  
	 */
	private void resetGoal() {
		ChargingStation unreachableStation = goals.get(0);
		goals.remove(unreachableStation);
		searchForGoal();
		badPositions.add(curPosition); 
		goals.add(unreachableStation); // Still keeps this station as a goal, as it might be possible to reach this station later
	}

	/**
	 * Check if position is near one of the bad positions
	 * @param position The position to be checked
	 * @return Whether it is within the range closer enough to one of the bad stations 
	 */
	private boolean isNearBad(Position position) {
		int numOfClosePos = this.badPositions.stream().parallel().filter(
				visitedPositions -> (position.getRelativeDistance(visitedPositions) < BAD_RANGE))
				.collect(Collectors.toSet()).size();
		return numOfClosePos > 0;
	}

	/**
	 * In addition to adjust the drone state, keep a record of the last direction the drone decided to move
	 */
	@Override
	public Position move(Direction direction) {
		super.move(direction);
		this.lastMove = direction;
		return curPosition;
	}

	/**
	 * Transfer 
	 */
	@Override
	public void transfer(ChargingStation chargingStation, double coins, double power) {
		if (goals.contains(chargingStation)) {
			goals.remove(chargingStation);
			searchForGoal();
		}
		visitedPositions.add(curPosition);
		super.transfer(chargingStation, coins, power);
	}

	private void searchForGoal() {
		visitedPositions = new ArrayList<Position>();

		Collections.sort(goals, new Comparator<ChargingStation>() {
			@Override
			public int compare(ChargingStation s1, ChargingStation s2) {
				return Double.compare(curPosition.getRelativeDistance(s1.getPosition()),
						curPosition.getRelativeDistance(s2.getPosition()));
			}
		});
	}
	
	private class DirectionComparator implements Comparator<Direction> {
		private Position goal;
		
		public DirectionComparator(Position goal) {
			this.goal = goal;
		}

		@Override
		public int compare(Direction d1, Direction d2) {
			Position p1 = curPosition.nextPosition(d1);
			Position p2 = curPosition.nextPosition(d2);
			return Double.compare(p1.getRelativeDistance(goal),
					p2.getRelativeDistance(goal));
		}
	}

}
