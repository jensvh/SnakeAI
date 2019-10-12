package me.jensvh.snakeai.oldschool;

import me.jensvh.snakeai.Settings;

public class Genome {
	
	private int[] layers;
	
	private Matrix[] weights;
	
	/**
	 * Instantiate a new {@link Genome}.
	 */
	public Genome() {
		// Set all nodes per layer in an array
		layers = new int[Settings.HIDDEN_NODES.length + 2];
		
		layers[0] = Settings.INPUT_NODES;
		layers[layers.length - 1] = Settings.OUTPUT_NODES;
		
		for (int i = 0; i < Settings.HIDDEN_NODES.length; i++) {
			layers[i + 1] = Settings.HIDDEN_NODES[i];
		}
		
		// Create weight matrices.
		weights = new Matrix[layers.length - 1];
		
		for (int i = 0; i < layers.length - 1; i++) {
			// An extra row for the bias
			weights[i] = new Matrix(layers[i] + 1, layers[i + 1]); // Change rows/columns
			// Randomize this matrix
			weights[i].randomize();
		}
	}

	
	/**
	 * Randomize all the weights.
	 */
	public void generateNewNetwork() {
		for (int i = 0; i < weights.length; i++) {
			weights[i].randomize();
		}
	}
	
	
	/**
	 * Give the inputs to the neural network. And see what it spits out.
	 * @param inputs The inputs for the neural network.
	 * @return The outputs of the neural network.
	 * @throws Exception Throws an exception when the size of the inputs does not match with the one from the neural network.
	 */
	public float[] feedForward(float[] inputs) throws Exception {
		// Check if the inputSize's are equal
		if (layers[0] != inputs.length) {
			throw new Exception("Wrong input dimensions, the neural network has " + layers[0] + " input nodes, not " + inputs.length + ".");
		}
		
		// Create a matrix that represents the nodes
		Matrix nodes = new Matrix(inputs);
		
		// Add an extra node, the bias
		nodes.addBias();
		
		//Feed forward this matrix to all the weights
		for (int i = 0; i < weights.length; i++) {
			nodes.multiply(weights[i]);
			
			nodes.sigmoid();
			
			// when its not the output layer, add bias
			if (i != weights.length - 1) { // TODO: replace with != -1
				nodes.addBias();
			}
		}
		
		// Output matrix as a single array
		return nodes.toArray();
	}
	
	
	public Genome clone() {
		Genome clone = new Genome();
		
		for (int matrix = 0; matrix < weights.length; matrix++) {
			clone.weights[matrix] = weights[matrix].clone();
		}
		
		return clone;
	}
	
	public Genome crossover(Genome parent) {
		Genome child = new Genome();
		
		for (int matrix = 0; matrix < weights.length; matrix++) {
			weights[matrix] = weights[matrix].randomCrossover(parent.weights[matrix]);
		}
		
		return child;
	}
	
	public void mutate() {
		for (Matrix matrix : weights) {
			matrix.mutateMatrix();
		}
	}
	
}
