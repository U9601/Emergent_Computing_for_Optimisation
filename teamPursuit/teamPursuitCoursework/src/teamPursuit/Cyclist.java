package teamPursuit;

class Cyclist {
	static final int MAX_POWER = 1200;
	static final int MIN_POWER = 200;
	private static final double DRAG_COEFFICIENT = 0.65;
	private static final double MECHANICAL_EFFICIENCY = 0.977;
	private static final double BIKE_MASS = 7.7;
	
	private double height;
	private double weight;
	private double coefficientDragArea;
	private double meanMaximumPower;
	private TeamPursuit event;
	private double totalEnergy;
	private double remainingEnergy;
	private double currentVelocity;
	private int startPosition;
	private int position;
	private int fatigueLevel = 0;
	
	Cyclist(double height, double weight, double meanMaximumPower, TeamPursuit event, int startPosition) {
		this.height = height;
		this.weight = weight;
		this.meanMaximumPower = meanMaximumPower;
		this.event = event;
		this.startPosition = startPosition;
		this.position = startPosition;
		this.currentVelocity = 0.0;
		updateCDA();
		updateTotalEnergy();
		this.remainingEnergy = this.totalEnergy;
	}
	
	double setPace(int power) {
		double fatigueFactor = 1 - (0.01 * this.fatigueLevel);
		
		double deltaKE = ((power * MECHANICAL_EFFICIENCY * fatigueFactor) - (this.coefficientDragArea * 0.5 * event.airDensity * Math.pow(this.currentVelocity, 3)) - (TeamPursuit.FRICTION_COEFFICIENT * (this.weight + BIKE_MASS) * TeamPursuit.GRAVITATIONAL_ACCELERATION * this.currentVelocity)) * TeamPursuit.TIME_STEP;
		double newVelocity = Math.pow(((2 * deltaKE / (this.weight + BIKE_MASS)) + Math.pow(this.currentVelocity, 2)), 0.5);
		double acceleration = newVelocity - this.currentVelocity;
		double distance = (this.currentVelocity * TeamPursuit.TIME_STEP) + (0.5 * acceleration * Math.pow(TeamPursuit.TIME_STEP, 2));
		
		this.currentVelocity = newVelocity;
		
		if (this.remainingEnergy > power * TeamPursuit.TIME_STEP) {
			this.remainingEnergy -= power * TeamPursuit.TIME_STEP;
		} else {
			this.remainingEnergy = 0.0;
		}
		
		return distance;
	}
	
	void follow(double distance) {
		double fatigueFactor = 1 - (0.01 * this.fatigueLevel);
		
		double acceleration = 2 * (distance - (this.currentVelocity * TeamPursuit.TIME_STEP)) / Math.pow(TeamPursuit.TIME_STEP, 2);
		double newVelocity = this.currentVelocity + (acceleration * TeamPursuit.TIME_STEP);
		double deltaKE = 0.5 * (this.weight + BIKE_MASS) * (newVelocity - currentVelocity);
		double power = ((this.coefficientDragArea * TeamPursuit.DRAFTING_COEFFICIENTS[this.position - 2] * 0.5 * event.airDensity * Math.pow(this.currentVelocity, 3)) + (TeamPursuit.FRICTION_COEFFICIENT * (this.weight + BIKE_MASS) * TeamPursuit.GRAVITATIONAL_ACCELERATION * this.currentVelocity) + (deltaKE / TeamPursuit.TIME_STEP)) / (MECHANICAL_EFFICIENCY * fatigueFactor);
		
		this.currentVelocity = newVelocity;
		
		
		if (this.remainingEnergy > power * TeamPursuit.TIME_STEP) {
			this.remainingEnergy -= power * TeamPursuit.TIME_STEP;
		} else {
			this.remainingEnergy = 0.0;
		}
	}
	
	double getHeight() {
		return this.height;
	}
	
	double getWeight() {
		return this.weight;
	}
	
	double getMeanMaximumPower() {
		return this.meanMaximumPower;
	}
	
	double getRemainingEnergy() {
		return this.remainingEnergy;
	}
	
	int getPosition() {
		return this.position;
	}
	
	void setWeight(double weight) {
		this.weight = weight;
		updateCDA();
		updateTotalEnergy();
	}
	
	void setHeight(double height) {
		this.height = height;
		updateCDA();
	}
	
	void setMeanMaximumPower(double meanMaximumPower) {
		this.meanMaximumPower = meanMaximumPower;
		updateTotalEnergy();
	}
	
	void setPosition(int position) {
		this.position = position;
	}
	
	void increaseFatigue() {
		this.fatigueLevel += 2;
	}
	
	void recover() {
		if (this.fatigueLevel > 0)
			this.fatigueLevel--;
	}
	
	void reset() {
		this.remainingEnergy = this.totalEnergy;
		this.position = this.startPosition;
		this.fatigueLevel = 0;
		this.currentVelocity = 0;
	}
	
	private void updateCDA() {
		this.coefficientDragArea = DRAG_COEFFICIENT * ((0.0293 * Math.pow(this.height, 0.725))*(Math.pow(this.weight, 0.425)) + 0.0604);
	}
	
	private void updateTotalEnergy() {
		if (this.event instanceof MensTeamPursuit) {
			this.totalEnergy = this.meanMaximumPower * this.weight * 240;
		} else {
			this.totalEnergy = this.meanMaximumPower * this.weight * 210;
		}
	}
}
