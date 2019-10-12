package me.jensvh.snakeai;

import java.util.ArrayList;

import me.jensvh.snakeai.oldschool.Genome;
//import me.jensvh.snakeai.nn.Genome;
import processing.core.PApplet;
import processing.core.PVector;

public class Snake {
	
	// Position
	private PVector position;
	
	// Tail
	private ArrayList<PVector> tail;
	private int tailLength;
	
	// Mental state
	private boolean alive = true;
	
	// Food
	private Food food;
	
	// Brain
	private Genome genome;
	
	// Genetic Algorithm
	private int stepsTaken;
	private int stepsLeft;
	private int fitness;
	
	
	/**
	 * Instantiate a new snake
	 */
	public Snake() {
		// Start in the middle of the screen
		position = new PVector((int) Math.ceil((double) Settings.HORIZONTAL_TILES / 2.0), (int) Math.ceil((double) Settings.VERTICAL_TILES / 2.0));
		
		// Create random food
		food = new Food();
		
		// Initialize the tail
		tail = new ArrayList<PVector>();
		tailLength = 3;
		
		// Give the snake a "brain"
		genome = new Genome();
		
		// Initialize the GA
		stepsTaken = 0;
		stepsLeft = Settings.STEPS_BEFORE_DEATH;
		fitness = 0;
	}
	
	/**
	 * Creates a new Neural Network with random weights.
	 */
	@Deprecated
	public void randomize() {
		genome.generateNewNetwork();
	}
	
