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

package zutil.db.bean;

import zutil.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * A Class that contains information about a bean
 */
class DBBeanConfig{
    private static final Logger logger = LogUtil.getLogger();
    /** This is a cache of all the initialized beans */
    private static HashMap<String,DBBeanConfig> beanConfigs = new HashMap<String,DBBeanConfig>();


	/** The name of the table in the DB **/
    protected String tableName;
    /** The name of the id column **/
    protected String idColumn;
    /** All the fields in the bean **/
    protected ArrayList<Field> fields;

    protected DBBeanConfig(){
        fields = new ArrayList<Field>();
    }



    /**
     * @return the configuration object for the specified class
     */
    protected static DBBeanConfig getBeanConfig(Class<? extends DBBean> c){
        if( !beanConfigs.containsKey( c.getName() ) )
            initBeanConfig( c );
        return beanConfigs.get( c.getName() );
    }

    /**
     * Caches the fields
     */
    private static void initBeanConfig(Class<? extends DBBean> c){
        //logger.fine("Initiating new BeanConfig( "+c.getName()+" )");
        DBBeanConfig config = new DBBeanConfig();
        // Find the table name
        DBBean.DBTable tableAnn = c.getAnnotation(DBBean.DBTable.class);
        if( tableAnn != null ){
            config.tableName = tableAnn.value();
            config.idColumn  = tableAnn.idColumn();
        }
        else{
            config.tableName = c.getSimpleName();
            config.idColumn  = "id";
        }
        // Add the fields in the bean and all the super classes fields
        for(Class<?> cc = c; cc != DBBean.class ;cc = cc.getSuperclass()){
            Field[] fields = cc.getDeclaredFields();
            for( Field field : fields ){
                int mod = field.getModifiers();
                if( !Modifier.isTransient( mod ) &&
                        !Modifier.isAbstract( mod ) &&
                        !Modifier.isFinal( mod ) &&
                        !Modifier.isStatic( mod ) &&
                        !Modifier.isInterface( mod ) &&
                        !Modifier.isNative( mod ) &&
                        !config.fields.contains( field )){
                    config.fields.add( field );
                }
            }
            if( tableAnn == null || !tableAnn.superBean() )
                break;
        }

        beanConfigs.put(c.getName(), config);
    }

    protected static String getFieldName(Field field){
        String name = null;
        DBBean.DBColumn colAnnotation = field.getAnnotation(DBBean.DBColumn.class);
        if(colAnnotation != null)
            name = colAnnotation.value();
        else
            name = field.getName();
        return name;
    }
}