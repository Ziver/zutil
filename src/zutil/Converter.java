package zutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	 * @param object The object to look for the interface
	 * @param interf The interface to look for
	 * @return True if the interface is implemented else false
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
}
