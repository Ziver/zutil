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

package zutil.ui;

import zutil.log.LogUtil;
import zutil.parser.DataNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ziver
 */
public class Configurator<T> {
    private static final Logger logger = LogUtil.getLogger();

    /**
     * Sets a field in a class as externally configurable.
     */
    @Retention(RetentionPolicy.RUNTIME) // Make this annotation accessible at runtime via reflection.
    @Target({ElementType.FIELD})        // This annotation can only be applied to class fields.
    public static @interface Configurable{
        /** Nice name of this parameter **/
        String value();
        /** Defines the order the parameters, in ascending order **/
        int order() default Integer.MAX_VALUE;
    }


    public static enum ConfigType{
        STRING, INT, BOOLEAN
    }


    private static HashMap<Class, ConfigurationParam[]> classConf = new HashMap<>();

    private T obj;
    private ConfigurationParam[] params;

    public Configurator(T obj){
        this.obj = obj;
        this.params = getConfiguration(obj.getClass(), obj);
    }

    public T getObject(){
        return obj;
    }

    public ConfigurationParam[] getConfiguration(){
        return params;
    }

    public static ConfigurationParam[] getConfiguration(Class c){
        if(!classConf.containsKey(c))
            classConf.put(c, getConfiguration(c, null));
        return classConf.get(c);
    }
    protected static ConfigurationParam[] getConfiguration(Class c, Object obj){
        ArrayList<ConfigurationParam> conf = new ArrayList<ConfigurationParam>();

        Field[] all = c.getDeclaredFields();
        for(Field f : all){
            if(f.isAnnotationPresent(Configurable.class) &&
                    !Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
                try {
                    conf.add(new ConfigurationParam(f, obj));
                } catch (IllegalAccessException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        }

        ConfigurationParam[] list = conf.toArray(new ConfigurationParam[conf.size()]);
        Arrays.sort(list);
        return list;
    }

    /**
     * Uses a Map to assign all parameters of the Object
     */
    public Configurator<T> setValues(Map<String,String> parameters){
        for(ConfigurationParam param : this.params){
            if(parameters.containsKey(param.getName()))
                param.setValue(parameters.get(param.getName()));
        }
        return this;
    }

    /**
     * Uses a Map to assign all parameters of the Object.
     * NOTE: the DataNode must be of type Map
     */
    public Configurator<T> setValues(DataNode node){
        if(!node.isMap())
            return this;
        for(ConfigurationParam param : this.params){
            if(node.get(param.getName()) != null)
                param.setValue(node.getString(param.getName()));
        }
        return this;
    }

    public DataNode getValuesAsNode(){
        DataNode node = new DataNode(DataNode.DataType.Map);
        for(ConfigurationParam param : this.params){
            node.set(param.getName(), param.getString());
        }
        return node;
    }


    /**
     * All configuration parameters that was set
     * for each parameter will be applied to the object.
     *
     * The postConfigurationAction() method will be called on the target object if it implements the ConfigurationActionListener interface.
     */
    public void applyConfiguration(){
        StringBuilder strParams = new StringBuilder();
        for(ConfigurationParam param : params){
            try {
                param.apply(obj);
                // Logging
                if(logger.isLoggable(Level.FINE)) {
                    strParams.append(param.getName());
                    if(param.isTypeString())
                        strParams.append(": '").append(param.getString()).append("', ");
                    else
                        strParams.append(": ").append(param.getString());
                }
            } catch (IllegalAccessException e) {
                logger.log(Level.WARNING, null, e);
            }
            if(obj instanceof ConfigurationActionListener)
                ((ConfigurationActionListener) obj).postConfigurationAction();
        }
        if(logger.isLoggable(Level.FINE))
            logger.fine("Configured object: " + obj.getClass().getName() + " ("+ strParams +")");
    }



    public interface ConfigurationActionListener{
        void postConfigurationAction();
    }


    public static class ConfigurationParam implements Comparable<ConfigurationParam>{
        protected Field field;
        protected String name;
        protected String niceName;
        protected ConfigType type;
        protected Object value;
        protected int order;


        protected ConfigurationParam(Field f, Object obj) throws IllegalAccessException {
            field = f;
            field.setAccessible(true);
            name = field.getName();
            if(obj != null)
                value = field.get(obj);
            if(field.isAnnotationPresent(Configurable.class)) {
                niceName = field.getAnnotation(Configurable.class).value();
                order = field.getAnnotation(Configurable.class).order();
            }
            else{
                niceName = name;
                order = Integer.MAX_VALUE;
            }

            if     (f.getType() == String.class) type = ConfigType.STRING;
            else if(f.getType() == int.class)    type = ConfigType.INT;
            else if(f.getType() == boolean.class)type = ConfigType.BOOLEAN;

        }

        public String getName(){       return name;}
        public String getNiceName(){   return niceName;}
        public ConfigType getType(){   return type;}
        public boolean isTypeString(){ return type == ConfigType.STRING;}
        public boolean isTypeInt(){    return type == ConfigType.INT;}
        public boolean isTypeBoolean(){return type == ConfigType.BOOLEAN;}

        public String getString(){
            if(value == null)
                return null;
            return value.toString();
        }
        public boolean getBoolean(){
            if(value == null || type != ConfigType.BOOLEAN)
                return false;
            return (boolean)value;
        }

        /**
         * This method will set a value for the represented field,
         * to apply the change to the source object the method
         * {@link #applyConfiguration()} needs to be called
         */
        public void setValue(String v){
            switch(type){
                case STRING:
                    value = v; break;
                case INT:
                    value = Integer.parseInt(v); break;
                case BOOLEAN:
                    value = Boolean.parseBoolean(v); break;
            }
        }

        protected void apply(Object obj) throws IllegalAccessException {
            if(obj != null)
                field.set(obj, value);
        }


        @Override
        public int compareTo(ConfigurationParam configurationParam) {
            return this.order - configurationParam.order;
        }
    }
}
