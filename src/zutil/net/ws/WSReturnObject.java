/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.ws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * This class is used as an return Object for a web service.
 * If a class implements this interface then it implies that multiple
 * parameters can be returned through the WSInterface. And the
 * implementing class will be transparent to the requester. Example:
 *
 * <pre>
 * 	private static class TestObject implements WSReturnObject{
 *		&#64;WSParamName("name")
 *		public String name;
 *		&#64;WSParamName("lastname")
 *	    &#64;WSDocumentation("The users last name")
 *		public String lastname;
 *
 *		public TestObject(String n, String l){
 *			name = n;
 *			lastname = l;
 *		}
 *	}
 * </pre>
 *
 * @author Ziver
 *
 */
public abstract class WSReturnObject{

    public Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException{
        return field.get(this);
    }
}
