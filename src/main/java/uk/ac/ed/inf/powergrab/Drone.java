package uk.ac.ed.inf.powergrab;

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
	
	public Position getCurPosition() {
		return curPosition;
	}

	public double getCoins() {
		return coins;
	}


	public double getPower() {
		return power;
	}

	public void setCurPosition(Position curPosition) {
		this.curPosition = curPosition;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}

	public void setPower(double power) {
		this.power = power;
	}



	public abstract Direction move();
	
}
