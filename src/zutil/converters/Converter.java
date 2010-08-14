package zutil.converters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

import zutil.io.DynamicByteArrayStream;

public class Converter {
	private Converter(){}

	/** 
	 * Converts an object to an array of bytes.
	 * 
	 * @param object the object to convert.
	 * @return the associated byte array.
	 * @throws IOException 
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
	 * @param num is the number to convert
	 * @return an byte array of four bytes
	 */
	public static byte[] toBytes(int num){
		return new byte[]{ 
				(byte)(num & 0xff), 
				(byte)((num >> 8)& 0xff), 
				(byte)((num >> 16)& 0xff), 
				(byte)((num >> 24)& 0xff)};
	}
	
	/**
	 * Converts a Integer to an byte
	 * 
	 * @param num is the number to convert
	 * @return an byte
	 */
	public static byte toByte(int num){
		return (byte)(num & 0xff);
	}

	/** 
	 * Converts an array of bytes back to its constituent object. The 
	 * input array is assumed to have been created from the original object.
	 * 
	 * @param bytes the byte array to convert.
	 * @return the associated object.
	 * @throws Exception 
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
	 * @param bytes the byte array to convert.
	 * @return the associated object.
	 * @throws Exception 
	 */
	public static Object toObject(DynamicByteArrayStream bytes) throws Exception{
		Object object = null;

		ObjectInputStream ois = new ObjectInputStream(bytes);
		object = ois.readObject();
		ois.close();

		return object;
	}

	/**
	 * Checks if the given interface is implemented in the object
	 * 
	 * @param object the object to look for the interface
	 * @param interf the interface to look for
	 * @return true if the interface is implemented else false
	 */
	public static boolean isInstanceOf(Object object, Class<?> interf){
		Class<?>[] objectInterf = object.getClass().getInterfaces();
		for(int i=0; i<objectInterf.length ;i++){
			if(objectInterf[i] == interf){
				return true;
			}
		}
		return false;
	}

	/** array needed for byteToHex */
	private static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	/**
	 * Converts a byte Array to a Hex String
	 * 
	 * @param raw the byte array to convert
	 * @return a Hex String
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
	 * @param raw the byte array to convert
	 * @return a Hex String
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
	 * @param raw the byte to convert
	 * @return a Hex String
	 */
	public static String toHexString(byte raw){
		String ret = ""+HEX_CHARS[(int) (raw >>> 0x04)& 0x0F ];
		ret += ""+HEX_CHARS[(int) raw & 0x0F ];

		return ret;
	}

	/**
	 * Converts the given byte to a String with 1's and 0's
	 * 
	 * @param raw the byte to convert
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
	 * @param raw the byte array to convert
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
	 * Converts a BitSet to a Integer
	 * 
	 * @param bits the BitSet to convert
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
	 * @param bits the boolean array to convert
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
	 * Converts a Integer to a BitSet
	 * 
	 * @param i the Integer to convert
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
	 * @param <T> is the resulting class
	 * @param data is the String data to be converted
	 * @param c is the class to convert to
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
											return (T) new sun.misc.BASE64Decoder().decodeBuffer(data);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}