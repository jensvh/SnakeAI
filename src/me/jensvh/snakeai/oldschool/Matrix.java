package me.jensvh.snakeai.oldschool;

import java.util.Random;

import me.jensvh.snakeai.Settings;

public class Matrix {
	
	private float[][] matrix;
	
	private int rows;
	private int columns;
	
	/**
	 * Instantiate new empty matrix.
	 * @param rows The amount of rows.
	 * @param columns The amount of columns.
	 */
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		
		this.matrix = new float[rows][columns];
	}

	/**
	 * Instantiate matrix from single array. Handy for creating a matrix out of inputs.
	 * @param array Single array.
	 */
	public Matrix(float[] array) {
		this.rows = 1;
		this.columns = array.length;
		
		this.matrix = new float[rows][columns];
		
		for (int i = 0; i < array.length; i++) {
			matrix[0][i] = array[i];
		}
	}
	
	/**
	 * Instantiate matrix from array.
	 * @param array The array from which a matrix is formed.
	 */
	public Matrix(float[][] array) {
		this(array.length, array[0].length);
		
		// Copy array into matrix
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = array[row][column];
			}
		}
	}
	
	
	/**
	 * Multiply this matrix with another one.
	 * @param other The other matrix.
	 * @throws Exception Throws an Exception when the multiplication is impossible due to wrong dimensions.
	 */
	public void multiply(Matrix other) throws Exception {
		// Automatic transpose the first matrix
		if (this.columns != other.rows && this.rows == other.rows) {
			this.transpose();
		}
		
		// Check for wrong dimensions
		if (this.columns != other.rows) {
			throw new Exception("Unable to multiply, wrong dimensions.");
		}
		
		float[][] newMatrix = new float[this.rows][other.columns];
		
		// Multiply value's
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < other.columns; j++) {
				float newValue = 0;
				for (int k = 0; k < this.columns; k++) {
					newValue += this.matrix[i][k] * other.matrix[k][j];
				}
				
				newMatrix[i][j] = newValue;
			}
		}
		
		matrix = newMatrix.clone();
		// The rows are the same
		columns = other.columns;
		
	}
	
	
	/**
	 * Transpose the matrix. It's like mirroring around the diagonal from the top left to the right bottom.
	 */
	public void transpose() {
		float[][] transposed = new float[columns][rows];
		
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				transposed[column][row] = matrix[row][column];
			}
		}
		
		columns = rows;
		rows = transposed.length;
		
		matrix = transposed.clone();
	}
	
	
	/* ------------------- GA ------------------------ */
	/**
	 * Give the matrix random values between -1 and 1.
	 */
	public void randomize() {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = (new Random().nextFloat() * 2) - 1;
			}
		}
	}
	

	/**
	 * Sigmoid each value in the matrix.
	 */
	public void sigmoid() {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				matrix[row][column] = sigmoid(matrix[row][column]);
			}
		}
	}
	
	
	/**
	 * Sigmoid a float value.
	 * @param value The value to be sigmoided.
	 * @return The sigmoided value.
	 */
	private float sigmoid(float value) {
		return 1.0f / (1.0f + (float) Math.pow(Math.E, -value));
	}
	
	
	/**
	 * Add a bias. It adds a one at the end of the matrix.
	 * @throws Exception When the matrix does not represent a layer.
	 */
	public void addBias() throws Exception {
		// Only add a bias if the matrix represents a layer of nodes, i.e. If there is only one row or one column.
		if (!(rows == 1 || columns == 1)) {
			throw new Exception("Unable to add a bias to a non layer matrix.");
		}
		
		if (rows == 1) {
			float[][] newMatrix = new float[rows][columns + 1];
			
			for (int column = 0; column < columns; column++) {
				newMatrix[0][column] = matrix[0][column];
			}
			
			// Add bias value
			newMatrix[0][columns] = 1;
			
			matrix = newMatrix.clone();
			
			columns++;
		} else {
			float[][] newMatrix = new float[rows+1][columns];
			
			for (int row = 0; row < rows; row++) {
				newMatrix[row][0] = matrix[row][0];
			}
			
			// Add bias value
			newMatrix[rows][0] = 1;
			
			matrix = newMatrix.clone();
			
			rows++;
		}
	}
	
	
	/**
	 * Mutate every connections with a chance set in settings, i.e. each value in the matrix.
	 */
	public void mutateEachElement() {
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				// Mutation rate
				if (new Random().nextFloat() < Settings.MUTATION_RATE) {
					matrix[row][column] += new Random().nextGaussian()/5; //TODO: gaussian/5
					
					// Keep value between boundaries
					if (matrix[row][column] > 1) {
						matrix[row][column] = 1;
					} else if (matrix[row][column] < -1) {
						matrix[row][column] = -1;
					}
				}
			}
		}
	}
	
	/**
	 * First take a chance to mutate this matrix, then mutate each element by a chance.
	 */
	public void mutateMatrix() {
		float rand = new Random().nextFloat();
		
		if (rand < Settings.MUTATION_RATE) {
			// Mutate
			mutateEachElement();
		}
	}
	
	
	/**
	 * Crossover two matrices, split the two matrices at a specific point and melt them together.
	 * @param parent The other matrix
	 * @return Melted matrix
	 */
	public Matrix pointCrossover(Matrix parent) {
		Matrix child = new Matrix(rows, columns);
		
		// Random point in matrix
		int randomRow = new Random().nextInt(rows);
		int randomColumn = new Random().nextInt(columns);
		
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				if (row < randomRow || (row == randomRow && column <= randomColumn)) {
					child.matrix[row][column] = this.matrix[row][column];
				} else {
					child.matrix[row][column] = parent.matrix[row][column];
				}
			}
		}
		
		return child;
	}
	
	/**
	 * Crossover two matrices, Randomly choose a value from one of the matrices.
	 * @param parent The other matrix
	 * @return New random matrix
	 */
	public Matrix randomCrossover(Matrix parent) {
		Matrix child = new Matrix(rows, columns);
		
		// Foreach value
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				// Randomly choose one of the matrices value
				child.matrix[row][column] = (new Random().nextFloat() > 0.5f) ? this.matrix[row][column] : parent.matrix[row][column];
			}
		}
		
		return child;
	}
	
	
	public Matrix clone() {
		Matrix clone = new Matrix(rows, columns);
		
		//Copying items into new matrix
		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				clone.matrix[row][column] = this.matrix[row][column];
			}
		}
		
		return clone;
	}
	
	/* ----------------- Getters and Setters ------------------------ */
	
	public void setValue(int row, int column, float value) {
		matrix[row][column] = value;
	}
	
	/**
	 * Returns the matrix into a single array if possible. Handy for retrieving outputs.
	 * @return A single matrix.
	 * @throws Exception Throws Exception when the matrix has the wrong dimensions for a single array.
	 */
	public float[] toArray() throws Exception {
		if (!(this.rows == 1 || this.columns == 1)) {
			throw new Exception("Unable to create array out of matrix.");
		}
		
		float[] arr = new float[rows*columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				arr[j+i*columns] = matrix[i][j];
			}
		}
		
		return arr;
		
		/*
		if (rows == 1) {
			float[] array = new float[columns];
			
			for (int i = 0; i < columns; i++) {
				array[i] = matrix[0][i];
			}
			
			return array;
		} else {
			float[] array = new float[rows];
			
			for (int i = 0; i < rows; i++) {
				array[i] = matrix[i][0];
			}
			
			return array;
		}*/
	}
	
	@Override
	public String toString() {
		String str = "";
		
		for (int row = 0; row < rows; row++) {
			str += "[";
			for (int column = 0; column < columns; column++) {
				str += " " + matrix[row][column] + " ";
			}
			
			str += "] \n";
		}
		
		return str;
	}
}
