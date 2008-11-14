package zutil.math;

/**
 * Very Simple  math functions
 * 
 * @author Ziver
 */
public class ZMath {
	
	/**
	 * Calculates the percentige the value has
	 */
	public static double percent(int min, int max, int value){
		return ((double)(value-min)/(max-min))*100;
	}
}
