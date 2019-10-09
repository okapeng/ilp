package uk.ac.ed.inf.powergrab;

public class PowerGrab {
	
	private static final double INITIAL_COINS = 0;
	private static final double INITIAL_POWER = 250;
	private static final int MAX_MOVES = 250;
	
	private Position initPosition;
	private DroneType droneType;
	private int randomSeed;
	private int moves;

	public PowerGrab(Position initPosition, DroneType droneType, int randomSeed) {
		this.initPosition = initPosition;
		this.droneType = droneType;
		this.randomSeed = randomSeed;
		this.moves = 0;
	}

	public void play() {
		switch (droneType) {
		case stateful:
			// TODO 
			break;
		case stateless:
			playStateless(initPosition, randomSeed);
			break;
		default:
			return;
		}
	}
	
	private void playStateless(Position initPosition, int seed) {
		Drone drone = new StatelessDrone(initPosition, INITIAL_COINS, INITIAL_POWER, seed);
		while (drone.getPower() > 0 && moves < MAX_MOVES) {
			drone.move();
			moves++;
		}
	}
}
