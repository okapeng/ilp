package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerGrab {

	private static final double INITIAL_COINS = 0;
	private static final double INITIAL_POWER = 250;
	private static final int MAX_MOVES = 250;

	private Drone drone;
	private int numOfMoves;
	private StringBuffer movesTrace = new StringBuffer();

	public PowerGrab(Position initPosition, DroneType droneType, int randomSeed) {
		this.numOfMoves = 0;
		switch (droneType) {
		case stateful:
			this.drone = new StatefulDrone(initPosition, INITIAL_COINS, INITIAL_POWER);
			break;
		case stateless:
			this.drone = new StatelessDrone(initPosition, INITIAL_COINS, INITIAL_POWER, randomSeed);
			break;
		default:
			break;
		}
	}

	public String play() {
		while (drone.getPower() > 0 && numOfMoves < MAX_MOVES) {
			Position oldPosition = drone.getPosition();
			Direction moveDirection = drone.decideMoveDirection(getAllowedDirections());
			Position newPosition = drone.move(moveDirection);
			transfer();
			MapUtils.getInstance().drawTrajectory(oldPosition, newPosition);
			movesTrace.append(String.format("%s,%s,%s,%.2f,%.2f\n", oldPosition, moveDirection, newPosition,
					drone.getCoins(), drone.getPower()));
			numOfMoves++;
		}

		System.out.println(drone.coins);
		return movesTrace.toString();
	}

	private void transfer() {
		ChargingStation nearestStation = MapUtils.getInstance().getNearestStationInRange(drone.getPosition());
		double coins = drone.getCoins() + nearestStation.getCoins() > 0 ? nearestStation.getCoins()
				: 0 - drone.getCoins();
		double power = drone.getPower() + nearestStation.getPower() > 0 ? nearestStation.getPower()
				: 0 - drone.getPower();
		nearestStation.transfer(coins, power);
		drone.transfer(nearestStation, coins, power);
	}

	private List<Direction> getAllowedDirections() {
		List<Direction> alloweDirections = new ArrayList<Direction>(Direction.DIRECTIONS);
		return alloweDirections.stream().filter(dir -> drone.getPosition().nextPosition(dir).inPlayArea())
				.collect(Collectors.toList());
	}

}
