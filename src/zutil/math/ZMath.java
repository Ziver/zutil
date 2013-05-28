/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

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
