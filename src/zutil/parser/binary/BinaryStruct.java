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

package zutil.parser.binary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A interface that indicate that the implementing class can
 * be serialized into a linear binary stream.
 *
 * Created by Ziver on 2016-01-28.
 */
public interface BinaryStruct {

    /**
     * Basic BinaryField with a constant length.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface BinaryField{
        /** @return a number indicating the order the fields are read. Lowest index number field will be read first. */
        int index();
        /** @return the bit length of the data */
        int length();
    }

    /**
     * Can be used for fields that are of variable length. This interface
     * is only applicable for reading from a stream.
     * TODO: Length must be manually set when writing.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface VariableLengthBinaryField{
        /** @return a number indicating the order the fields are read. Lowest index number field will be read first. */
        int index();
        /** @return a String name of the field that contains the length of the data to be read. */
        String lengthField();
        /** @return the multiplier used on the lengthField parameter to convert the length in bits to
         *  a user defined value. Default value is 8 (which converts length to number of bytes). */
        int multiplier() default 8;
    }

    /**
     * Can be used with fields that need a custom serializer.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface CustomBinaryField{
        /** @return a number indicating the order the fields are read. Lowest index number field will be read first. */
        int index();
        /** @return the serializer class name that will be used. Class needs to be publicly visible. */
        Class<? extends BinaryFieldSerializer> serializer();
    }
}
