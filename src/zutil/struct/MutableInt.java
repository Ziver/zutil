package zutil.struct;

/**
 * A simple class that only contains a public int. Can be used in
 * recursive functions as a persistent integer.
 *  
 * @author Ziver
 */
public class MutableInt {
	public int i = 0;
	
	public MutableInt(){}
	
	public MutableInt(int i){
		this.i = i;
	}
}