	/**
	 * Update the snake, includes drawing the snake.
	 */
	public void update(PApplet applet) {
		// When the snake is death, no need to further update
		if (!alive) {
			return;
		}
		
		// When his steps are numbered, he can no longer live
		if (stepsLeft <= 0) {
			die();
			return;
		}
		
		// Look around
		float[] vision = takeALook();
		
		// Think about what you see
		float[] thinking = null;
		try {
			thinking = genome.feedForward(vision);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Decide where to go
		PVector directionToGo = decide(thinking);
		
		// Check if the move is possible, if not go death and return
		PVector nextPosition = position.copy().add(directionToGo);
		
		if (isOutOfMap(nextPosition)) {
			die();
			return;
		}
		
		// Check if he collides with his tail
		if (isOnTail(nextPosition)) {
			die();
			return;
		}
		
		// Check if he is on top of the food, if so eat
		if (nextPosition.x == food.getPosition().x && nextPosition.y == food.getPosition().y) {
			eat();
		}
		
		// Move the snake
		moveInDirection(directionToGo);
		
		
		// Now that he has moved, he has taken a step
		stepsTaken++;
		stepsLeft--;
		
		// So he's still alive
		// Why not drawing him
		draw(applet);
		
	}
	
	
	private void die() {
		alive = false;
		
		// See how well he've done
		//calculateFitness();
	}
	
	private void eat() {
		// He gets an extra body parts
		tailLength++;
		
		// And he can live a bit longer
		stepsLeft += 100; // TODO: += 100 instead of reset
		
		// New food
		food = new Food();
		// Make sure the food isnt on the tail
		while (tail.contains(food.getPosition())) {
			food = new Food();
		}
	}
	
	/**
	 * Moves the snake's head and his tail. When he has eaten recently, the tail will grow.
	 */
	private void moveInDirection(PVector direction) {
		
		// Move tail with him, and add a body block if he has recently eaten
		// If he has no tail, there is nothing to move
		if (tailLength != 0) {
			// If he has not eaten, move the list and remove the last item
			if (!hasEaten()) {
				// Move all the items in the list one down, ex. 1 -> 0
				// Last item does not have to be replaced
				for (int i = 0; i < tail.size() - 1; i++) {
					tail.set(i, tail.get(i + 1).copy()); // TODO: add copy
				}
				
				// Remove last one
				//tail.remove(tail.size() - 1);
				tail.set(tail.size() - 1 , position.copy()); // TODO: some weird changement
			} else {
			
				// Add the head into the list
				// The head is the previous position
				tail.add(position.copy());
			}
		}
		
		position.add(direction);
	}
	
	private boolean hasEaten() {
		return (tail.size() != tailLength);
	}
	
	/**
	 * Look for edges, food and body parts in 8 different directions.
	 * @return An array containing the inverse distance between the object and the snake's head. The distance is inverted to keep the value under 1.
	 */
	public float[] takeALook() {
		float[] vision = new float[24];// Change to array
		
		// Look
		float[] tmp = lookInDirection(0, 1);
		vision[0] = tmp[0];
		vision[1] = tmp[1];
		vision[2] = tmp[2];
		
		tmp = lookInDirection(1, 1);
		vision[3] = tmp[0];
		vision[4] = tmp[1];
		vision[5] = tmp[2];
		
		tmp = lookInDirection(1, 0);
		vision[6] = tmp[0];
		vision[7] = tmp[1];
		vision[8] = tmp[2];
		
		tmp = lookInDirection(1, -1);
		vision[9] = tmp[0];
		vision[10] = tmp[1];
		vision[11] = tmp[2];
		
		tmp = lookInDirection(0, -1);
		vision[12] = tmp[0];
		vision[13] = tmp[1];
		vision[14] = tmp[2];
		
		tmp = lookInDirection(-1, -1);
		vision[15] = tmp[0];
		vision[16] = tmp[1];
		vision[17] = tmp[2];
		
		tmp = lookInDirection(-1, 0);
		vision[18] = tmp[0];
		vision[19] = tmp[1];
		vision[20] = tmp[2];
		
		tmp = lookInDirection(-1, 1);
		vision[21] = tmp[0];
		vision[22] = tmp[1];
		vision[23] = tmp[2];
		
		return vision;
	}
	
	
	/**
	 * Look in the direction given for food, body parts and edges.
	 * @param x The x direction
	 * @param y The y direction
	 * @return An array containing the inverted distance from the object to the head.
	 */
	private float[] lookInDirection(int x, int y) {
		// Standard values, if nothing is found
		float[] distances = new float[3]; //TODO: to 0
		
		// Inverse y, the origin is at the left top
		PVector direction = new PVector(x, y);
		// Directly take a step, there is nothing interesting to find at the head's position
		PVector positionToLook = position.copy().add(direction);
		
		boolean foundFood = false;
		boolean foundBody = false;
		
		int distance = 1;
		// Keep looking until the edge is reached
		while(!isOutOfMap(positionToLook)) {
			
			// Look for food (if the distance between the two is zero), if not already found
			if (!foundFood && positionToLook.x == food.getPosition().x && positionToLook.y == food.getPosition().y) {
				distances[0] = 1;
				foundFood = true;
			}
			
			// Look for body parts
			if (!foundBody && isOnTail(positionToLook)) {
				distances[1] = (float) 1.0f / (float) distance;//TODO: to float
				foundBody = true;
			}
			
			// Take a step
			positionToLook.add(direction); 
			distance ++;
		}
		
		// Found edge, otherwise the loop was still running
		distances[2] = (float) 1.0f / (float) distance; // TODO: to float
		return distances; // TODO: to array
	}
	
	
	/**
	 * Chooses a direction based on the greatest value of the array.
	 * @param vision An array of floats.
	 * @return The direction as a {@link PVector}
	 */
	private PVector decide(float[] vision) {
		// Get biggest float from array
		float biggestValue = 0;
		int biggestId = 0;
		
		for (int i = 0; i < vision.length; i++) {
			if (vision[i] > biggestValue) {
				biggestValue = vision[i];
				biggestId = i;
			}
		}
		
		// Now turn the id into a direction
		// Inverse y, the origin is at the top left
		if (biggestId == 0) {
			return new PVector(0, -1);
		} else if (biggestId == 1) {
			return new PVector(1, 0);
		} else if (biggestId == 2) {
			return new PVector(0, 1);
		} else {
			return new PVector(-1, 0);
		}
	}
	
	
	/**
	 * Check for a certain position if it is out of the map, i.e. collides with the wall.
	 * @param position The position to be checked.
	 * @return True is he collides with the wall, False otherwise.
	 */
	private boolean isOutOfMap(PVector position) {
		if (position.x < 0 || position.x >= Settings.HORIZONTAL_TILES || position.y < 0 || position.y >= Settings.VERTICAL_TILES) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if a location is on the tail.
	 * @param location The location to be checked.
	 * @return True is the location is on the tail.
	 */
	private boolean isOnTail(PVector location) { // TODO: replace dist function with compare x and y
		for (PVector tailPosition : tail) {
			if (tailPosition.x == position.x && tailPosition.y == position.y) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Draw the snake and his food on the screen.
	 */
	private void draw(PApplet applet) {
		// Set color to white, and no stroke
		applet.fill(255);
		applet.stroke(0);
		
		// Draw the head of the snake
		applet.rect(position.x * Settings.TILE_SIZE, position.y * Settings.TILE_SIZE, Settings.TILE_SIZE, Settings.TILE_SIZE);
		
		// Draw the tail of the snake
		for (int i = 0; i < tail.size(); i++) {
			PVector tailPosition = tail.get(i);
			applet.rect(tailPosition.x * Settings.TILE_SIZE, tailPosition.y * Settings.TILE_SIZE, Settings.TILE_SIZE, Settings.TILE_SIZE);
		}
		
		// Draw the food
		food.draw(applet);
	}

	
	
	
	/* ----------------- GA -------------------- */
	/**
	 * Calculate fitness based on the length of the tail and the steps taken.
	 */
	public void calculateFitness() {
		fitness = (int) Math.floor(stepsTaken * stepsTaken * Math.pow(2, Math.floor(tailLength)));
	}
	
	
	public int getFitness() {
		return fitness;
	}
	
	public Snake clone() {
		Snake clone = new Snake();
		
		clone.genome = genome.clone();
		clone.alive = true;
		//TODO: clone.alive = true;
		return clone;
	}
	
	public Snake crossover(Snake parent) {
		Snake child = new Snake();
		
		child.genome = genome.crossover(parent.genome);
		
		return child;
	}
	
	public void mutate() {
		genome.mutate();
	}
	
	/* -------------------- Getters and Setters ---------------------- */
	public boolean isAlive() {
		return alive;
	}

	public int getLength() {
		return tailLength;
	}
	
}
