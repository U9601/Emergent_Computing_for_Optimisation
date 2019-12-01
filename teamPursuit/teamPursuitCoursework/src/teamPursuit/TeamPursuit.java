package teamPursuit;

public abstract class TeamPursuit {
	
	// Constants
	static final double FRICTION_COEFFICIENT = 0.0025;
	static final double DRAFTING_COEFFICIENTS[] = {0.75, 0.65, 0.55};
	static final double GRAVITATIONAL_ACCELERATION = 9.80665;
	static final double TIME_STEP = 0.001;
	protected static final double TRANSITION_TIME = 0.12;
	
	// Environmental Attributes
	protected double temperature = 20.0;
	protected double barometricPressure = 1013.25;
	protected double relativeHumidity = 0.5;
	double airDensity;
	protected Cyclist [] team;
	
	
	public void setTemperature(double temperature) throws Exception {
		if (temperature < 0.0 || temperature > 40.0)
			throw new Exception("Temperature must be in the range 0-40 C");
		this.temperature = temperature;
		updateAirDensity();
	}
	
	public void setBarometricPressure(double barometricPressure) throws Exception {
		if (barometricPressure < 800.0 || barometricPressure > 1200.0)
			throw new Exception("Barometric pressure must be in the range 800-1200 hPa");
		this.barometricPressure = barometricPressure;
		updateAirDensity();
	}
	
	public void setRelativeHumidity(double relativeHumidity) throws Exception {
		if (relativeHumidity < 0.0 || relativeHumidity > 1.0)
			throw new Exception("Relative humidity must be in the range 0-1");
		this.relativeHumidity = relativeHumidity;
		updateAirDensity();
	}
	
	public void setHeight(int cyclistId, double height) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		this.team[cyclistId].setHeight(height);
	}
	
	public void setWeight(int cyclistId, double weight) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		this.team[cyclistId].setWeight(weight);
	}
	
	public void setMeanMaximumPower(int cyclistId, double meanMaximumPower) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		this.team[cyclistId].setMeanMaximumPower(meanMaximumPower);
	}
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public double getBarometricPressure() {
		return this.barometricPressure;
	}
	
	public double getRelativeHumidity() {
		return this.relativeHumidity;
	}
	
	public double getHeight(int cyclistId) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		return this.team[cyclistId].getHeight();
	}
	
	public double getWeight(int cyclistId) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		return this.team[cyclistId].getWeight();
	}
	
	public double getMeanMaximumPower(int cyclistId) throws Exception {
		if (cyclistId >= this.team.length)
			throw new Exception("Cyclist identifier must be in the range 0-" + this.team.length);
		return this.team[cyclistId].getMeanMaximumPower();
	}
	
	public abstract SimulationResult simulate(boolean[] transitionStrategy, int[] pacingStrategy) throws Exception;
	
	protected void updateAirDensity() {
		double ppWaterVapour = 100 * this.relativeHumidity * (6.1078 * Math.pow(10, (((7.5 * (this.temperature + 273.15)) - 2048.625))/(this.temperature + 273.15 - 35.85)));
		double ppDryAir = 100 * this.barometricPressure - ppWaterVapour;
		this.airDensity = (ppDryAir/(287.058 * (this.temperature + 273.15))) + (ppWaterVapour/(461.495 * (this.temperature + 273.15)));
	}
	
	protected int cyclistsRemaining() {
		int cyclistsRemaining = 0;
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i].getRemainingEnergy() > 0.0)
				cyclistsRemaining++;
			else
				this.team[i].setPosition(0);
		}
		return cyclistsRemaining;
	}
	
	protected Cyclist leader() {
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i].getPosition() == 1)
				return this.team[i];
		}
		return null;
	}
	
	protected void transition() {
		for (int i = 0; i < this.team.length; i++) {
			if(this.team[i].getPosition() > 0) {
				if (this.team[i].getPosition() == 1)
					this.team[i].setPosition(cyclistsRemaining());
				else
					this.team[i].setPosition(this.team[i].getPosition() - 1);
			}
		}
	}
}
