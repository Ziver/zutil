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

package zutil.converter;

import zutil.io.DynamicByteArrayStream;
import zutil.parser.Base64Decoder;

import java.io.*;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

public class Converter {
	private Converter(){}

	/** 
	 * Converts an object to an array of bytes.
	 * 
	 * @param   object  the object to convert.
	 * @return the associated byte array.
	 */
	public static byte[] toBytes(Object object) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(object);
		oos.flush();
		oos.close();

		return baos.toByteArray();
	}
	
	/**
	 * Converts a Integer to an byte array
	 * 
	 * @param 	num 	is the number to convert
	 * @return a byte array of four bytes
	 */
	public static byte[] toBytes(int num){
		return new byte[]{
				(byte)(num & 0xff),
				(byte)((num >> 8)& 0xff),
				(byte)((num >> 16)& 0xff),
				(byte)((num >> 24)& 0xff)};
	}

    /**
     * Converts a char array to a byte array.
     *
     * @return a byte array with the same binary value as the char.
     */
	public static byte[] toBytes(char[] arr){
        byte[] ret = new byte[arr.length];
		for (int i=0; i<arr.length; ++i)
			ret[i] = (byte) (arr[i] & 0xFF);
        //System.arraycopy(arr, 0, ret, 0, arr.length); // does not work if char value is largen than 128
        return ret;
    }
	
	/**
	 * Converts a Integer to a byte
	 * 
	 * @param 	num 	is the number to convert
	 * @return a byte value of the integer
	 */
	public static byte toByte(int num){
		return (byte)(num & 0xff);
	}

    /**
     * Converts hex string to a byte array
     *
     * @param   hex   a String containing data coded in hex
     * @return a byte array
     */
    public static byte[] hexToByte(String hex){
        if(hex == null)
            return null;
        if(hex.startsWith("0x"))
            hex = hex.substring(2);
        byte[] b = new byte[(int)Math.ceil(hex.length()/2.0)];
        for(int i=0; i<hex.length(); i+=2){
            if(i+1 >= hex.length())
                b[(i+1)/2] = hexToByte(hex.charAt(i), '0');
            else
                b[(i+1)/2] = hexToByte(hex.charAt(i), hex.charAt(i+1));
        }
        return b;
    }

	/**
	 * Converts hex chars to a byte
	 * 
	 * @param   quad1   is the first hex value
	 * @param   quad2   is the second hex value
	 * @return a byte that corresponds to the hex
	 */
	public static byte hexToByte( char quad1, char quad2){
		byte b = hexToByte( quad2 );
		b |= hexToByte( quad1 ) << 4;
		return b;
	}
	
	/**
	 * Converts a hex chars to a byte
	 * 
	 * @param 	hex     is the hex value
	 * @return a byte that corresponds to the hex
	 */
	public static byte hexToByte( char hex ){
		switch( Character.toLowerCase(hex) ){
		case '0': return 0x00;
		case '1': return 0x01;
		case '2': return 0x02;
		case '3': return 0x03;
		case '4': return 0x04;
		case '5': return 0x05;
		case '6': return 0x06;
		case '7': return 0x07;
		case '8': return 0x08;
		case '9': return 0x09;
		case 'a': return 0x0a;
		case 'b': return 0x0b;
		case 'c': return 0x0c;
		case 'd': return 0x0d;
		case 'e': return 0x0e;
		case 'f': return 0x0f;
		}
		return (byte)0;
	}

	/** 
	 * Converts an array of bytes back to its constituent object. The 
	 * input array is assumed to have been created from the original object.
	 * 
	 * @param   bytes   the byte array to convert.
	 * @return the associated object.
	 */
	public static Object toObject(byte[] bytes) throws Exception{
		Object object = null;

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
		object = ois.readObject();
		ois.close();

		return object;
	}
	
	/** 
	 * Converts an array of bytes back to its constituent object. The 
	 * input array is assumed to have been created from the original object.
	 * 
	 * @param   bytes   the byte array to convert.
	 * @return the associated object.
	 */
	public static Object toObject(DynamicByteArrayStream bytes) throws Exception{
		Object object = null;

		ObjectInputStream ois = new ObjectInputStream(bytes);
		object = ois.readObject();
		ois.close();

		return object;
	}


    /**
     * Converts a byte to a Bit String (e.g 01100100)
     *
     * @param   raw     the byte array to convert
     * @return a bit String
     */
	public static String toBitString(byte raw){
		StringBuilder str = new StringBuilder(8);
		for (int i=0; i<8; ++i) {
			str.append(raw & 0x01);
			raw >>>= 1;
		}
		return str.reverse().toString();
	}

	/** array needed for byteToHex */
	private static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	/**
	 * Converts a byte Array to a Hex String
	 * 
	 * @param   raw     the byte array to convert
	 * @return a hex String
	 */
	public static String toHexString(byte[][] raw){
		StringBuffer ret = new StringBuffer();
		
		for(byte[] a : raw){
			for(byte b : a){
				ret.append(HEX_CHARS[(int) (b >>> 0x04)& 0x0F ]);
				ret.append(HEX_CHARS[(int) b & 0x0F ]);
			}
		}

		return ret.toString();
	}

	public static String toHexStringByColumn(byte[][] raw){
		StringBuffer ret = new StringBuffer();
		
		for(int col=0; col<raw[0].length ;col++){
			for(int row=0; row<raw.length ;row++){
				ret.append(HEX_CHARS[(int) (raw[row][col] >>> 0x04)& 0x0F ]);
				ret.append(HEX_CHARS[(int) raw[row][col] & 0x0F ]);
			}
		}

		return ret.toString();
	}
	
	/**
	 * Converts a byte Array to a Hex String
	 * 
	 * @param   raw     the byte array to convert
	 * @return a hex String
	 */
	public static String toHexString(byte[] raw){
		StringBuffer ret = new StringBuffer();

		for(byte b : raw){
			ret.append(HEX_CHARS[(int) (b >>> 0x04)& 0x0F ]);
			ret.append(HEX_CHARS[(int) b & 0x0F ]);
		}

		return ret.toString();
	}

	/**
	 * Converts a byte to a Hex String
	 * 
	 * @param   raw     the byte to convert
	 * @return a hex String
	 */
	public static String toHexString(byte raw){
		String ret = ""+HEX_CHARS[(int) (raw >>> 0x04)& 0x0F ];
		ret += ""+HEX_CHARS[(int) raw & 0x0F ];

		return ret;
	}

	/**
	 * Converts the given byte to a String with 1's and 0's
	 * 
	 * @param   raw     the byte to convert
	 * @return a String with 1's and 0's
	 */
	public static String toString(byte raw){
		StringBuffer ret = new StringBuffer();
		for(int i=128; i>0 ;i=( i<1 ? i=0 : i/2 ) ){
			ret.append(( (raw & i) == 0 ? '0' : '1'));
		}
		return ret.toString();
	}

	/**
	 * Converts the given byte array to a String with 1's and 0's
	 * 
	 * @param   raw     the byte array to convert
	 * @return a String with 1's and 0's
	 */
	public static String toString(byte[] raw){
		StringBuffer ret = new StringBuffer();
		for(byte b : raw){
			for(int i=128; i>0 ;i=( i<1 ? i=0 : i/2 ) ){
				ret.append(( (b & i) == 0 ? '0' : '1'));
			}
		}
		return ret.toString();
	}

    /**
     * Generates a comma separated string with key and value pairs
     *
     * @return a comma separated String
     */
	public static String toString(Map map){
		StringBuilder tmp = new StringBuilder();
		tmp.append("{");
		Iterator<Object> it = map.keySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
            Object value = map.get(key);
			tmp.append(key);
            if (value != null) {
                if (value instanceof String)
                    tmp.append(": \"").append(value).append("\"");
                else
                    tmp.append(value);
            }
            else
                tmp.append("null");

			if(it.hasNext())
				tmp.append(", ");
		}
		tmp.append('}');
		return tmp.toString();
	}

	/**
	 * Converts a BitSet to a Integer
	 * 
	 * @param   bits    the BitSet to convert
	 * @return a Integer
	 */
	public static int toInt(BitSet bits){
		int ret = 0;

		for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1)) {
			ret += Math.pow(2, i);
		}

		return ret;
	}
	
	/**
	 * Converts a boolean array(bit sequence whit most significant bit at index 0) to a Integer
	 * 
	 * @param   bits    the boolean array to convert
	 * @return a Integer
	 */
	public static int toInt(boolean[] bits){
		int ret = 0;

		for (int i = bits.length-1; i >= 0; i--) {
			if(bits[i])ret += Math.pow(2, bits.length-i-1);
		}

		return ret;
	}
	
	/**
	 * Converts a byte to a integer
	 * 
	 * @param   b   is the byte to convert
	 * @return the integer value of the byte
	 */
	public static int toInt(byte b){
		return (int)(b & 0xff);
	}

	/**
	 * Converts a dynamic sized byte array to a integer
	 *
	 * @param   b   is the byte array of size 1-4
	 * @return the int value of the byte array
	 */
	public static int toInt(byte[] b){
		int i = 0;
		switch (b.length){
			default:
			case 4:	i |= 0xFF000000 & (b[3] << 24);
			case 3: i |= 0x00FF0000 & (b[2] << 16);
			case 2: i |= 0x0000FF00 & (b[1] << 8);
			case 1:	i |= 0x000000FF &  b[0];
			case 0: break;
		}
		return i;
	}

	/**
	 * Converts a Integer to a BitSet
	 * 
	 * @param   num     the Integer to convert
	 * @return a BitSet object
	 */
	public static BitSet toBitSet(int num){
		BitSet ret = new BitSet();		
		String tmp = Integer.toBinaryString(num);

		for(int i=0; i<tmp.length() ;i++){
			ret.set(i , tmp.charAt(tmp.length()-i-1) != '0');
		}		
		return ret;
	}
	

	/**
	 * Converts a given String to a specified class
	 * 
	 * @param   <T>     is the resulting class
	 * @param   data    is the String data to be converted
	 * @param   c       is the class to convert to
	 * @return a instance of the class with the value in the string or null if there was an problem
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromString(String data, Class<T> c){
		if(data == null || data.isEmpty())
			return null;
		try{
			if(     c == String.class) 		return (T) data;
			else if(c == Integer.class) 	return (T) new Integer(data);
			else if(c == int.class) 		return (T) new Integer(data);
			else if(c == Long.class) 		return (T) new Long(data);
			else if(c == long.class) 		return (T) new Long(data);
			else if(c == Float.class) 		return (T) new Float(data);
			else if(c == float.class) 		return (T) new Float(data);
			else if(c == Double.class) 		return (T) new Double(data);
			else if(c == double.class) 		return (T) new Double(data);
			else if(c == Boolean.class) 	return (T) new Boolean(data);
			else if(c == boolean.class) 	return (T) new Boolean(data);
			else if(c == Byte.class) 		return (T) new Byte(data);
			else if(c == byte.class) 		return (T) new Byte(data);
			else if(byte[].class.isAssignableFrom(c))
											return (T) Base64Decoder.decode(data);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Replaces reserved and unsafe characters in URLs with hex values
	 */
	public static String urlEncode( String str ){
		StringBuilder out = new StringBuilder();
		for( char c : str.toCharArray() ){
			if( c>='0' && c<='9' || c>='A' && c<='Z' || c>='a' && c<='z' ||
					c=='$' || c=='-' || c=='_' || c=='.' || c=='+' || c=='!' || 
					c=='*' || c=='\'' || c=='(' || c==')' || c==',' )
				out.append( c );
			else{
				out.append( '%' ).append( toHexString((byte)c) );
			}				
		}
		
		return out.toString();
	}
	
	/**
	 * Replaces hex values from a URL with the proper characters
	 */
	public static String urlDecode( String str ){
		StringBuilder out = new StringBuilder();
		char[] array = str.toCharArray();
		for( int i=0; i<array.length ;i++ ){
			char c = array[i];
			if( c == '%' && i+2<array.length ){
				out.append( (char)hexToByte( array[++i], array[++i]) );
			}
			else
				out.append( c );
		}
		
		return out.toString();
	}
}
