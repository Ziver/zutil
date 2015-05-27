/*
 * Copyright (c) 2015 ezivkoc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
			
			// (d1*e-1) % c1 == 0
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
