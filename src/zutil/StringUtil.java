package zutil;

/**
 * This is a class whit utility methods.
 * 
 * @author Ziver *
 */
public class StringUtil {

	/**
	 * Present a size (in bytes) as a human-readable value
	 *
	 * @param size size (in bytes)
	 * @return string
	 */
	public static String formatBytesToString(long bytes){
		String[] sizes = new String[]{"YB", "ZB", "EB", "PB", "TB", "GB", "MB", "kB", "B"};
		int total = sizes.length-1;
		double value = bytes;
		
		for(; value > 1024 ;total--) {
			value /= 1024;
		}
		
		value = (double)( (int)(value*100) )/100;
		return value+" "+sizes[total];
	}
	
	
}
