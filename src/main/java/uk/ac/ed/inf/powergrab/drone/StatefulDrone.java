package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Stateful drone
 *
 * @author ivy
 */
public class StatefulDrone extends Drone {

    private Stack<Direction> route; // A stack of direction to reach the closest positive station
    private Direction backwardsDirection; // The direction back to last position
    private boolean reachAllGoals; // Whether the drone has visited all the positive charging station in the map

    public StatefulDrone(Position curPosition, double coins, double power, int seed) {
        super(curPosition, coins, power, seed);
        this.reachAllGoals = false;
        searchForGoal(); // Search for the first goal
    }

    /**
     * Because the search algorithm has already find the route to the closest positive station
     * Stateful drone simply follows the route.
     * If the drone has visited all the positive stations in the map, move back to the last position
     * because it's guaranteed to be safe
     *
     * @param directions All the possible directions the drone can choose
     * @return
     */
    @Override
    public Direction decideMoveDirection(List<Direction> directions) {
        if (reachAllGoals && backwardsDirection != null) return backwardsDirection;
        return route.pop();
    }

    /**
     * If the drone transfers coins with any of the stations
     * or its planed route is empty, search for new goal
     *
     * @param coins the amount of coins to be transferred
     * @param power the amount of power to be transferred
     */
    @Override
    public void transfer(double coins, double power) {
        if (coins != 0 || this.route.empty()) searchForGoal();
        super.transfer(coins, power);
    }

    /**
     * In addition to change the current position, the stateful drone also remember the
     * direction back to last position
     * @param direction to be moved
     * @return whether the drone has enough power to move
     */
    @Override
    public boolean move(Direction direction) {
        this.backwardsDirection = direction.getOppositeDirection();
        return super.move(direction);
    }

    /**
     * Find all the positive charging stations in the map and sort them according their
     * relative distance to the drone. Select the closest station as goal. Instantiate
     * a RouteFinder and pass the current position and goal to it to find the route to goal.
     * If there is no positive stations left in the map, update reachAllGoals and stop searching.
     */
    private void searchForGoal() {
        List<ChargingStation> goals = Map.getInstance().getChargingStations().stream()
                .filter(stations -> stations.getCoins() > 0)
                .collect(Collectors.toList());
        this.reachAllGoals = goals.isEmpty();
        if (reachAllGoals) return;

        goals.sort(Comparator.comparingDouble(g -> curPosition.getRelativeDistance(g.getPosition())));
        RouteFinder routeFinder = new RouteFinder(curPosition, goals.get(0));
        this.route = routeFinder.search();
    }

    /**
     * Inner class: given a goal and initial position find the shortest route to the goal
     */
    private class RouteFinder {
        private static final int MAX_SEARCH_DEPTH = 250;
        private static final double HISTORY_WEIGHT = 0.0003;
        private static final double DISTANCE_WEIGHT = 2;
        private static final double BAD_RANGE = 0.0001;

        private ChargingStation goal;
        private Position initPosition;
        private HashMap<Position, Stack<Direction>> frontier = new HashMap<>();
        private List<Position> explored = new ArrayList<>();
        private int depth = 0;

        /**
         * Constructor of RouteFinder, initialise the frontier with the initial position
         * and an empty route (stack)
         *
         * @param initPosition initial position of the drone
         * @param goal         the charging station aiming to reach
         */
        RouteFinder(Position initPosition, ChargingStation goal) {
            this.goal = goal;
            this.initPosition = initPosition;
            frontier.put(curPosition, new Stack<>());
        }

        /**
         * Recursively expand the entry in frontier that is closest to the goal (the distance is
         * defined by the heuristic) until it reaches the goal or another positive charging station.
         * The entry being expanded will be added to the explored list. The search will terminate
         * either any of the positive station is reached (not necessary the initial goal), the
         * maximum depth of the search is reached or the frontier is empty.
         *
         * @return a stack of direction representing the route to the goal if found one
         * otherwise return a stack with a direction choosing from the first three closest possibilities
         */
        Stack<Direction> search() {
            if (depth > MAX_SEARCH_DEPTH || frontier.isEmpty()) return oneStepTowardsGoal();

            java.util.Map.Entry<Position, Stack<Direction>> closestEntry = frontier.entrySet().stream()
                    .min(Comparator.comparingDouble(this::heuristic)).get();
            Position closestPosition = closestEntry.getKey();
            Stack<Direction> bestRoute = closestEntry.getValue();
            frontier.remove(closestPosition);
            explored.add(closestPosition);

            for (Direction dir : getUnexploredDirections(closestPosition)) {
                Position nextPosition = closestPosition.nextPosition(dir);
                Stack<Direction> nextRoute = new Stack<>();
                nextRoute.addAll(bestRoute);
                nextRoute.add(0, dir);
                if (Map.getInstance().getNearestStationInRange(nextPosition).getCoins() > 0 && nextRoute.size() > 0)
                    return nextRoute;
                frontier.put(nextPosition, nextRoute);
            }

            depth++;
            return search();
        }

        /**
         * If the search algorithm fails to find a route to the goal or any of the positive charging station
         * return the direction randomly choosing from the first three directions moving towards the goal
         *
         * @return
         */
        private Stack<Direction> oneStepTowardsGoal() {
            List<Direction> bestNextMove = getUnexploredDirections(initPosition).stream()
                    .sorted(Comparator.comparingDouble(dir -> initPosition.nextPosition(dir).getRelativeDistance(goal.getPosition())))
                    .collect(Collectors.toList());
            Stack<Direction> route = new Stack<>();
            route.add(bestNextMove.get(rand.nextInt(Math.min(bestNextMove.size(), 3))));
            return route;
        }

        /**
         * The heuristic for the searching algorithm, defined by the relative distance from the
         * position to the goal and the number of steps already moves, each times by a weight.
         *
         * @param entry is an element in the frontier
         * @return the estimated distance between it and the goal
         */
        private double heuristic(java.util.Map.Entry<Position, Stack<Direction>> entry) {
            return entry.getKey().getRelativeDistance(goal.getPosition()) * DISTANCE_WEIGHT
                    + entry.getValue().size() * HISTORY_WEIGHT;
        }

        /**
         * Given a position find all the valid directions it can move to without crashing into negative
         * charging station or being too close to a position already explored
         *
         * @param position a position in frontier
         * @return list of valid directions that are safe and not explored previously
         */
        private List<Direction> getUnexploredDirections(Position position) {
            List<Direction> directions = new ArrayList<>(Direction.DIRECTIONS);
            directions.removeIf(dir -> !position.nextPosition(dir).inPlayArea());
            directions.removeIf(dir -> Map.getInstance().getNearestStationInRange(position.nextPosition(dir)).getCoins() < 0);
            directions.removeIf(dir -> isExplored(position.nextPosition(dir)));
            return directions;
        }

        /**
         * Check whether the input position is too close to any of the previously explored position
         * i.e. within the distance defined by BAD_RANGE
         *
         * @param position
         * @return whether it's in the region of explored
         */
        private boolean isExplored(Position position) {
            long numOfClosePos = explored.stream().parallel()
                    .filter(visitedPositions -> (position.getRelativeDistance(visitedPositions) < BAD_RANGE)).count();
            return numOfClosePos > 0;
        }

    }

}
