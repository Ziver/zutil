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

import zutil.ClassUtil;
import zutil.io.file.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.*;

/**
 * Utility functions for the standard Java Logger
 * 
 * @author Ziver
 */
public class LogUtil {
    private static final Logger logger = Logger.getLogger( LogUtil.class.getName() );


    private LogUtil(){}

    /**
     * @return a new Logger for the calling class
     */
    public static Logger getLogger(){
        return Logger.getLogger(ClassUtil.getCallingClass(LogUtil.class));
    }

    /**
     * Sets the log formatter to all root Handlers
     *
     * @param 		f 		is the formatter class
     */
    public static void setGlobalFormatter(Formatter f){
        Logger root = Logger.getLogger("");
        for (Handler handler : root.getHandlers()) {
            handler.setFormatter(f);
        }
    }

    /**
     * Adds the log formatter to all handlers in the namespace
     *
     * @param 		f 		is the formatter class
     */
    public static void setFormatter(String name, Formatter f){
        Logger root = Logger.getLogger(name);
        for (Handler handler : root.getHandlers()) {
            handler.setFormatter(f);
        }
    }

    /**
     * Sets the global log level
     */
    public static void setGlobalLevel(Level level){
        setLevel("", level);
    }

    /**
     * Adds a Handler to the root namespace
     */
    public static void addGlobalHandler(Handler handler){
        Logger root = Logger.getLogger("");
        root.addHandler(handler);
    }

    /**
     * Sets the log level for a specified class
     */
    public static void setLevel(Class<?> c, Level level){
        setLevel(c.getName(), level);
    }

    /**
     * Sets the log level for a specified logger
     */
    public static void setLevel(String name, Level level){
        logger.fine("Changing log level of \""+name+"\" to \""+level.getLocalizedName()+"\"");
        Logger newLogger = Logger.getLogger(name);
        newLogger.setLevel(level);
        // Check if the logger has a handler
        if( newLogger.getHandlers().length > 0 ){
            // Set the level on the handlers if its level is higher
            for (Handler handler : newLogger.getHandlers()) {
                if(handler.getLevel().intValue() > level.intValue())
                    handler.setLevel(level);
            }
        }
    }

    public static boolean isLoggable(Class clazz, Level level){
        return Logger.getLogger(clazz.getName()).isLoggable(level);
    }

    public static void readConfiguration(String file){
        try{
            File confFile = FileUtil.find(file);
            if (confFile != null) {
                FileInputStream in = new FileInputStream(confFile);
                LogManager.getLogManager().readConfiguration(in);
                in.close();
            }
            else
                logger.warning("Unable to find logging configuration file: "+file);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Unable to load logging configuration: "+file, e);
        }
    }
}
