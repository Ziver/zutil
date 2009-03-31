/**
 * 
 */
package zutil.algo;

import java.math.BigInteger;

/**
 * The algorithm solves the discreet log equation x^2 = n mod p
 * 
 * @author Ziver
 * @see http://en.wikipedia.org/wiki/Shanks-Tonelli_algorithm
 */
public class ShanksTonelliAlgo {
	public static BigInteger calc(BigInteger n, BigInteger p){
		
		BigInteger nOrg = n;
		BigInteger S = null, V, R, U, X;
		BigInteger ONE = BigInteger.ONE;
		BigInteger TWO = BigInteger.valueOf( 2 );
		BigInteger Q = p.add( ONE ).divide( TWO );

		switch( p.mod( BigInteger.valueOf(4) ).intValue() ){
		case 3:
			S = n.pow( Q.divide( TWO ).intValue() ).mod( p );
			break;
		case 1:
			S = ONE;
			n = n.subtract( ONE );
			while (n.divide( p ).compareTo( ONE ) == 0) {
				S = S.add( ONE );
				//n = (n-2s+1) mod p
				n = n.subtract( TWO.multiply( S ) ).add( ONE ).mod( p );
				if (n.compareTo( BigInteger.ZERO ) == 0){
					return S;
				}
			}
			Q = Q.divide( TWO );                        
			V = ONE;
			R = S;
			U = ONE;
			while (Q.compareTo( BigInteger.ZERO ) > 0) {
				X = R.pow(2).subtract( n.multiply( U.pow(2) ) ).mod( p );      
				U = TWO.multiply( R ).multiply( U ).mod( p );
				R = X;
				if ( Q.testBit(0) ){
					X = S.multiply( R ).subtract( n.multiply(V).multiply(U) ).mod( p );
					V = V.multiply(R).add( S.multiply(U) ).mod( p );
					S = X;
				}
				Q = Q.divide( TWO );
			}
		}

		if( S != null && S.multiply( S ).mod( p ).compareTo( nOrg ) != 0 ){
			return null;
		}

		return S;
		/*
		
		//p-1 = Q*2^S
		BigInteger S = null, Q = null, R = null, V = null, W = null;

		//Q = ( 2^S )/( 1-p );
		p-1 = ( 2^S )/( 1-p ) * 2^S;

		// R = n^( (Q+1)/2 ) mod p
		R = n.pow( Q.add(BigInteger.ONE).divide(BigInteger.valueOf(2)).intValue() ).mod( p );
		// V = W^Q mod p
		V = W.pow( Q.intValue() ).mod( p );

		for(int i=S.intValue(); true ;){			
			while( true ){
				i--;
				// 1 = ( ( R^2 * n^-1 )^2^i ) mod p
				if( ( R.pow(2).multiply( n.pow(-1) ) ).pow( (int)Math.pow(2, i) ).mod( p ).compareTo( BigInteger.ONE ) == 0 ) 
					break;
			}

			if(i == 0) return R;
			//R = ( RV^(2^(S-i-1)) ) mod p
			else R = ( R.multiply( V.pow( (int)Math.pow( 2, S.intValue()-i-1) ) )).mod( p ); 
		}
		*/
	}
}
