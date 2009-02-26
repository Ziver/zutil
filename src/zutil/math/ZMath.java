package zutil.math;

import java.math.BigInteger;

/**
 * Very Simple  math functions
 * 
 * @author Ziver
 */
public class ZMath {
	
	/**
	 * Calculates the percentage of the values
	 */
	public static double percent(int min, int max, int value){
		return ((double)(value-min)/(max-min))*100;
	}
	
	/**
	 * Solves the equation: x^2 + px + q = 0
	 * 
	 * @return the two values of x as an array
	 */
	public static double[] pqFormula(double p, double q){
		double[] ret = new double[2];
		double t = (p/2);
		ret[0] = Math.sqrt( t*t - q );
		ret[1] = -ret[0];
		t *= -1;
		ret[0] += t;
		ret[1] += t;
		return ret;
	}
	
	/**
	 * Solves the equation: x^2 + px + q = 0.
	 * WARNING: This uses only BigInteger, thereby removing the decimals in the calculation
	 * 
	 * @return the two values of x as an array
	 */
	public static BigInteger[] pqFormula(BigInteger p, BigInteger q){
		BigInteger[] ret = new BigInteger[2];
		BigInteger t = p.divide( BigInteger.valueOf(2) );
		ret[0] = ZMath.sqrt( t.multiply( t ).subtract( q ) );
		ret[1] = ret[0].negate();
		t = t.negate();
		ret[0] = ret[0].add( t );
		ret[1] = ret[1].add( t );
		return ret;
	}
	
	/**
	 * Calculates the square root of a big number
	 * 
	 */
	public static BigInteger sqrt(BigInteger value){
		BigInteger op = value;
		BigInteger res = BigInteger.ZERO;
		BigInteger one = BigInteger.ONE;

		while( one.compareTo( op ) < 0 ){
			one = one.shiftLeft( 2 );
		}
		one = one.shiftRight(2);
		
		while( !one.equals( BigInteger.ZERO ) ){
			if( op.compareTo( res.add( one ) ) >= 0 ){
				op = op.subtract( res.add( one ) );
				res = res.add( one.shiftLeft( 1 ) );
			}
			res = res.shiftRight( 1 );
			one = one.shiftRight( 2 );
		}
		
		return res;
	}
}
