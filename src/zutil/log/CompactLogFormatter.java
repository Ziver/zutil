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

package zutil.log;

import zutil.StringUtil;
import zutil.io.StringOutputStream;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

public class CompactLogFormatter extends Formatter{
    /** The split pattern where the **/
    private static final Pattern splitter = Pattern.compile("\n");
    /** the stream should print time stamp **/
    private boolean timeStamp = true;
    /** The time stamp style **/
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    /** If displaying class names are enabled **/
    private boolean className = true;
    /** If displaying method names are enabled **/
    private boolean methodName = false;
    /** Specifies the max length of the longest class name **/
    private int max_class_name = 0;
    /** Cache for the class padding **/
    private static HashMap<String,String> padd_cache = new HashMap<>();
    /** Date temp file **/
    private Date date = new Date();


    @Override
    public String format(LogRecord record) {
        StringBuilder prefix = new StringBuilder();

        if (timeStamp) {
            date.setTime(record.getMillis());
            prefix.append(dateFormatter.format(date));
            prefix.append(' ');
        }

        switch(record.getLevel().intValue()) {
        case /* SEVERE  */	1000: prefix.append("[SEVERE] "); break;
        case /* WARNING */	900 : prefix.append("[WARNING]"); break;
        case /* INFO    */	800 : prefix.append("[INFO]   "); break;
        case /* CONFIG  */	700 : prefix.append("[CONFIG] "); break;
        case /* FINE    */	500 : prefix.append("[FINE]   "); break;
        case /* FINER   */	400 : prefix.append("[FINER]  "); break;
        case /* FINEST  */	300 : prefix.append("[FINEST] "); break;
        }
        prefix.append(' ');

        if (className) {
            prefix.append(paddClassName(record.getSourceClassName()));
        }
        if (methodName) {
            prefix.append(record.getSourceMethodName());
        }
        prefix.append(": ");

        StringBuilder ret = new StringBuilder();
        if (record.getMessage() != null) {
            String[] array = splitter.split(record.getMessage());
            for (int i=0; i<array.length; ++i) {
                if (i != 0)
                    ret.append('\n');
                if (prefix.length() > 0)
                    ret.append(prefix);
                ret.append(array[i]);
            }
            ret.append('\n');
        }

        if (record.getThrown() != null) {
            StringOutputStream out = new StringOutputStream();
            record.getThrown().printStackTrace(new PrintStream(out));
            String[] array = splitter.split(out.toString());
            for (int i=0; i<array.length; ++i) {
                if (i != 0)
                    ret.append('\n');
                if (prefix.length() > 0)
                    ret.append(prefix);
                ret.append(array[i]);
            }
            ret.append('\n');
        }
        return ret.toString();
    }

    /**
     * If the formatter should add a time stamp in front of the log message
     *
     * @param enable set to True to activate time stamp
     */
    public void enableTimeStamp(boolean enable) {
        timeStamp = enable;
    }

    /**
     * The DateFormat to print in the time stamp
     *
     * @param ts is the String to send to SimpleDateFormat
     */
    public void setTimeStamp(String ts) {
        dateFormatter = new SimpleDateFormat(ts);
    }

    /**
     * If the formatter should add the class/source name in front of the log message
     *
     * @param enable set to True to activate class/source name
     */
    public void enableClassName(boolean enable) {
        className = enable;
    }

    /**
     * If the formatter should add the class/source name in front of the log message
     *
     * @param enable set to True to activate class/source name
     */
    public void enableMethodName(boolean enable) {
        methodName = enable;
    }

    /**
     * @return the Class name
     */
    private String paddClassName(String source) {
        String cStr = padd_cache.get(source);
        if (cStr == null || cStr.length() != max_class_name) {
            cStr = source.substring(source.lastIndexOf('.') + 1); // Remove packages
            if (cStr.lastIndexOf('$') >= 0) { // extract subclass name
                String subClass = cStr.substring(cStr.lastIndexOf('$') + 1);
                if (!Pattern.matches("\\d+", subClass)) // Don'n substring for anonymous classes
                    cStr = subClass;
            }

            if (cStr.length() > max_class_name)
                max_class_name = cStr.length();

            cStr += StringUtil.getSpaces(max_class_name - cStr.length());
            padd_cache.put(source, cStr);
        }
        return cStr;
    }

}
