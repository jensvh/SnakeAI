package me.jensvh.snakeai;

public class Settings {
	
	// Screen variables
	public static final int WIDTH = 400;
	public static final int HEIGHT = 400;
	public static final int FPS = 1;
	
	public static final int TILE_SIZE = 10;
	public static final float HORIZONTAL_TILES = WIDTH / TILE_SIZE;
	public static final float VERTICAL_TILES = HEIGHT / TILE_SIZE;
	
	
	// Genetic Algorithm
	public static final int POPULATION_SIZE = 1000;
	public static final float MUTATION_RATE = 0.05f;
	public static final int STEPS_BEFORE_DEATH = 200;
	
	// Neural network
	public static final int INPUT_NODES = 24;
	public static final int OUTPUT_NODES = 4;
	public static final int[] HIDDEN_NODES = {18, 18};

}
