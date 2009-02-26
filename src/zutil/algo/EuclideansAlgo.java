package zutil.algo;

import java.math.BigInteger;
import java.util.LinkedList;

import zutil.MultiPrintStream;

/**
 * Euclidean algorithm is an algorithm to determine 
 * the greatest common divisor (GCD)
 * 
 * @author Ziver
 *
 */
public class EuclideansAlgo {

	/**
	 * Simple Test
	 * @param args
	 */
	public static void main(String[] args){
		MultiPrintStream.out.println("*** Correct Answer: ");
		MultiPrintStream.out.println("java.util.LinkedList{0, 2, 1, 1, 1, 4, 12, 102, 1, 1, 2, 3, 2, 2, 36}");
		MultiPrintStream.out.println("GCD: 1");
		
		MultiPrintStream.out.println("*** Integer:");
		MultiPrintStream.out.dump(calcGenerators(60728973, 160523347));
		MultiPrintStream.out.println("GCD: "+calc(60728973, 160523347));
		
		MultiPrintStream.out.println("*** BigInteger: ");
		MultiPrintStream.out.dump(calcGenerators(new BigInteger("60728973"), new BigInteger("160523347")));
		MultiPrintStream.out.println("GCD: "+calc(new BigInteger("60728973"), new BigInteger("160523347")));
	}
	
	/**
	 * Runs the Euclidean algorithm on the two input 
	 * values.
	 * 
	 * @param a is the first integer
	 * @param b is the second integer
	 * @return a integer containing the GCD of the integers
	 */
	public static int calc(int a, int b){
		int t;
		while( b != 0 ){
			t = b;
			b = a % b;
			a = t;
		}

		return a;
	}
	
	/**
	 * Runs the Euclidean algorithm on the two input 
	 * values.
	 * 
	 * @param a is the first BigInteger
	 * @param b is the second BigInteger
	 * @return a BigInteger containing the GCD of the BigIntegers
	 */
	public static BigInteger calc(BigInteger a, BigInteger b){
		BigInteger t;
		
		while( !b.equals(BigInteger.ZERO) ){
			t = b;
			b = a.mod( b );
			a = t;
		}

		return a;
	}

	/**
	 * Runs the Euclidean algorithm on the two input 
	 * values to find the generators for the values.
	 * 
	 * @param a is the first integer
	 * @param b is the second integer
	 * @return a list of integers that is generators for a and b
	 */
	public static LinkedList<Integer> calcGenerators(int a, int b){
		LinkedList<Integer> list = new LinkedList<Integer>();
		int t;
		
		while( b != 0 ){
			list.add( a/b );
			t = b;
			b = a % b;
			a = t;
		}
		
		return list;
	}
	
	/**
	 * Runs the Euclidean algorithm on the two input 
	 * values to find the generators for the values.
	 * 
	 * @param a is the first BigInteger
	 * @param b is the second BigInteger
	 * @return a list of BigIntegers that is generators of a and b
	 */
	public static LinkedList<BigInteger> calcGenerators(BigInteger a, BigInteger b){
		LinkedList<BigInteger> list = new LinkedList<BigInteger>();
		BigInteger t;
		
		while( !b.equals(BigInteger.ZERO) ){
			list.add( new BigInteger("0").add( a.divide( b ) ) );
			t = b;
			b = a.mod( b );
			a = t;
		}

		return list;
	}
}
