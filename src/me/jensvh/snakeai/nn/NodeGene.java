package me.jensvh.snakeai.nn;

public class NodeGene {
	
	private int id;
	private NodeType type;
	
	private float value;
	
	/**
	 * Create a new node.
	 * @param id The id of the node
	 * @param type The {@link NodeType}
	 */
	public NodeGene(int id, NodeType type) {
		this.id = id;
		this.type = type;
		this.value = type.getBaseValue();
	}

	public int getId() {
		return id;
	}

	public NodeType getType() {
		return type;
	}

	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}

	public void addToValue(float toAdd) {
		value += toAdd;
	}
	
	/* ---------------------- GA --------------------- */
	
	public void sigmoid() {
		// Don't sigmoid bias and input nodes
		if (type == NodeType.BIAS || type == NodeType.INPUT) {
			return;
		}
		value = (float) (1.0f / (1.0f + (float) Math.pow(Math.E, -value)));
	}

	public NodeGene clone() {
		NodeGene clone = new NodeGene(id, type);
		
		return clone;
	}
	
}
