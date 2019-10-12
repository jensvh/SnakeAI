package me.jensvh.snakeai.oldschool;

public class MatrixTester {
	
	static float[][] array1 = {
			{1},
			{2}
	};
	
	static float[][] array2 = {
			{1, 2}
	};
	
	public static void main(String[] args) throws Exception {
		Matrix matrix1 = new Matrix(array1);
		Matrix matrix2 = new Matrix(array2);
		
		matrix2.transpose();
		
		System.out.println(matrix2.toString());
	}

}
