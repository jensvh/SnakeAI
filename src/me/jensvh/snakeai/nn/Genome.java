package me.jensvh.snakeai.nn;

import com.google.common.collect.ArrayListMultimap;

import me.jensvh.snakeai.Settings;

/**
 * Kind of a brain.
 *
 */
public class Genome {
	
	// The layer, and the node
	private ArrayListMultimap<Integer, NodeGene> nodes;
	// The inNode id and the connection
	private ArrayListMultimap<Integer, ConnectionGene> connections;
	
	private int[] nodesOnLayer;
	
	/**
	 * Create a {@link Genome} from multiple int's that represents the amount of nodes per layer.
	 * @param inputs The amount of input nodes
	 * @param outputs The amount of output nodes
	 * @param hiddens An array of all the hidden layers with the amount of nodes
	 */
	public Genome() {
		nodes = ArrayListMultimap.create();
		connections = ArrayListMultimap.create();

		// Store amount of nodes into array
		nodesOnLayer = new int[2 + Settings.HIDDEN_NODES.length];
		
		nodesOnLayer[0] = Settings.INPUT_NODES;
		nodesOnLayer[nodesOnLayer.length - 1] = Settings.OUTPUT_NODES;
		
		for (int i = 0; i < Settings.HIDDEN_NODES.length; i++) {
			nodesOnLayer[i + 1] = Settings.HIDDEN_NODES[i];
		}
	}
	
	/**
	 * Create a {@link Genome} from an array that represents the amount of nodes per layer. The biases needs to be included in this array.
	 * @param nodesPerLayer An array from all layers with the amount of nodes
	 */
	@Deprecated
	public Genome(int[] nodesPerLayer) {
		nodes = ArrayListMultimap.create();
		connections = ArrayListMultimap.create();
		
		nodesOnLayer = new int[nodesPerLayer.length];
		
		// Copy value from one array to another
		for (int i = 0; i < nodesPerLayer.length; i++) {
			nodesOnLayer[i] = nodesPerLayer[i];
		}
	}
	
	/**
	 * Create a new neural network with random weights between -1 and 1.
	 */
	public void generateNewNetwork() {
		int currentId = 0;
		
		// For each layer
		for (int layer = 0; layer < getLayers(); layer++) {
			// Add nodes to this layer
			for (int node = 0; node < getNodesOnLayer(layer); node++) {
				// When first layer set type as input, if last set type as output, else Hidden
				
				if (isInputLayer(layer)) { // First layer
					nodes.put(layer, new NodeGene(currentId, NodeType.INPUT));
				} else if (isOutputLayer(layer)) { // Last layer
					nodes.put(layer, new NodeGene(currentId, NodeType.OUTPUT));
				} else { // Rest of layers
					nodes.put(layer, new NodeGene(currentId, NodeType.HIDDEN));
				}
				
				currentId++;
			}
			
			// If not output layer
			if (!isOutputLayer(layer)) {
				// Add a bias
				nodes.put(layer, new NodeGene(currentId, NodeType.BIAS));
				currentId++;
				
				// Create connections to next layer
				// Id's of all nodes on this layer
				// currentId is id of last node, -1 because of the bias we added, -nodesOnThisLayer to get the first node of the layer
				for (int thisLayer = currentId - getNodesOnLayer(layer) - 1; thisLayer < currentId; thisLayer++) { 
					// Id's of nodes on next layer;
					// curentId is id of first node of the next layer, then connect to all nodes on the next layer
					for (int nextLayer = currentId; nextLayer < currentId + getNodesOnLayer(layer + 1); nextLayer++) {
						connections.put(thisLayer, new ConnectionGene(thisLayer, nextLayer));
					}
				}
			}
		}
	}

	/**
	 * Give the inputs to the neural network. And see what it spits out.
	 * @param inputs The inputs for the neural network.
	 * @return The outputs of the neural network.
	 * @throws Exception Throws an exception when the size of the inputs does not match with the one from the neural network.
	 */
	public float[] feedForward(float[] inputs) throws Exception {
		// Check if the the input sizes are equal
		if (getInputNodes() != inputs.length) {
			throw new Exception("Wrong input dimensions, the neural network has " + getInputNodes() + " input nodes, not " + inputs.length + ".");
		}
		
		// Set the input nodes with their corresponding value
		setInputs(inputs);
		
		// Start the feed forward motion
		startFeedForwarding();
		
		// Get the outputs
		float[] outputs = getOutputs();
		
		return outputs;
	}
	
