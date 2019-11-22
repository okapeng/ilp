package uk.ac.ed.inf.powergrab.drone;

import java.util.List;

import uk.ac.ed.inf.powergrab.map.*;

public abstract class Drone {

	protected Position curPosition;
	protected double coins;
	protected double power;

	public Drone(Position curPosition, double coins, double power) {
		super();
		this.curPosition = curPosition;
		this.coins = coins;
		this.power = power;
	}

	public Position getPosition() {
		return curPosition;
	}

	public double getCoins() {
		return coins;
	}

	public double getPower() {
		return power;
	}

	public void setPosition(Position position) {
		this.curPosition = position;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public void transfer(ChargingStation chargingStation, double coins, double power) {
		this.coins += coins;
		this.power += power;
	}

	public Position move(Direction direction) {
		this.curPosition = curPosition.nextPosition(direction);
		this.power -= 1.25;
		return this.curPosition;
	}

	public abstract Direction decideMoveDirection(List<Direction> directions);

	@Override
	public String toString() {
		return "Drone [curPosition=" + curPosition + ", coins=" + coins + ", power=" + power + "]";
	}

}
