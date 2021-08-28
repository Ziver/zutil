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

package zutil.ui.conf;

import zutil.log.LogUtil;
import zutil.parser.DataNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is a helper class that lets can configure fields inside of a object.
 * The target class should implement the {@link Configurable} annotation on all the
 * fields that should be configurable. And then the gui can use the {@link Configurator#getConfiguration()}
 * to display all the correct fields. To later save the user input back to the target
 * object the {@link Configurator#setValues(DataNode)} or {@link Configurator#setValues(Map)}
 * can be used to set the individual fields and finally call {@link Configurator#applyConfiguration()}
 * to configure the target object.
 * <p>
 * External listener can be registered to be called before or after configuration changes
 * by implementing {@link PreConfigurationActionListener} or {@link PostConfigurationActionListener}.
 * The configured object will automatically be registered as a listener if it also implements
 * these interfaces.
 *
 * <p>
 * Supported field types: String, int, boolean, enum
 * <p>
 * Created by Ziver
 */
public class Configurator<T> {
    private static final Logger logger = LogUtil.getLogger();

    // ----------------------------------------------------
    // Public interfaces
    // ----------------------------------------------------

    /**
     * Sets a field in a class as externally configurable.
     */
    @Retention(RetentionPolicy.RUNTIME) // Make this annotation accessible at runtime via reflection.
    @Target({ElementType.FIELD})        // This annotation can only be applied to class fields.
    public @interface Configurable {
        /** Nice name of this parameter **/
        String value();
        /** A longer human friendly description of the parameter **/
        String description() default "";
        /** Defines the order the parameter, parameter with lowest order value will be shown first **/
        int order() default Integer.MAX_VALUE;
        /** Provide a custom set of values through a value provider that will be the choice the user will have. **/
        Class<? extends ConfigValueProvider> valueProvider() default DummyValueProvider.class;
    }

    /**
     * Interface for providing a specific selection of values that a user is allowed to pick from for assignment to an object field.
     * The instantiation of the value provider will be done and cashed for each class type.
     * The class implementing this interface is required to have one of the below constructors for instantiation:
     * <pre>
     *     ** no constructors **
     *     public XXX() {}
     *     public XXX(Class fieldType) {}
     *     public XXX(Class fieldType, Object fieldValue) {}
     * </pre>
     *
     * @param <V> represents the value type that will be assigned to the target field.
     */
    public interface ConfigValueProvider<V> {
        /**
         * @return a String representing the given obj.
         */
        String getValue(V obj);

        /**
         * @return a array of all possible values that the user can select from.
         */
        List<String> getPossibleValues();

        /**
         * Convert the user selected value into the actual Object that should be assigned to the target field.
         *
         * @param value the string value that was selected by the user.
         * @return a Object that will be assigned to the target field, note a exception will be thrown if the return type does not match the field.
         */
        V getObject(String value);
    }

    /**
     * Defines supported configurable types
     */
    public enum ConfigType {
        STRING, NUMBER, BOOLEAN, SELECTION
    }


    // ----------------------------------------------------
    // Configurator data and logic
    // ----------------------------------------------------

    private static HashMap<Class, ConfigurationParam[]> classConf = new HashMap<>();

    private T obj;
    private ConfigurationParam[] params;
    private PreConfigurationActionListener<T> preListener;
    private PostConfigurationActionListener<T> postListener;


    public Configurator(T obj) {
        this.obj = obj;
        this.params = getConfiguration(obj.getClass(), obj);
    }


    public T getObject() {
        return obj;
    }

    public ConfigurationParam[] getConfiguration() {
        return params;
    }

    public static ConfigurationParam[] getConfiguration(Class c) {
        if (!classConf.containsKey(c))
            classConf.put(c, getConfiguration(c, null));
        return classConf.get(c);
    }
    protected static ConfigurationParam[] getConfiguration(Class c, Object obj) {
        ArrayList<ConfigurationParam> conf = new ArrayList<>();

        for (Class<?> cc = c; cc != Object.class; cc = cc.getSuperclass()) { // iterate through all super classes
            for (Field f : cc.getDeclaredFields()) {
                if (f.isAnnotationPresent(Configurable.class)) {
                    try {
                        conf.add(new ConfigurationParam(f, obj));
                    } catch (ReflectiveOperationException e) {
                        logger.log(Level.SEVERE, null, e);
                    }
                }
            }
        }

        ConfigurationParam[] list = conf.toArray(new ConfigurationParam[0]);
        Arrays.sort(list);
        return list;
    }

    /**
     * Uses a Map to assign all parameters of the Object
     *
     * @return a reference to itself so that method calls can be chained.
     */
    public Configurator<T> setValues(Map<String,String> parameters) {
        for (ConfigurationParam param : this.params) {
            if (parameters.containsKey(param.getName()))
                param.setValue(parameters.get(param.getName()));
        }
        return this;
    }

    /**
     * Uses a Map to assign all parameters of the Object.
     * NOTE: the DataNode must be of type Map
     *
     * @return a reference to itself so that method calls can be chained.
     */
    public Configurator<T> setValues(DataNode node) {
        if (!node.isMap())
            return this;

        for (ConfigurationParam param : this.params) {
            if (node.get(param.getName()) != null)
                param.setValue(node.getString(param.getName()));
        }
        return this;
    }

    public DataNode getValuesAsNode() {
        DataNode node = new DataNode(DataNode.DataType.Map);
        for (ConfigurationParam param : this.params) {
            node.set(param.getName(), param.getString());
        }
        return node;
    }


    /**
     * Set a listener that will be called just before the configuration has been applied
     */
    public void setPreConfigurationListener(PreConfigurationActionListener<T> listener) {
        preListener = listener;
    }

    /**
     * Set a listener that will be called after the configuration has been applied
     */
    public void setPostConfigurationListener(PostConfigurationActionListener<T> listener) {
        postListener = listener;
    }

    /**
     * All configuration parameters that was set
     * for each parameter will be applied to the object.
     *
     * The preConfigurationAction() method will be called before the target object has
     * been configured if it implements the PreConfigurationActionListener interface.
     * The postConfigurationAction() method will be called after the target object is
     * configured if it implements the PostConfigurationActionListener interface.
     */
    public void applyConfiguration() {
        if (preListener != null)
            preListener.preConfigurationAction(this, obj);
        if (obj instanceof PreConfigurationActionListener)
            ((PreConfigurationActionListener<T>) obj).preConfigurationAction(this, obj);

        StringBuilder strParams = new StringBuilder();
        for (ConfigurationParam param : params) {
            try {
                param.apply(obj);

                // Logging
                if (logger.isLoggable(Level.FINE)) {
                    strParams.append(param.getName()).append(": ");
                    if (param.isTypeString())
                        strParams.append("'").append(param.getString()).append("'");
                    else
                        strParams.append(param.getString());
                    strParams.append(", ");
                }
            } catch (IllegalAccessException e) {
                logger.log(Level.WARNING, null, e);
            }
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("Configured object: " + obj.getClass().getName() + " (" + strParams + ")");

        if (obj instanceof PostConfigurationActionListener)
            ((PostConfigurationActionListener<T>) obj).postConfigurationAction(this, obj);
        if (postListener != null)
            postListener.postConfigurationAction(this, obj);
    }


    public interface PreConfigurationActionListener<T> {
        void preConfigurationAction(Configurator<T> configurator, T obj);
    }
    public interface PostConfigurationActionListener<T> {
        void postConfigurationAction(Configurator<T> configurator, T obj);
    }


    public static class ConfigurationParam implements Comparable<ConfigurationParam> {
        protected String niceName;
        protected String description;
        protected int order;

        protected Field field;
        protected String name;
        protected ConfigType type;
        protected Object value;
        protected ConfigValueProvider valueProvider;


        protected ConfigurationParam(Field f, Object obj) throws ReflectiveOperationException {
            field = f;
            field.setAccessible(true);
            name = field.getName();

            if (obj != null)
                value = field.get(obj);

            if (field.isAnnotationPresent(Configurable.class)) {
                niceName = field.getAnnotation(Configurable.class).value();
                description = field.getAnnotation(Configurable.class).description();
                order = field.getAnnotation(Configurable.class).order();
                valueProvider = getValueProviderInstance(field.getAnnotation(Configurable.class).valueProvider());
            } else {
                niceName = name;
                order = Integer.MAX_VALUE;
            }

            if (valueProvider != null)             type = ConfigType.SELECTION;
            else if (f.getType() == String.class)  type = ConfigType.STRING;
            else if (f.getType() == int.class)     type = ConfigType.NUMBER;
            else if (f.getType() == double.class)  type = ConfigType.NUMBER;
            else if (f.getType() == boolean.class) type = ConfigType.BOOLEAN;
            else if (f.getType().isEnum()) {
                type = ConfigType.SELECTION;
                valueProvider = new ConfigEnumValueProvider((Class<Enum>) f.getType());
            } else {
                throw new IllegalArgumentException(f.getType() + " is not a supported native configurable type, a value provided is required for arbitrary objects.");
            }
        }

        private ConfigValueProvider getValueProviderInstance(Class<? extends ConfigValueProvider> valueProviderClass) throws ReflectiveOperationException {
            if (DummyValueProvider.class.equals(valueProviderClass))
                return null;

            for (Constructor constructor : valueProviderClass.getConstructors()) {
                switch (constructor.getParameterCount()) {
                    case 0: return valueProviderClass.getDeclaredConstructor().newInstance();
                    case 1: return valueProviderClass.getDeclaredConstructor(Class.class).newInstance(field.getType());
                    case 2: return valueProviderClass.getDeclaredConstructor(Class.class, Object.class).newInstance(field.getType(), value);
                }
            }

            throw new NoSuchMethodException("Unable to find proper constructor inside " + valueProviderClass.getSimpleName());
        }

        public String getName()          { return name; }
        public String getNiceName()      { return niceName; }
        public String getDescription()   { return description; }
        public ConfigType getType()      { return type; }
        public boolean isTypeString()    { return type == ConfigType.STRING; }
        public boolean isTypeNumber()    { return type == ConfigType.NUMBER; }
        public boolean isTypeBoolean()   { return type == ConfigType.BOOLEAN; }
        public boolean isTypeSelection() { return type == ConfigType.SELECTION; }

        public String getString() {
            if (valueProvider != null)
                return valueProvider.getValue(value);
            if (value == null)
                return null;
            return value.toString();
        }
        public boolean getBoolean() {
            if (value == null || type != ConfigType.BOOLEAN)
                return false;
            return (boolean) value;
        }

        /**
         * @return a String array with all possible values that can be assigned or an empty array if any value within the type definition can be set.
         */
        public List<String> getPossibleValues() {
            if (valueProvider != null) {
                return valueProvider.getPossibleValues();
            }
            return Collections.EMPTY_LIST;
        }

        /**
         * This method will set a value for the represented field,
         * to apply the change to the source object the method
         * {@link #applyConfiguration()} needs to be called
         *
         * @param selectedValue the value that was selected by user.
         */
        public void setValue(String selectedValue) {
            switch(type) {
                case STRING:
                    value = selectedValue; break;
                case NUMBER:
                    if (field.getType() == double.class)
                        value = Double.parseDouble(selectedValue);
                    else
                        value = Integer.parseInt(selectedValue);
                    break;
                case BOOLEAN:
                    value = Boolean.parseBoolean(selectedValue); break;
                case SELECTION:
                    value = valueProvider.getObject(selectedValue); break;
            }
        }

        protected void apply(Object obj) throws IllegalAccessException {
            if (obj != null)
                field.set(obj, value);
        }


        @Override
        public int compareTo(ConfigurationParam configurationParam) {
            return this.order - configurationParam.order;
        }
    }

    /**
     * This is a default class that only indicates that no ValueProvider has been given in the {@link Configurable} annotation.
     */
    private static class DummyValueProvider implements ConfigValueProvider {
        @Override
        public String getValue(Object obj) {return null;}
        @Override
        public List<String> getPossibleValues() {return Collections.EMPTY_LIST;}
        @Override
        public Object getObject(String value) {return null;}
    }
}
