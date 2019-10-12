package me.jensvh.snakeai.nn;

public enum NodeType {

	INPUT(0),
	HIDDEN(0),
	OUTPUT(0),
	BIAS(1);
	
	private int base_value;
	
	private NodeType(int value) {
		this.base_value = value;
	}

	public int getBaseValue() {
		return base_value;
	}
	
}
