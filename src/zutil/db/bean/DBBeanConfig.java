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

import zutil.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Class that contains information about a bean
 */
class DBBeanConfig{
    /** This is a cache of all the initialized beans */
    private static HashMap<String,DBBeanConfig> beanConfigs = new HashMap<>();


    /** The name of the table in the DB **/
    private String tableName;
    /** The name of the id column **/
    private String idColumnName;
    /** All normal fields in the bean **/
    private ArrayList<DBBeanFieldConfig> fields = new ArrayList<>();
    /** All sub bean fields in the bean **/
    private ArrayList<DBBeanSubBeanConfig> subBeanFields = new ArrayList<>();


    private DBBeanConfig(){ }



    /**
     * @return the configuration object for the specified class
     */
    public static DBBeanConfig getBeanConfig(Class<? extends DBBean> c){
        if( !beanConfigs.containsKey( c.getName() ) )
            initBeanConfig( c );
        return beanConfigs.get( c.getName() );
    }

    /**
     * Caches the fields
     */
    private static void initBeanConfig(Class<? extends DBBean> c){
        DBBeanConfig config = new DBBeanConfig();
        // Find the table name
        DBBean.DBTable tableAnn = c.getAnnotation(DBBean.DBTable.class);
        if( tableAnn != null ){
            config.tableName = tableAnn.value();
            config.idColumnName = tableAnn.idColumn();
        } else {
            config.tableName = c.getSimpleName();
            config.idColumnName = "id";
        }

        // Add the fields in the bean and all the super classes fields
        for(Class<?> cc = c; cc != DBBean.class ;cc = cc.getSuperclass()){
            Field[] fields = cc.getDeclaredFields();
            for( Field field : fields ){
                int mod = field.getModifiers();
                if( !Modifier.isTransient( mod ) &&
                        !Modifier.isFinal( mod ) &&
                        !Modifier.isStatic( mod ) &&
                        !config.fields.contains( field )){
                    if (List.class.isAssignableFrom(field.getType()) &&
                            field.getAnnotation(DBBean.DBLinkTable.class) != null)
                        config.subBeanFields.add(new DBBeanSubBeanConfig(field));
                    else
                        config.fields.add(new DBBeanFieldConfig(field));
                }
            }
            if( tableAnn == null || !tableAnn.superBean() )
                break;
        }

        beanConfigs.put(c.getName(), config);
    }


    public String getTableName(){
        return tableName;
    }

    public String getIdColumnName(){
        return idColumnName;
    }

    public List<DBBeanFieldConfig> getFields(){
        return fields;
    }

    public List<DBBeanSubBeanConfig> getSubBeans(){
        return subBeanFields;
    }


    public static class DBBeanFieldConfig {
        private Field field;
        private String fieldName;

        private DBBeanFieldConfig(Field field){
            this.field = field;
            if( !Modifier.isPublic( field.getModifiers()))
                field.setAccessible(true);

            DBBean.DBColumn colAnnotation = field.getAnnotation(DBBean.DBColumn.class);
            if(colAnnotation != null)
                fieldName = colAnnotation.value();
            else
                fieldName = field.getName();
        }


        public String getName(){
            return fieldName;
        }

        public Class<?> getType(){
            return field.getType();
        }

        public Object getValue(Object obj) {
            try {
                return field.get(obj);
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public void setValue(Object obj, Object fieldValue) {
            try {
                if (!Modifier.isPublic(field.getModifiers()))
                    field.setAccessible(true);

                // Set basic data type
                if (fieldValue == null && ClassUtil.isPrimitive(field.getType())) {
                    if (field.getType() == Integer.TYPE) field.setInt(obj, 0);
                    else if (field.getType() == Character.TYPE) field.setChar(obj, (char) 0);
                    else if (field.getType() == Byte.TYPE) field.setByte(obj, (byte) 0);
                    else if (field.getType() == Short.TYPE) field.setShort(obj, (short) 0);
                    else if (field.getType() == Long.TYPE) field.setLong(obj, 0L);
                    else if (field.getType() == Float.TYPE) field.setFloat(obj, 0f);
                    else if (field.getType() == Double.TYPE) field.setDouble(obj, 0d);
                    else if (field.getType() == Boolean.TYPE) field.setBoolean(obj, false);
                } else {
                    // Some special cases
                    if (field.getType() == Boolean.TYPE && fieldValue instanceof Integer)
                        field.setBoolean(obj, ((Integer) fieldValue) > 0); // Convert an Integer to boolean
                    else
                        field.set(obj, fieldValue);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public static class DBBeanSubBeanConfig extends DBBeanFieldConfig{
        private String linkTableName;
        private Class<? extends DBBean> subBeanClass;
        private DBBeanConfig subBeanConfig;
        private String parentIdCol;

        private DBBeanSubBeanConfig(Field field){
            super(field);

            DBBean.DBLinkTable linkAnnotation = field.getAnnotation(DBBean.DBLinkTable.class);
            this.linkTableName = linkAnnotation.table();
            this.subBeanClass = linkAnnotation.beanClass();
            this.subBeanConfig = DBBeanConfig.getBeanConfig(subBeanClass);
            this.parentIdCol = linkAnnotation.idColumn();
        }


        public String getLinkTableName() {
            return linkTableName;
        }

        public boolean isStandaloneLinkTable(){
            return !linkTableName.equals(subBeanConfig.tableName);
        }

        public Class<? extends DBBean> getSubBeanClass() {
            return subBeanClass;
        }

        public DBBeanConfig getSubBeanConfig() {
            return subBeanConfig;
        }

        public String getParentIdColumnName() {
            return parentIdCol;
        }
    }
}