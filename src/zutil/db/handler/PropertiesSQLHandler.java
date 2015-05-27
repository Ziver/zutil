/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.db.handler;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

import zutil.db.SQLResultHandler;

/**
 * Adds the result of the query to a Properties object,
 * 
 * The handler sets the first column of the result as 
 * the key and the second column as the value
 * 
 * @author Ziver
 */
public class PropertiesSQLHandler implements SQLResultHandler<Properties> {
	
	private Properties prop;
	
	/**
	 * Creates a new Properties object to be filled
	 */
	public PropertiesSQLHandler(){
		this.prop = new Properties();
	}
	
	/**
	 * Adds data to a existing Properties object
	 */
	public PropertiesSQLHandler(Properties p){
		this.prop = p;
	}
	
	
	/**
	 * Is called to handle an result from an query.
	 */
	public Properties handleQueryResult(Statement stmt, ResultSet result) throws SQLException{
		while( result.next() )
			prop.setProperty(result.getString(0), result.getString(1));
		return prop;
	}
}
