package zutil.algo;

import java.math.BigInteger;
import java.util.LinkedList;

import zutil.math.ZMath;

/**
 * The Wieners algorithm factorizes two big numbers a and b.
 * It uses the Euclidien algorithm to calculate the generator of the
 * numbers and then uses them to calculate the factorization.
 * 
 * @author Ziver
 *
 */
public class WienersAlgo {
	
	/**
	 * Runs the Wieners algorithm for the given values.
	 * 
	 * @param n is the first value
	 * @param e is the second value
	 * @return a BigInteger array of length two. 
	 * 				First index is p and second is q. 
	 * 				If no value was found then it returns null. 
	 */
	public static BigInteger[] calc(BigInteger n, BigInteger e){
		BigInteger[] ret = null;
		
		LinkedList<BigInteger> gen = EuclideansAlgo.calcGenerators(e, n);
		
		BigInteger c0 = BigInteger.ONE;
		BigInteger c1 = gen.poll();
		BigInteger d0 = BigInteger.ZERO;
		BigInteger d1 = BigInteger.ONE;
		
		BigInteger t, n1, g;		
		while(!gen.isEmpty()){
			g = gen.poll();
			
			t = c1;
			c1 = g.multiply( c1 ).add( c0 );
			c0 = t;
			
			t = d1;
			d1 = g.multiply( d1 ).add( d0 );
			d0 = t;
			
			n1 = d1.multiply( e ).subtract( BigInteger.ONE );
			if( n1.mod( c1 ).equals( BigInteger.ZERO ) ){
				n1 = n1.divide( c1 );
				
				// x^2 - ( n - n1 +1 )x + n = 0
				ret = ZMath.pqFormula(
						n.subtract( n1 ).add( BigInteger.ONE ).negate(),
						n);
				
				if(ret[0].compareTo( BigInteger.ZERO ) >= 0 &&
						ret[1].compareTo( BigInteger.ZERO ) >= 0 &&
						ret[0].multiply( ret[1] ).equals( n )){
					return ret;
				}
			}
		}
		
		return null;		
	}
}
