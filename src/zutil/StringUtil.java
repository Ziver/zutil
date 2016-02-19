/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil;

import zutil.converters.Converter;

import java.util.ArrayList;
import java.util.Iterator;

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
	 * @param   bytes   size (in bytes)
	 * @return string
	 */
	public static String formatByteSizeToString(long bytes){
		int total = sizes.length-1;
		double value = bytes;
		
		for(; value > 1024 ;total--) {
			value /= 1024;
		}

		value = (double)( (int)(value*10) )/10;
		return value+" "+sizes[total];
	}
	
	public static String formatTimeToString(long milisec){
		StringBuilder str = new StringBuilder();
		long tmp = 0;
		
		// Years
		if( milisec >= 31557032762.3361d ){
			tmp = (long) (milisec / 31557032762.3361d);
			milisec -= tmp * 31557032762.3361d;
			if( tmp > 1 ) 
				str.append(tmp).append(" years ");
			else 
				str.append(tmp).append(" year ");
		}
		// Months
		if( milisec >= 2629743830l ){
			tmp = (long) (milisec / 2629743830l);
			milisec -= tmp * 2629743830l;
			if( tmp > 1 ) 
				str.append(tmp).append(" months ");
			else 
				str.append(tmp).append(" month ");
		}		
		// Days
		if( milisec >= 86400000 ){
			tmp = (long) (milisec / 86400000);
			milisec -= tmp * 86400000;
			if( tmp > 1 ) 
				str.append(tmp).append(" days ");
			else 
				str.append(tmp).append(" day ");
		}
		// Hours
		if( milisec >= 3600000 ){
			tmp = (long) (milisec / 3600000);
			milisec -= tmp * 3600000;
			if( tmp > 1 ) 
				str.append(tmp).append(" hours ");
			else 
				str.append(tmp).append(" hour ");
		}
		// Minutes
		if( milisec >= 60000 ){
			tmp = (long) (milisec / 60000);
			milisec -= tmp * 60000;
			str.append(tmp).append(" min ");
		}
		// sec
		if( milisec >= 1000 ){
			tmp = (long) (milisec / 1000);
			milisec -= tmp * 1000;
			str.append(tmp).append(" sec ");
		}
		if( milisec > 0 ){
			str.append(milisec).append(" milisec ");
		}
		
		return str.toString();
	}

    /**
     * Generates a String where the number has been prefixed
     * with zeros until the string has the wanted size.
     *
     * @param      number
     * @param      length
     * @return a new String with the given length or longer if the number has more characters.
     */
    public static String prefixInt(int number, int length){
        StringBuilder str = new StringBuilder().append(number).reverse();
        while (str.length() < length)
            str.append('0');
        return str.reverse().toString();
    }

    /**
     * @param   list        a list of object that toString() will be called on
     * @param   delimiter   a String delimiter that will be added between every entry in the list
     * @return a String containing all entries in the list with the specified delimiter in between entries
     */
	public static String join(Iterable<?> list, String delimiter){
        StringBuilder str = new StringBuilder();
        Iterator<?> it = list.iterator();
        if(it.hasNext()) {
            str.append(it.next().toString());
            while (it.hasNext()) {
                str.append(delimiter).append(it.next().toString());
            }
        }
        return str.toString();
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
	 * @param       str		is the string to trim
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

    /**
     * Presents a binary array in HEX and ASCII
     *
     * @param       data     The source binary data to format
     * @return A multiline String with human readable HEX and ASCII
     */
    public static String formatBytesToString(byte[] data){
        StringBuffer output = new StringBuffer();

        //000  XX XX XX XX XX XX XX XX  '........'
        int maxOffset = (""+data.length).length();
        for(int offset=0; offset<data.length; offset+=8){
            if(offset != 0)
                output.append('\n');

            // Offset
            String offsetStr = ""+offset;
            for(int i=offsetStr.length(); i<3 || i<maxOffset; ++i){
                output.append('0');
            }
            output.append(offsetStr);
            output.append("  ");

            // HEX
            for(int i=0; i<8; ++i){
                if(offset+i < data.length)
                    output.append(Converter.toHexString(data[offset+i]));
                else
                    output.append("  ");
                output.append(' ');
            }
            output.append(' ');

            // ACII
            output.append('\'');
            for(int i=0; i<8; ++i){
                if(offset+i < data.length)
                    if( 32 <= data[offset+i] && data[offset+i] <= 126 )
                        output.append((char)data[offset+i]);
                    else
                        output.append('.');
                else
                    output.append(' ');
            }
            output.append('\'');
        }

        return output.toString();
    }


	private static ArrayList<String> SPACES = new ArrayList<String>();
	/**
	 * @return A string containing a specific amount of spaces
	 */
	public static String getSpaces(int i){
		if(SPACES.size() <= i){ // Do we need to generate more spaces?
			synchronized (SPACES) { // Make sure no one else updates the list at the same time
				if(SPACES.size() <= i) { // Make sure the previous synchronized thread hasn't already generated strings
					if (SPACES.isEmpty())
						SPACES.add("");
					for (int j = SPACES.size(); j <= i; j++) {
						SPACES.add(SPACES.get(j - 1) + " ");
					}
				}
			}
		}
		return SPACES.get(i);
	}
}
