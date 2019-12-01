package ea;

import java.awt.List;

/***
 * This is an example of an EA used to solve the problem
 *  A chromosome consists of two arrays - the pacing strategy and the transition strategy
 * This algorithm is only provided as an example of how to use the code and is very simple - it ONLY evolves the transition strategy and simply sticks with the default
 * pacing strategy
 * The default settings in the parameters file make the EA work like a hillclimber:
 * 	the population size is set to 1, and there is no crossover, just mutation
 * The pacing strategy array is never altered in this version- mutation and crossover are only
 * applied to the transition strategy array
 * It uses a simple (and not very helpful) fitness function - if a strategy results in an
 * incomplete race, the fitness is set to 1000, regardless of how much of the race is completed
 * If the race is completed, the fitness is equal to the time taken
 * The idea is to minimise the fitness value
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import teamPursuit.TeamPursuit;
import teamPursuit.WomensTeamPursuit;

public class EA<Invidiual> implements Runnable{
	
	// create a new team with the default settings
	public static TeamPursuit teamPursuit = new WomensTeamPursuit(); 
	
	private ArrayList<Individual> population = new ArrayList<Individual>();
	private int iteration = 0;
	long startTime = System.currentTimeMillis();
	
	public EA() {
		
	}

	
	public static void main(String[] args) {
		EA ea = new EA();
		ea.run();
	}

	public void run() {
		initialisePopulation();
		long end = startTime+ 300000;
		System.out.println("finished init pop");
		iteration = 0;
		while(iteration < Parameters.maxIterations){
			iteration++;
			Individual parent1 = rouletteWheelSelection();
			Individual parent2 = rouletteWheelSelection();
			//Individual parent1 = tournamentSelection();
			//Individual parent2 = tournamentSelection();
			//Individual child = crossover(parent1, parent2); 
			//Individual child = two_pt_crossover(parent1, parent2);
		    Individual child = uniform_corssover(parent1, parent2);
			child = hillClimber(child);
			child = mutate(child);
			child.evaluate(teamPursuit);
			replace(child);
			sawTooth();
			printStats();
		}						
		Individual best = getBest(population);
		best.print();
		
	}
	
	
	private void printStats() {	
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		long elapsedSeconds = elapsedTime / 1000;
		long secondsDisplay = elapsedSeconds % 60;
		long elapsedMinutes = elapsedSeconds / 60;
		long minutesDisplay = elapsedMinutes % 60;
		long elapsedHours = elapsedMinutes / 60;
		
		System.out.println("" + iteration + "\t" + getBest(population) + "\t" + getWorst(population)+ "\t Average: " +averageFitness()+ "\t Time Elsaped: " +elapsedHours+" Hours " +minutesDisplay+ " Minutes " +secondsDisplay+ " Seconds");		
	}
	
	private Individual mutate(Individual child){
		child = orginalMutate(child);
		child = shuffleMutate(child);
		child = scrambleMutate(child);
		return child;
	}



	private void replace(Individual child) {
		Individual worst = getWorst(population);
		if(child.getFitness() < worst.getFitness()){
			int idx = population.indexOf(worst);
			population.set(idx, child);
		}
	}
	
	private Individual rouletteWheelSelection(){
		Individual parent = new Individual();
		int totalFitness = 0;
		
		for(int i = 0; i < population.size(); i++){
			totalFitness += population.get(i).getFitness();
		}
		double random = ThreadLocalRandom.current().nextDouble(0, totalFitness);
		double sum = 0;
		int i = 0;
		while(sum <= random){ 
			sum += population.get(i).getFitness();
			i++;
		}
		parent = population.get(i-1);
		
		return parent;
	}


	private Individual orginalMutate(Individual child) {
		
		// choose how many elements to alter
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);
		
		// mutate the transition strategy

			//mutate the transition strategy by flipping boolean value
			for(int i = 0; i < mutationRate; i++){
				int index = Parameters.rnd.nextInt(child.transitionStrategy.length);
				child.transitionStrategy[index] = !child.transitionStrategy[index];
				
			}
			
			for(int i = 0; i < mutationRate; i++){
				int index = Parameters.rnd.nextInt(child.pacingStrategy.length);
			
				
				if(iteration < 20000){
					if(child.pacingStrategy[index]+50 > 1200){
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(-50, 0);
						
					}else if(child.pacingStrategy[index]-50 < 200){
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(0, 50);
						
					}else{
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(-50, 50);
					}
				}
				
				if (iteration > 20000){
					if(child.pacingStrategy[index]+20 > 1200){
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(-20, 0);
						
					}else if(child.pacingStrategy[index]-20 < 200){
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(0, 20);
						
					}else{
						child.pacingStrategy[index] += ThreadLocalRandom.current().nextInt(-20, 20);
					}
				}
				
			}
		return child;
	}
	
	private void sawTooth(){
		if(iteration % 40 == 0){
			if(population.size() >= 10){
				Individual worst = getWorst(population);
				population.remove(worst);
			}else{
				initialisePopulation();
			}
		}
	}
	
	private double averageFitness(){
		double total = 0;
		for(int i = 0; i < population.size(); i++){
			total += population.get(i).getFitness();
		}
		
		return total/population.size();
	}
	
	private Individual shuffleMutate(Individual child) {
		
		int size = child.pacingStrategy.length;
		int size1 = child.transitionStrategy.length;
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);
		
		for (int i = 0; i < size; i++){
			if(Parameters.rnd.nextInt() < mutationRate){
				int swapIndex = ThreadLocalRandom.current().nextInt(0, size - 2);
				if (swapIndex >= i){
					swapIndex++;
				}
				int value = child.pacingStrategy[i];
				
				child.pacingStrategy[i] = child.pacingStrategy[swapIndex];
				child.pacingStrategy[swapIndex] = value;
			}
		}
		
		for(int i = 0; i < size1; i++){
			if(Parameters.rnd.nextInt() < mutationRate){
				int swapIndex = ThreadLocalRandom.current().nextInt(0, size1 - 2);
				if (swapIndex >= i){
					swapIndex++;
				}
				boolean value = child.transitionStrategy[i];
				
				child.transitionStrategy[i] = child.transitionStrategy[swapIndex];
				child.transitionStrategy[swapIndex] = value;
			}

		}
		
		return child;
	}
	
	private Individual scrambleMutate(Individual child){
		
		int size = child.pacingStrategy.length;
		int size1 = child.transitionStrategy.length;
		int pos1 = ThreadLocalRandom.current().nextInt(0, size);
		int pos2 = ThreadLocalRandom.current().nextInt(0, size);
		int pos3 = ThreadLocalRandom.current().nextInt(0, size1);
		int pos4 = ThreadLocalRandom.current().nextInt(0, size1);
		ArrayList<Integer> listChild = new ArrayList<Integer>();
		ArrayList<Boolean> listChild1 = new ArrayList<Boolean>();
		
		if(pos1 >= pos2){
			int temp = pos1;
			pos1 = pos2;
			pos2 =  temp;
		}
		
		if(pos3 >= pos4){
			int temp = pos3;
			pos3 = pos4;
			pos4 =  temp;
		}
		
		for(int i = pos1; i <= pos2; i++){
			listChild.add(child.pacingStrategy[i]);
		}
		
		Collections.shuffle(listChild);
		
		int j = 0;
		for(int i = pos1; i <= pos2; i++){
			child.pacingStrategy[i] = listChild.get(j);
			j++;
		}
		

		for(int i = pos3; i <= pos4; i++){
			listChild1.add(child.transitionStrategy[i]);
		}
		
		Collections.shuffle(listChild1);
		
		j = 0;
		for (int i = pos3; i <= pos4; i++){
			child.transitionStrategy[i] = listChild1.get(j);
			j++;
		}
	
		return child;
	}

	


	private Individual crossover(Individual parent1, Individual parent2) {
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}
		Individual child = new Individual();
		
		int crossoverPoint = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		
		// just copy the pacing strategy from p1 - not evolving in this version
		for(int i = 0; i < parent1.pacingStrategy.length; i++){			
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}
		
		
		for(int i = 0; i < crossoverPoint; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		for(int i = crossoverPoint; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}
		return child;
	}
	
	private Individual two_pt_crossover(Individual parent1, Individual parent2) {
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}
		Individual child = new Individual();
		
		int crossoverPoint = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		int crossoverPoint2 = Parameters.rnd.nextInt(parent2.transitionStrategy.length);
		
		// just copy the pacing strategy from p1 - not evolving in this version
		for(int i = 0; i < parent1.pacingStrategy.length; i++){			
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}
		
		
		for(int i = 0; i < crossoverPoint; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		
		for(int i = 0; i < crossoverPoint2; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		
		for(int i = crossoverPoint; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}
		
		for(int i = crossoverPoint2; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}
		return child;
	}
	
	private Individual uniform_corssover(Individual parent1, Individual parent2){
		 int i;
	     double rand = 0;

	     Individual child = new Individual();
	     
	     for(i = 0; i < child.pacingStrategy.length; i++){		
	    	 rand = Math.random();
	    	 if (rand > 0.5) {

                 child.pacingStrategy[i] = parent1.pacingStrategy[i];
             } else {
                 child.pacingStrategy[i] = parent2.pacingStrategy[i] ;
             }
			}

	        for(i = 0; i < child.transitionStrategy.length; i++) {
	            rand = Math.random();
	            if (rand > 0.5) {

	                 child.transitionStrategy[i] = parent1.transitionStrategy[i];
	             } else {
	                 child.transitionStrategy[i] = parent2.transitionStrategy[i] ;
	             }

	        }
	        return child;

	}
	
	private Individual hillClimber(Individual child){
		try{
	        double oldFitness = child.getFitness();
	        mutate(child);
	        double newFitness = child.getFitness();
	
	        while (newFitness < oldFitness)
	        {
	            mutate(child);
	        }
		}catch(NullPointerException npe){
			
		}

        return child;



    }


	/**
	 * Returns a COPY of the individual selected using tournament selection
	 * @return
	 */
	private Individual tournamentSelection() {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++){
			candidates.add(population.get(Parameters.rnd.nextInt(population.size())));
		}
		return getBest(candidates).copy();
	}


	private Individual getBest(ArrayList<Individual> aPopulation) {
		double bestFitness = Double.MAX_VALUE;
		Individual best = null;
		for(Individual individual : aPopulation){
			if(individual.getFitness() < bestFitness || best == null){
				best = individual;
				bestFitness = best.getFitness();
			}
		}
		return best;
	}

	private Individual getWorst(ArrayList<Individual> aPopulation) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : population){
			if(individual.getFitness() > worstFitness || worst == null){
				worst = individual;
				worstFitness = worst.getFitness();
			}
		}
		return worst;
	}
	
	private void printPopulation() {
		for(Individual individual : population){
			System.out.println(individual);
		}
	}

	private void initialisePopulation() {
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise();			
			individual.evaluate(teamPursuit);
			population.add(individual);
							
		}		
	}	
}
