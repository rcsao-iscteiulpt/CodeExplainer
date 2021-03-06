package pt.iscte.paddle.codexplainer;

public class BasicFunctions {

	static int abs(int n) {
		if(n < 0)
			return -n;
		return n;
	}
	
	double summation(double[] array) {
		double sum = 0.0;
		int i = 0;
		while(i != array.length) {
			sum = sum + array[i];
			i = i + 1;
		}
		return sum;
	}
	
	boolean isPrime(int n) {
	    int i = 2;
	    while(i < n) {	
	        if(n%i == 0) {
	            return false;
	        }    
	        i = i + 1;
	    }
	    return true;    
	}
	
	int max(int[] array) {
	    int m = 0;
	    int i = 1;
		while(i < array.length) {
			if(array[i] > m) {
				m = array[i];
			}
			i = i + 1;
		}
		return m;
	}
	
	static int factorial(int n) {     
		if(n == 0) {
			return 1;
		} else {
			return n * factorial(n - 1);
		}
	}	
	
	static int[] naturals(int n) {
		int[] array = new int[n];
		
		int i = 0;
		while(i < array.length) {
			array[i] = i + 1;
			i = i + 1;
		}
		
		return array;
	}
	
	static int[] subArray(int[] array, int from, int to) {
		int[] subArray = new int[to - from + 1];
		
		int i = from;
		while(i <= to) {
			subArray[i - from] = array[i];
			i = i + 1;
		}
		return subArray;
	}
	
	boolean contains(int[][] matrix, int n) {
		int i = 0;
		while(i < matrix.length) {
			int j = 0;
			while(j < matrix[i].length) {
				if(matrix[i][j] == n) {
					return true;
				}
				j = j + 1;	
			}
			i = i + 1;	
		}
		return false;	
	}
	
	
	public static void main(String[] args) {
		//System.out.println(isPrime(6));
		
		//int[] array = new int[]{1,4,6,2};
		//System.out.println(max(array));
		
		//System.out.println(abs(146));
		
		//System.out.println(factorial(2));
		
		
	//	int[] v = naturals(7);
//		for(int i = 0; i < v.length; i++) {
//			System.out.println(v[i]);
//		}
		
//		int[] v1 = subArray(v, 2, 4);
//		for(int i = 0; i < v.length; i++) {
//			System.out.println(v1[i]);
//		}
		
//		int[][] m = {{1,2,9},{3,4,1}};
//		System.out.println(contains(m, 5));
		
	}
}
