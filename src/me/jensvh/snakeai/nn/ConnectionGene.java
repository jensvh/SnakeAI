package me.jensvh.snakeai.nn;

import java.util.Random;

import me.jensvh.snakeai.Settings;
import me.jensvh.snakeai.SnakeAI;

public class ConnectionGene {
	
	private int inNode;
	private int outNode;
	private float weight;
	
	/**
	 * Create a connection between two nodes with a random weight.
	 * @param inNode The id of the input {@link NodeGene}
	 * @param outNode The id of the output {@link NodeGene}
	 */
	public ConnectionGene(int inNode, int outNode) {
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = (new Random().nextFloat() * 2) - 1;
	}
	
	/**
	 * Create a connection between two nodes with specified weight.
	 * @param inNode The id of the input {@link NodeGene}
	 * @param outNode The id of the output {@link NodeGene}
	 */
	public ConnectionGene(int inNode, int outNode, float weight) {
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
	}

	/* ------------------- GA --------------------- */
	/**
	 * Mutate's the weight of the connection with a chance set by {@link SnakeAI#MUTATION_RATE}.
	 */
	public void mutate() {
		if (new Random().nextFloat() <= Settings.MUTATION_RATE) {
			// Mutate the weight by adding a tiny number
			weight += new Random().nextGaussian();
			
			// Keep weight between -1 and 1
			if (weight > 1) {
				weight = 1;
			} else if (weight < -1) {
				weight = -1;
			}
		}
	}
	
	public ConnectionGene crossover(ConnectionGene parent) {
		if (!this.equalsIgnoreWeight(parent)) {
			// The two connections dont connect the same nodes...
			System.out.println("Error");
			return this.clone();
		}
		
		ConnectionGene child = new ConnectionGene(inNode, outNode);
		child.setWeight((new Random().nextFloat() >= .5) ? this.weight : parent.weight);
		
		return child;
	}
	
	public ConnectionGene clone() {
		return new ConnectionGene(inNode, outNode, weight);
	}
	
	
	/* ------------------ extra functions ------------------- */
	public int getInNode() {
		return inNode;
	}

	public int getOutNode() {
		return outNode;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public boolean equals(ConnectionGene conn) {
		return (this.inNode == conn.inNode && this.outNode == conn.outNode && this.weight == conn.weight);
	}
	
	public boolean equalsIgnoreWeight(ConnectionGene conn) {
		return (this.inNode == conn.inNode && this.outNode == conn.outNode);
	}

}
