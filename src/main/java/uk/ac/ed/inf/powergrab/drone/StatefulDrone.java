package uk.ac.ed.inf.powergrab.drone;

import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

import java.util.*;
import java.util.stream.Collectors;

public class StatefulDrone extends Drone {

    private final static int MAX_SEARCH_DEPTH = 1250;
    private static final double R = 0.0003;
    private static final double BAD_RANGE = 0.00015;
    private Stack<Direction> route;
    private Random rand;
    //    private List<ChargingStation> goals;
    private List<ChargingStation> badStations;
    private ChargingStation goal;
    private Direction lastMove;

    public StatefulDrone(Position curPosition, double coins, double power, int seed) {
        super(curPosition, coins, power, seed);
//        this.goals = Map.getInstance().getChargingStations().stream().filter(stations -> stations.getCoins() > 0)
//                .collect(Collectors.toList());
        this.badStations = Map.getInstance().getChargingStations().stream().filter(stations -> stations.getCoins() < 0)
                .collect(Collectors.toList());
        this.rand = new Random(seed);
        searchForGoal();
    }

    @Override
    public Direction decideMoveDirection(List<Direction> directions) {
        if (route.isEmpty() && !searchForGoal()) {
            System.out.println("cannot find route");
            List<Direction> safeDirections = getSafeDirections(curPosition);
            safeDirections.remove(lastMove.getOppositeDirection());
            safeDirections.sort(Comparator.comparingDouble(dir -> curPosition.nextPosition(dir).getRelativeDistance(this.goal.getPosition())));
//            return safeDirections.get(0);
            return safeDirections.get(rand.nextInt(Math.min(safeDirections.size(), 3)));
        }
//        if (goals.isEmpty() && lastMove != null) {
        if (route.isEmpty() && lastMove != null) {
            return lastMove.getOppositeDirection();
        }
        return route.get(0);
    }

    @Override
    public void transfer(ChargingStation chargingStation, double coins, double power) {
//        goals.remove(chargingStation);
        if (goal == chargingStation) goal = null;
        super.transfer(chargingStation, coins, power);
    }

    @Override
    public boolean move(Direction direction) {
        if (!this.route.empty() && route.indexOf(direction) == 0) {
            this.route.remove(0);
        }
        this.lastMove = direction;
        return super.move(direction);
    }

    private boolean searchForRoute() {
        HashMap<Position, Stack<Direction>> initFrontier = new HashMap<>();
        initFrontier.put(curPosition, new Stack<>());
        return searchForRouteRec(initFrontier, 0, new ArrayList<>());
    }

    private boolean searchForRouteRec(HashMap<Position, Stack<Direction>> frontier, int depth, List<Position> explored) {
        if (depth > MAX_SEARCH_DEPTH)
            return false;

        Optional<Position> closestPositionOp = frontier.keySet().stream()
                .min(Comparator.comparingDouble(pos -> (pos.getRelativeDistance(goal.getPosition()) + frontier.get(pos).size() * R)));
        if (!closestPositionOp.isPresent()) {
            return false;
        }
        Position closestPosition = closestPositionOp.get();

        List<Direction> safeDirections = getSafeDirections(closestPosition);
        safeDirections.removeIf(dir -> isExplored(explored, closestPosition.nextPosition(dir)));
        if (safeDirections.size() > 1) {
            depth++;
            for (Direction dir : safeDirections) {
                Position nextPosition = closestPosition.nextPosition(dir);
                ChargingStation potentialStation = Map.getInstance().getNearestStationInRange(nextPosition);
                if (potentialStation.getCoins() > 0
                        && frontier.get(closestPosition).size() > 0) {
                    this.goal = potentialStation;
                    this.route = frontier.get(closestPosition);
                    route.push(dir);
                    return true;
                }
                Stack<Direction> tempRoute = new Stack<>();
                tempRoute.addAll(frontier.get(closestPosition));
                tempRoute.push(dir);
                frontier.put(nextPosition, tempRoute);
            }
        }

        frontier.remove(closestPosition);
        explored.add(closestPosition);
        return searchForRouteRec(frontier, depth, explored);

    }

    private List<Direction> getSafeDirections(Position position) {
        List<Direction> safeDirections = position.getValidDirections();
        safeDirections.removeIf(dir -> badStations.contains(Map.getInstance().getNearestStationInRange(position.nextPosition(dir))));
        return safeDirections;
    }

    private boolean isExplored(List<Position> explored, Position position) {
        long numOfClosePos = explored.stream().parallel()
                .filter(visitedPositions -> (position.getRelativeDistance(visitedPositions) < BAD_RANGE)).count();
        return numOfClosePos > 0;
    }

    private boolean searchForGoal() {
        List<ChargingStation> goals = Map.getInstance().getChargingStations().stream()
                .filter(stations -> stations.getCoins() > 0)
                .collect(Collectors.toList());
        if (goals.isEmpty())
            return true;
        this.route = new Stack<>();
        goals.sort(Comparator.comparingDouble(g -> curPosition.getRelativeDistance(g.getPosition())));
//        return searchForRoute();
        while (route.isEmpty() && !goals.isEmpty()) {
            this.goal = goals.get(0);
            searchForRoute();
            goals.remove(0);
        }
        return !route.isEmpty();
    }
}
