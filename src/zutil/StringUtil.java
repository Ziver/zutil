package zutil;

/**
 * This is a class whit utility methods.
 * 
 * @author Ziver *
 */
public class StringUtil {
	public static final String[] sizes = new String[]{"YB", "ZB", "EB", "PB", "TB", "GB", "MB", "kB", "B"};

	/**
	 * Present a size (in bytes) as a human-readable value
	 *
	 * @param size size (in bytes)
	 * @return string
	 */
	public static String formatBytesToString(long bytes){
		int total = sizes.length-1;
		double value = bytes;
		
		for(; value > 1024 ;total--) {
			value /= 1024;
		}
		
		value = (double)( (int)(value*10) )/10;
		return value+" "+sizes[total];
	}
	
	
}
