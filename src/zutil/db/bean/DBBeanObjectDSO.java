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

package zutil.db.bean;

import zutil.db.DBConnection;
import zutil.log.LogUtil;
import zutil.parser.json.JSONParser;
import zutil.parser.json.JSONWriter;
import zutil.ui.Configurator;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A intermediate class for loading Objects of generic Classes.
 * The extending class must set the "superBean" parameter to true in {@link DBBean.DBTable}.
 * The Object that is stored must use Configurator to define what fields that should be stored.
 *
 * This class needs two fields in DB:
 * <ul>
 *     <li>String type: defining the class name</li>
 *     <li>Text config: the object configuration is stored as JSON</li>
 * </ul>
 */
public abstract class DBBeanObjectDSO<T> extends DBBean{
    private static final Logger logger = LogUtil.getLogger();

    // DB parameters
    private String type;
    /** Used to store the Object configuration in DB */
    private String config;

    // Local parameters
    private transient T cachedObj;



    @Override
    protected void postUpdateAction() {
        if (type != null && !type.isEmpty()) {
            if (cachedObj == null) {
                try {
                    Class clazz = Class.forName(type);
                    cachedObj = (T) clazz.newInstance();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Unable instantiate class: " + type, e);
                }
            }

            if (config != null && !config.isEmpty()) {
                Configurator<T> configurator = getObjectConfigurator();
                configurator.setValues(JSONParser.read(config));
                configurator.applyConfiguration();
            }
        }
    }

    @Override
    public void save(DBConnection db) throws SQLException {
        if (cachedObj == null)
            this.config = null;
        else {
            Configurator<T> configurator = getObjectConfigurator();
            this.config = JSONWriter.toString(configurator.getValuesAsNode());
        }
        super.save(db);
    }



    public T getObject(){
        return cachedObj;
    }

    /**
     * Will replace the current object.
     *
     * @param obj is the object to set or null to reset the DSO
     */
    public void setObject(T obj){
        if(obj != null) {
            type = obj.getClass().getName();
            config = null;
            cachedObj = obj;
        } else {
            type = null;
            config = null;
            cachedObj = null;
        }
    }

    public String getObjectClass(){
        return type;
    }

    public void setObjectClass(Class<? extends T> clazz){
        setObjectClass(clazz.getName());
    }

    public void setObjectClass(String clazz){
        if (this.type == null || !this.type.equals(type)) {
            // TODO: check if clazz is subclass of T
            setObject(null);
            this.type = clazz;
            postUpdateAction(); // instantiate cached object
        }
    }

    public Configurator<T> getObjectConfigurator(){
        return new Configurator<>(cachedObj);
    }


    public String toString(){
        Object obj = getObject();
        if (obj != null)
            return obj.toString();
        return "null (DSO: "+ super.toString() +")";
    }
}