	/**
	 * Give the input nodes their starting value.
	 * @param inputs The start values.
	 */
	private void setInputs(float[] inputs) {
		for (int id = 0; id < inputs.length; id++) {
			nodes.get(0).get(id).setValue(inputs[id]);
		}
	}
	
	/**
	 * Get the outputs from the neural network.
	 * @return The output value's of the neural network.
	 */
	private float[] getOutputs() {
		float[] outputs = new float[getOutputNodes()];
		
		// The id's of the output nodes
		for (int id = 0; id < getOutputNodes(); id++) {
			outputs[id] = nodes.get(nodes.keySet().size() - 1).get(id).getValue();
		}
		
		return outputs;
	}
	
	/**
	 * Start the feed forward motion. It passes all the values of a node through a connections to the next node.
	 */
	private void startFeedForwarding() {
		for (int layer = 0; layer < nodes.keySet().size(); layer++) {
			for (int node = 0; node < nodes.get(layer).size(); node++) {
				NodeGene nodeGene = nodes.get(layer).get(node);
				// Sigmoid each node
				nodeGene.sigmoid();
				
				// Pass value throught connection to next node
				// Loop throught all the connections of this node
				for (ConnectionGene connection : connections.get(nodeGene.getId())) {
					float valueToPass = nodeGene.getValue() * connection.getWeight();
					getNodeById(connection.getOutNode()).addToValue(valueToPass);
				}
			}
		}
	}
	
	// TODO: toGenoType for visualization
	// TODO: toPhenoType for visualization
	
	/* Getters and Setters */
	
	private NodeGene getNodeById(int id) {
		for (int layer = 0; layer < nodes.size(); layer++) {
			for (int node = 0; node < nodes.get(layer).size(); node++) {
				if (nodes.get(layer).get(node).getId() == id) {
					return nodes.get(layer).get(node);
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
 	private int getNodeCount() {
		return nodes.size();
	}
	
	private int getLayers() {
		return nodesOnLayer.length;
	}
	
	private int getNodesOnLayer(int layer) {
		if (layer < 0 || layer >= nodesOnLayer.length) {
			return 0;
		}
		
		return nodesOnLayer[layer];
	}

	private int getInputNodes() {
		return nodesOnLayer[0];
	}
	
	private int getOutputNodes() {
		return nodesOnLayer[nodesOnLayer.length - 1];
	}
	
	/**
	 * Check if the current layer is the input layer.
	 * @param layer The current layer.
	 * @return True if it is the input layer, false otherwise.
	 */
	private boolean isInputLayer(int layer) {
		return (layer == 0);
	}
	
	/**
	 * Check if the current layer is the output layer.
	 * @param layer The current layer.
	 * @return True if it is the output layer, false otherwise.
	 */
	private boolean isOutputLayer(int layer) {
		return (layer == nodesOnLayer.length - 1);
	}

	/* ---------------------------- GA -------------------------- */
	public Genome clone() {
		Genome clone = new Genome();
		
		// Add nodes
		for (int layer = 0; layer < nodes.size(); layer++) {
			for (int node = 0; node < nodes.get(layer).size(); node++) {
				clone.nodes.put(layer, nodes.get(layer).get(node).clone());
			}
		}
		
		// Add connections
		for (Integer id : connections.keySet()) {
			for (ConnectionGene conn : connections.get(id)) {
				clone.connections.put(id.intValue(), conn.clone());
			}
		}
		
		return clone;
	}
	
	public Genome crossover(Genome parent) {
		Genome child = new Genome();
		
		// First add nodes
		for (int layer = 0; layer < nodes.size(); layer++) {
			for (int node = 0; node < nodes.get(layer).size(); node++) {
				child.nodes.put(layer, nodes.get(layer).get(node).clone());
			}
		}
		
		// Add connections
		for (int id = 0; id < connections.keys().size(); id++) {
			for (int connection = 0; connection < connections.get(id).size(); connection++) {
				child.connections.put(id, connections.get(id).get(connection).crossover(parent.connections.get(id).get(connection)));
			}
		}
		
		return child;
	}
	
	public void mutate() {
		for (ConnectionGene connection : connections.values()) {
			connection.mutate();
		}
	}
}
