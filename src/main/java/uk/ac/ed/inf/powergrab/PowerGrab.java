package uk.ac.ed.inf.powergrab;

import uk.ac.ed.inf.powergrab.drone.Drone;
import uk.ac.ed.inf.powergrab.drone.DroneType;
import uk.ac.ed.inf.powergrab.drone.StatefulDrone;
import uk.ac.ed.inf.powergrab.drone.StatelessDrone;
import uk.ac.ed.inf.powergrab.map.ChargingStation;
import uk.ac.ed.inf.powergrab.map.Direction;
import uk.ac.ed.inf.powergrab.map.Map;
import uk.ac.ed.inf.powergrab.map.Position;

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
		System.out.print(Map.getInstance().getchargingStations().stream().map(ChargingStation::getCoins)
				.filter(x -> x > 0).reduce(Double::sum) + "\t");
		while (drone.getPower() > 0 && numOfMoves < MAX_MOVES) {
			Position oldPosition = drone.getPosition();
			Direction moveDirection = drone.decideMoveDirection(drone.getPosition().getAllowedDirections());
			Position newPosition = drone.move(moveDirection);
			transfer();
			Map.getInstance().drawTrajectory(oldPosition, newPosition);
			movesTrace.append(String.format("%s,%s,%s,%.2f,%.2f\n", oldPosition, moveDirection, newPosition,
					drone.getCoins(), drone.getPower()));
			numOfMoves++;
		}

		System.out.println("Final coins: " + drone.getCoins());
		return movesTrace.toString();
	}

	private void transfer() {
		ChargingStation nearestStation = Map.getInstance().getNearestStationInRange(drone.getPosition());
		double coins = drone.getCoins() + nearestStation.getCoins() > 0 ? nearestStation.getCoins()
				: 0 - drone.getCoins();
		double power = drone.getPower() + nearestStation.getPower() > 0 ? nearestStation.getPower()
				: 0 - drone.getPower();
		nearestStation.transfer(coins, power);
		drone.transfer(nearestStation, coins, power);
		if (coins < 0) {
			System.out.println("Drone crashes negative station");
		}
	}

}
