package teamPursuit;

public class SimulationResult {
	
	double finishTime;
	double proportionCompleted;
	double energyRemaining[];
	double velocityProfile[];
	
	
	SimulationResult(double finishTime, double proportionCompleted, double [] energyRemaining, double [] velocityProfile) {
		this.finishTime = finishTime;
		this.energyRemaining = energyRemaining;
		this.proportionCompleted = proportionCompleted;
		this.velocityProfile = velocityProfile;
	}
	
	public double getFinishTime() {
		return this.finishTime;
	}
	
	public double getProportionCompleted() {
		return this.proportionCompleted;
	}
	
	public double [] getEnergyRemaining() {
		return this.energyRemaining;
	}
	
	public double [] getVelocityProfile() {
		return this.velocityProfile;
	}
	
	public String toString() {
		String output = "Simulation Result\n-----------------\n";
		if (this.finishTime < Double.POSITIVE_INFINITY) {
			output = output + "Finish Time: " + finishTime + " seconds\n";
			for (int i = 0; i < this.energyRemaining.length; i++) {
				output = output + "Cyclist " + (i+1) + " Energy Remaining: " + energyRemaining[i] + " joules\n";
			}
		} else {
			output = output + "Riders had insufficient energy for race completion\n" +
					"Proportion of race completed: " + (this.proportionCompleted * 100) + "%\n";
		}
		return output;
	}
}
