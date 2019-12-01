package teamPursuit;

public final class MensTeamPursuit extends TeamPursuit {
	
	private static final int TEAM_SIZE = 4;
	private static final int RACE_DISTANCE = 4000;
	private static final int LAP_DISTANCE = 250;
	private static final int RACE_SEGMENTS = (2 * (RACE_DISTANCE / LAP_DISTANCE)) - 1;
	private static final int MAXIMUM_TRANSITIONS = RACE_SEGMENTS - 1;
	
	
	public MensTeamPursuit() {
		updateAirDensity();
		this.team = new Cyclist[TEAM_SIZE];
		for (int i = 0; i < this.team.length; i++) {
			this.team[i] = new Cyclist(1.75, 75.0, 6.0, this, i+1);
		}
	}
	
	public SimulationResult simulate(boolean [] transitionStrategy, int [] pacingStrategy) throws Exception {
		
		if (transitionStrategy.length != MAXIMUM_TRANSITIONS)
			throw new Exception("Transition strategy for the mens team pursuit must have exactly " + MAXIMUM_TRANSITIONS +" elements");
		if (pacingStrategy.length != RACE_SEGMENTS)
			throw new Exception("Pacing strategy for the mens team pursuit must have exactly " + RACE_SEGMENTS + " elements");
		for (int i = 0; i < RACE_SEGMENTS; i++) {
			if (pacingStrategy[i] > Cyclist.MAX_POWER || pacingStrategy[i] < Cyclist.MIN_POWER)
				throw new Exception("All power elements of the pacing strategy must be in the range " + Cyclist.MIN_POWER + "-" + Cyclist.MAX_POWER + " Watts");
		}
		
		for (int i = 0; i < this.team.length; i++) {
			this.team[i].reset();
		}
		
		double velocityProfile[] = new double[RACE_SEGMENTS];
		double proportionCompleted = 0;
		double raceTime = 0;
		for (int i = 0; i < RACE_SEGMENTS; i++) {
			double distance;
			if (i == 0 || i == (RACE_SEGMENTS - 1))
				distance = 187.5;
			else
				distance = 125.0;
			if (cyclistsRemaining() >= 3) {
				
				if (cyclistsRemaining() == 3)
					validateOrder();
				
				if (i >= 1 && transitionStrategy[i-1]) {
					transition();
					raceTime += TeamPursuit.TRANSITION_TIME;
				}
				Cyclist leader = leader();
				double time = 0.0;
				double distanceRidden = 0.0;
				while (distanceRidden < distance) {
					double dist = leader.setPace(pacingStrategy[i]);
					
					for (int j = 0; j < this.team.length; j++) {
						if (this.team[j].getPosition() > 1)
							this.team[j].follow(dist);
					}
					
					if (distanceRidden + dist <= distance)
						distanceRidden += dist;
					else
						distanceRidden = distance;
				
					time += TIME_STEP;
				}
				
				leader.increaseFatigue();
				for (int j = 0; j < this.team.length; j++) {
					if (this.team[j].getPosition() > 1)
						this.team[j].recover();
				}
				
				if (cyclistsRemaining() >= 3) {
					velocityProfile[i] = distance / time;
					raceTime += time;
					proportionCompleted += distance / RACE_DISTANCE;
				} else {
					raceTime = Double.POSITIVE_INFINITY;
				}
			} else {
				raceTime = Double.POSITIVE_INFINITY;
				break;
			}
		}
		
		double [] remainingEnergies = new double[this.team.length];
		for (int i = 0; i < this.team.length; i++) {
			remainingEnergies[i] = this.team[i].getRemainingEnergy();
		}
		
		return new SimulationResult(raceTime, proportionCompleted, remainingEnergies, velocityProfile);
	}
	
	private void validateOrder() {
		for (int i = 0; i < this.team.length; i++) {
			if (this.team[i].getPosition() == 4) {
				this.team[i].setPosition(3);
				if (this.team[(i+1)%4].getPosition() == 0) {
					this.team[(i+2)%4].setPosition(1);
					this.team[(i+3)%4].setPosition(2);
				} else if (this.team[(i+2)%4].getPosition() == 0) {
					this.team[(i+3)%4].setPosition(2);
				}
			}
		}
	}
}
