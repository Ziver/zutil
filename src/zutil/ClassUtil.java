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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

/**
 * This class include some utility functions for classes
 *
 * User: Ziver
 */
public class ClassUtil {
    /** A Set that contains possible wrapper objects for primitives **/
    private static final HashSet<Class<?>> wrappers;
    static {
        wrappers = new HashSet<Class<?>>();
        wrappers.add(Boolean.class);
        wrappers.add(Character.class);
        wrappers.add(Byte.class);
        wrappers.add(Short.class);
        wrappers.add(Integer.class);
        wrappers.add(Long.class);
        wrappers.add(Float.class);
        wrappers.add(Double.class);
        wrappers.add(Void.class);
    }

    /** A Set that contains possible primitives **/
    private static final HashSet<Class<?>> primitives;
    static {
        primitives = new HashSet<Class<?>>();
        primitives.add(boolean.class);
        primitives.add(char.class);
        primitives.add(byte.class);
        primitives.add(short.class);
        primitives.add(int.class);
        primitives.add(long.class);
        primitives.add(float.class);
        primitives.add(double.class);
        primitives.add(void.class);
        primitives.add(String.class);
    }


    /**
     * @return if the given class is a wrapper for a primitive
     */
    public static boolean isWrapper(Class<?> type){
        return wrappers.contains( type );
    }

    /**
     * @return if the given class is a primitive including String
     */
    public static boolean isPrimitive(Class<?> type){
        return primitives.contains( type );
    }


    /**
     * @param   field       is the field to return the generics from
     * @return the generics assigned to a specific field or a empty list
     */
    public static Class<?>[] getGenericClasses(Field field){
        return getGenericClasses(field.getGenericType());
    }
    /**
     * Traverses the class hierarchy and searches for the given super class or interface
     * and returns the assigned generic types.
     *
     * @param   c               the source class
     * @param   superClass      is the super class to search for
     * @return the generics for a specific super class or a empty list if
     *         there is no generics or the super class is not found
     */
    public static Class<?>[] getGenericClasses(Class<?> c, Class<?> superClass){
        if(superClass != null) {
            // Search for the super class
            while (c.getSuperclass() != null && c.getSuperclass() != Object.class) {
                // Did we find the super class?
                if (superClass.isAssignableFrom(c.getSuperclass()))
                    return getGenericClasses(c.getGenericSuperclass());
                c = c.getSuperclass();
            }
        }
        return new Class[0];
    }
    private static Class<?>[] getGenericClasses(Type genericType){
        if(genericType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericType;
            Type[] argTypes = aType.getActualTypeArguments();
            Class<?>[] classArray = new Class<?>[argTypes.length];
            for(int i=0; i<classArray.length; ++i) {
                if(argTypes[i] instanceof Class)
                    classArray[i] = (Class<?>) argTypes[i];
            }
            return classArray;
        }
        return new Class[0];
    }
}
