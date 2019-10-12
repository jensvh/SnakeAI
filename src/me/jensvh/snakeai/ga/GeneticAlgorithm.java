package me.jensvh.snakeai.ga;

import java.util.Random;

import me.jensvh.snakeai.Settings;
import me.jensvh.snakeai.Snake;
import processing.core.PApplet;

public class GeneticAlgorithm {

	private int generation;
	private Snake[] population;//TODO: to array
	
	// Scores
	private int highScore;
	private int bestSnake;
	private int deaths;
	private int fitnessSum;
	
	public GeneticAlgorithm() {
		generation = 0;
		population = new Snake[Settings.POPULATION_SIZE];
		
		// Create a new random generation
		randomGeneration();
		
		// Set all value's to their defaults
		reset();
	}
	
	/**
	 * Update the GA.
	 * @param applet Used to draw to the screen.
	 */
	public void update(PApplet applet) {
		// Update current snake until he's death
		for (int id = 0; id < population.length; id++) {
			if (population[id].isAlive()) {
				population[id].update(applet);
			} else {
				
				deaths++;
				
			}
		}
		
		if (deaths >= population.length) {
			// Next generation
			// calc maxfitness
			calcFitness();
			
			/*for (int id = 0; id < population.size(); id++) {
				if (population.get(id).getFitness() > highScore) {
					// He is one of the bests
					highScore = population.get(id).getFitness();
					bestSnake = id;
				}
				
				totalScore += population.get(id).getFitness();
			}*/
			
			nextGeneration();
		}
		
		deaths = 0;
	}
	
	
	private void calcFitness() {
		for (int i = 0; i < population.length; i++) {
			population[i].calculateFitness();
		}
	}
	
	/**
	 * Create a new generation from the current one. It clone's the best snake to prevent decreasing in highscore. The other child are made out of two parents chosen by their fitness. The higher the fitness the more chance to be chosen.
	 */
	private void nextGeneration() {
		// Create a new generation with childs
		Snake[] childs = new Snake[Settings.POPULATION_SIZE];//TODO: to array
		
		// Set best snake
		setBestSnake();
		
		// Calculate fitness sum
		calculateFitnessSum();
		
		// Add the best snake into the new generation, against decreasing of fitness
		childs[0] = population[bestSnake].clone();
		
		// Keep adding child's until the populationSize is reached
		for (int i = 1; i < Settings.POPULATION_SIZE; i++) { //TODO: to <= instead of <
			// Select two parents
			Snake parent1 = selectParent();
			Snake parent2 = selectParent();
			
			// Make baby
			Snake child = parent1.crossover(parent2);
			
			// Mutate the baby
			child.mutate();
			
			// Add the child
			childs[i] = child;
		}
		
		// Add childs to next genertion
		population = childs.clone();
		
		// Next generation
		System.out.println("Generation: " + generation + " with a highscore of " + highScore + ".");
		
		generation++;
		reset();
	}
	
	private void setBestSnake() {
		int max = 0;
		int maxId = 0;
		for (int i = 0; i < population.length; i++) {
			if (population[i].getFitness() > max) {
				max = population[i].getFitness();
				maxId = i;
			}
		}
		
		bestSnake = maxId;
		highScore = max;
	}
	
	/**
	 * Create's a generation of random snake's.
	 */
	private void randomGeneration() {
		for (int i = 0; i < Settings.POPULATION_SIZE; i++) {
			Snake snake = new Snake();
			population[i] = snake;
		}
	}
	
	private void calculateFitnessSum() {
		int sum = 0;
		
		for (int i = 0; i < population.length; i++) {
			sum += population[i].getFitness();
		}
		
		this.fitnessSum = sum;
	}
	
	/**
	 * Select a random parent based on its fitness. The higher the fitness the more chance he has to be chosen.
	 * @return A {@link Snake}
	 */
	private Snake selectParent() {
		
		int fitnessToReach = new Random().nextInt(fitnessSum);
		
		int currentFitness = 0;
		
		for (int id = 0; id < population.length; id++) {
			currentFitness += population[id].getFitness();
			
			if (currentFitness > fitnessToReach) {
				return population[id];
			}
		}
		
		return population[bestSnake];
	}
	
	/**
	 * Resets all the value's that held information for this generation.
	 */
	private void reset() {
		highScore = 0;
		bestSnake = 0;
		deaths = 0;
	}
	
}
