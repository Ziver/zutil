package zutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;

public class Converter {

	/** 
	 * Converts an object to an array of bytes.
	 * 
	 * @param object the object to convert.
	 * @return the associated byte array.
	 */
	public static byte[] toBytes(Object object){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
		return baos.toByteArray();
	}

	/** 
	 * Converts an array of bytes back to its constituent object. The 
	 * input array is assumed to have been created from the original object.
	 * 
	 * @param bytes the byte array to convert.
	 * @return the associated object.
	 */
	public static Object toObject(byte[] bytes)	{
		Object object = null;
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois= new ObjectInputStream(bais);
			object = ois.readObject();
			ois.close();
			bais.close();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}catch(ClassNotFoundException cnfe){
			System.out.println(cnfe.getMessage());
		}
		return object;
	}

	/**
	 * Checks if the given interface is implemented in the object
	 * 
	 * @param object the object to look for the interface
	 * @param interf the interface to look for
	 * @return true if the interface is implemented else false
	 */
	@SuppressWarnings("unchecked")
	public static boolean isInstanceOf(Object object, Class interf){
		Class[] objectInterf = object.getClass().getInterfaces();
		for(int i=0; i<objectInterf.length ;i++){
			if(objectInterf[i] == interf){
				return true;
			}
		}
		return false;
	}

	// array neaded for byteToHex
	private static char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	/**
	 * Converts a byte Array to a Hex String
	 * 
	 * @param raw the byte arrat to convert
	 * @return a Hex String
	 */
	public static String toHexString(byte[] raw){
		StringBuffer ret = new StringBuffer();

		for(byte b : raw){
			ret.append(HEX_CHARS[(int) b & 0x0F ]);
			ret.append(HEX_CHARS[(int) (b >>> 0x04)& 0x0F ]);
		}

		return ret.toString();
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
}
