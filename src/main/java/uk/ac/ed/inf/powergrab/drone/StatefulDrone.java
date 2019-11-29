package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.*;
import java.util.stream.Collectors;

public class StatefulDrone extends Drone {

    private final static int MAX_SEARCH_DEPTH = 250;
    private static final double R = 0.0003;
    private static final double BAD_RANGE = 0.0001;
    private static final double DISTANCE_WEIGHT = 2;

    private Stack<Direction> route;
    private Random rand;
    private List<ChargingStation> badStations;
    private ChargingStation goal;
    private Direction backwardsDirection;
    private boolean reachAllGoals;

    public StatefulDrone(Position curPosition, double coins, double power, int seed) {
        super(curPosition, coins, power, seed);
        this.badStations = Map.getInstance().getChargingStations().stream().filter(stations -> stations.getCoins() < 0)
                .collect(Collectors.toList());
        this.rand = new Random(seed);
        this.reachAllGoals = false;
        searchForGoal();
    }

    @Override
    public Direction decideMoveDirection(List<Direction> directions) {
        if (reachAllGoals && backwardsDirection != null) return backwardsDirection;
        if (route.isEmpty() && !reachAllGoals) {
            List<Direction> safeDirections = getSafeDirections(curPosition);
//            safeDirections.remove(lastMove.getOppositeDirection());
            safeDirections.sort(Comparator.comparingDouble(dir -> curPosition.nextPosition(dir).getRelativeDistance(this.goal.getPosition())));
            return safeDirections.get(rand.nextInt(Math.min(safeDirections.size(), 3)));
        }
        return route.pop();
    }

    @Override
    public void transfer(ChargingStation chargingStation, double coins, double power) {
        if (coins != 0) searchForGoal();
        super.transfer(chargingStation, coins, power);
    }

    @Override
    public boolean move(Direction direction) {
        this.backwardsDirection = direction.getOppositeDirection();
        return super.move(direction);
    }

    private void searchForGoal() {
        List<ChargingStation> goals = Map.getInstance().getChargingStations().stream()
                .filter(stations -> stations.getCoins() > 0)
                .collect(Collectors.toList());
        this.reachAllGoals = goals.isEmpty();
//        if (reachAllGoals) return;

        goals.sort(Comparator.comparingDouble(g -> curPosition.getRelativeDistance(g.getPosition())));
        this.route = new Stack<>();
        while (route.isEmpty() && !goals.isEmpty()) {
            this.goal = goals.get(0);
            searchForRoute();
            goals.remove(0);
        }
    }

    private void searchForRoute() {
        HashMap<Position, Stack<Direction>> initFrontier = new HashMap<>();
        initFrontier.put(curPosition, new Stack<>());
        searchForRouteRec(initFrontier, 0, new ArrayList<>());
    }

    private void searchForRouteRec(HashMap<Position, Stack<Direction>> frontier, int depth, List<Position> explored) {
        if (depth > MAX_SEARCH_DEPTH || frontier.isEmpty()) return;

        java.util.Map.Entry<Position, Stack<Direction>> closestEntry = frontier.entrySet().stream()
                .min(Comparator.comparingDouble(this::heuristic)).get();
        Position closestPosition = closestEntry.getKey();
        Stack<Direction> bestRoute = closestEntry.getValue();

        List<Direction> newDirections = getSafeDirections(closestPosition);
        newDirections.removeIf(dir -> isExplored(explored, closestPosition.nextPosition(dir)));

        if (newDirections.size() > 1) {
            depth++;
            for (Direction dir : newDirections) {
                Position nextPosition = closestPosition.nextPosition(dir);
                ChargingStation potentialStation = Map.getInstance().getNearestStationInRange(nextPosition);
                Stack<Direction> nextRoute = new Stack<>();
                nextRoute.addAll(bestRoute);
                nextRoute.add(0, dir);
                if (potentialStation.getCoins() > 0 && nextRoute.size() > 0) {
                    this.goal = potentialStation;
                    this.route = nextRoute;
                    return;
                }
                frontier.put(nextPosition, nextRoute);
            }
        }

        frontier.remove(closestPosition);
        explored.add(closestPosition);
        searchForRouteRec(frontier, depth, explored);
    }

    private double heuristic(java.util.Map.Entry<Position, Stack<Direction>> entry) {
        return entry.getKey().getRelativeDistance(goal.getPosition()) * DISTANCE_WEIGHT + entry.getValue().size() * R;
    }

    private List<Direction> getSafeDirections(Position position) {
        List<Direction> safeDirections = position.getValidDirections();
        safeDirections.removeIf(dir -> badStations.contains(Map.getInstance().getNearestStationInRange(position.nextPosition(dir))));
//        safeDirections.removeIf(dir -> Map.getInstance().getNearestStationInRange(position.nextPosition(dir)).getCoins() < 0);
        return safeDirections;
    }

    private boolean isExplored(List<Position> explored, Position position) {
        long numOfClosePos = explored.stream().parallel()
                .filter(visitedPositions -> (position.getRelativeDistance(visitedPositions) < BAD_RANGE)).count();
        return numOfClosePos > 0;
    }
}
