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
	
	/**
	 * Trims the given char and whitespace at the beginning and the end
	 * 
	 * @param 		str		is the string to trim
	 * @param		trim	is the char to trim
	 * @return				a trimmed String
	 */
	public static String trim(String str, char trim){
		if( str == null || str.isEmpty() )
			return str;
		int start=0, stop=str.length();
		// The beginning
		for(int i=0; i<str.length() ;i++){
			char c = str.charAt( i );
			if( c <= ' ' || c == trim ) 
				start = i+1;
			else
				break;
		}
		// The end
		for(int i=str.length()-1; i>start ;i--){
			char c = str.charAt( i );
			if( c <= ' ' || c == trim ) 
				stop = i;
			else
				break;
		}
		if( start >= str.length() )
			return "";
		//System.out.println("str: \""+str+"\" start: "+start+" stop: "+stop);
		return str.substring(start, stop);
	}
	
	/**
	 * Trims the whitespace and quotes if the string starts and ends with one
	 * 
	 * @param 		str		is the string to trim
	 * @return
	 */
	public static String trimQuotes(String str){
		if( str == null )
			return null;
		str = str.trim();
		if( str.length() >= 2 && str.charAt(0)=='\"' && str.charAt(str.length()-1)=='\"'){
			str = str.substring(1, str.length()-1);
		}
		return str;
	}
}
