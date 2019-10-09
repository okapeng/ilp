package uk.ac.ed.inf.powergrab;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class StatelessDrone extends Drone {

	private Random rand;

	public StatelessDrone(Position curPosition, double coins, double power, int seed) {
		super(curPosition, coins, power);
		this.rand = new Random(seed);
	}

	public Direction move() {
		Direction moveDirection;
		TreeMap<ChargingStation, Direction> potentialDirections = new TreeMap<ChargingStation, Direction>(){
			@Override
			public Comparator<? super ChargingStation> comparator() {
				return new Comparator<ChargingStation>() {
					@Override
					public int compare(ChargingStation s1, ChargingStation s2) {
						return (int)(s1.getCoins() - s2.getCoins());
					}
				};
			}
		};
		
		for (Integer i = 0; i < Direction.NUM_OF_DIRECTIONS; i++) {
			Position tempPosition = curPosition.nextPosition(Direction.valueOf(i.toString()));
			if (tempPosition.inPlayArea()) {
				ChargingStation nearestStation = MapUtils.getInstance().getNearestStation(tempPosition);
				if (nearestStation.getCoins() >= 0)
					potentialDirections.put(nearestStation, Direction.valueOf(i.toString()));
			}
		}
		
		this.power --;
		if (potentialDirections.firstKey().getCoins() > 0) {
			moveDirection = potentialDirections.firstEntry().getValue();
			this.curPosition = curPosition.nextPosition(moveDirection);
			MapUtils.getInstance().getNearestStation(curPosition).transfer(this);
		} else {
			moveDirection = ((List<Direction>) potentialDirections.values())
					.get(rand.nextInt(potentialDirections.size()));
			this.curPosition = curPosition.nextPosition(moveDirection);
		}
		
		return moveDirection;
	}

}
