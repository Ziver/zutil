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

package zutil.parser;

import zutil.io.file.FileUtil;
import zutil.log.LogUtil;
import zutil.struct.MutableInt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for generating dynamic text/code from set data.
 * The syntax is similar to the javascript mustache library.
 *
 * <br /><br />
 * Supported tags:
 * <ul>
 *  <li><b> {{key}} </b><br>
 *      <b> {{obj.attr}} </b><br>
 *      Will be replaced with the string from the key.</li>
 *  <li><b> {{#key}}...{{/key}} </b><br>
 *      <b> {{#obj.attr}}...{{/obj.attr}} </b><br>
 *      Will display content between the tags if:
 *      <ul>
 *          <li>key is defined,</li>
 *          <li>if the key references a list or array the content will be iterated
 *              for every element, the element can be referenced by the tag {{.}},</li>
 *          <li>if key is a boolean with the value true,</li>
 *          <li>if key is a Integer with the value anything other then 0,</li>
 *          <li>if key ends with () it will be evaluated as a method call, the returned
 *              type will be evaluated against the criteria in this list.</li>
 *      </ul>
 *      </li>
 *  <li><b> {{^key}}</b><br>
 *      <b> {{^obj.attr}}...{{/obj.attr}} </b><br>
 *      A negative condition, will display content if:
 *      <ul>
 *          <li>the key is undefined,</li>
 *          <li>the key is a empty list,</li>
 *          <li>the key is a zero length array,</li>
 *          <li>the key is a false boolean,</li>
 *          <li>the key is a 0 Integer</li>
 *          <li>if key ends with () it will be evaluated as a method call, the returned
 *              type will be evaluated against the criteria in this list.</li>
 *      </ul>
 *      </li>
 *  <li><b>{{! ignore me }}</b><br>
 *      Comment, will be ignored.</li>
 * </ul>
 *
 * TODO: {{> file}}: include file
 * TODO: {{=<% %>=}}: change delimiter
 *
 * @author Ziver koc
 */
public class Templator {
    private static final Logger log = LogUtil.getLogger();

    private HashMap<String,Object> data;
    private TemplateEntity tmplRoot;

    // File metadata
    private File file;
    private long lastModified;

    /**
     * A template file will be read from the disk. The template will
     * be regenerated if the file changes.
     */
    public Templator(File tmpl) throws IOException {
        this.data = new HashMap<String, Object>();
        this.file = tmpl;
        parseTemplate(FileUtil.getContent(file));
        this.lastModified = file.lastModified();
    }
    public Templator(String tmpl){
        this.data = new HashMap<String, Object>();
        parseTemplate(tmpl);
    }


    public void set(String key, Object data){
        this.data.put(key, data);
    }
    public Object get(String key){
        return this.data.get(key);
    }
    public void remove(String key){
        this.data.remove(key);
    }

    /**
     * Will clear all data attributes
     */
    public void clear(){
        data.clear();
    }

    public String compile(){
        if(file != null && lastModified != file.lastModified()){
            try {
                log.info("Template file("+file.getName()+") changed. Regenerating template...");
                parseTemplate(FileUtil.getContent(file));
                this.lastModified = file.lastModified();
            } catch(IOException e) {
                log.log(Level.WARNING, "Unable to regenerate template", e);
            }
        }

        StringBuilder str = new StringBuilder();
        if(tmplRoot != null)
            tmplRoot.compile(str);
        return str.toString();
    }

    /**
     * Will parse or re-parse the source template.
     */
    private void parseTemplate(String tmpl){
        tmplRoot = parseTemplate(new TemplateNode(), tmpl, new MutableInt(), null);
    }
    private TemplateNode parseTemplate(TemplateNode root, String tmpl, MutableInt m, String parentTag){
        StringBuilder data = new StringBuilder();
        boolean tagOpen = false;

        for(; m.i<tmpl.length(); ++m.i){
            char c = tmpl.charAt(m.i);
            String d = ""+ c + (m.i+1<tmpl.length() ? tmpl.charAt(m.i+1) : ' ');
            switch( d ){
                case "{{":
                    root.add(new TemplateStaticString(data.toString()));
                    data.delete(0, data.length());
                    tagOpen = true;
                    ++m.i;
                    break;
                case "}}":
                    if(!tagOpen){ // Tag not opened, incorrect enclosure
                        data.append(c);
                        continue;
                    }
                    tagOpen = false;
                    ++m.i;
                    String tagName = data.toString();
                    data.delete(0, data.length());
                    switch(tagName.charAt(0)) {
                        case '#': // Condition
                            ++m.i;
                            root.add(parseTemplate(new TemplateCondition(tagName.substring(1)),
                                            tmpl, m, tagName));
                            break;
                        case '^': // Negative condition
                            ++m.i;
                            root.add(parseTemplate(new TemplateNegativeCondition(tagName.substring(1)),
                                    tmpl, m, tagName));
                            break;
                        case '/': // End tag
                            // Is this tag closing the parent?
                            if(parentTag != null && tagName.endsWith(parentTag.substring(1)))
                                return root;
                            log.severe("Closing non-opened tag: {{" + tagName + "}}");
                            root.add(new TemplateStaticString("{{"+tagName+"}}"));
                            break;
                        case '!': // Comment
                            break;
                        default:
                            root.add(new TemplateDataAttribute(tagName));
                    }
                    break;
                default:
                    data.append(c);
                    break;
            }
        }
        if(tagOpen) // Incomplete tag, insert it as normal text
            data.insert(0, "{{");
        if(data.length() > 0) // Still some text left, add to node
            root.add(new TemplateStaticString(data.toString()));

        // If we get to this point means that this node is incorrectly close
        // or this is the end of the file, so we convert it to a normal node
        if(parentTag != null) {
            root = new TemplateNode(root);
            String tagName = "{{"+parentTag+"}}";
            log.severe("Missing closure of tag: " + tagName);
            root.addFirst(new TemplateStaticString(tagName));
        }
        return root;
    }


    /**************************** Template Helper Classes *************************************/

    protected interface TemplateEntity {
        public void compile(StringBuilder str);
    }

    protected class TemplateNode implements TemplateEntity {
        private List<TemplateEntity> entities;

        public TemplateNode(){
            this.entities = new ArrayList<TemplateEntity>();
        }
        public TemplateNode(TemplateNode node){
            this.entities = node.entities;
        }

        public void addFirst(TemplateEntity s){
            entities.add(0, s);
        }
        public void add(TemplateEntity s){
            entities.add(s);
        }

        public void compile(StringBuilder str) {
            for(TemplateEntity sec : entities)
                sec.compile(str);
        }
    }

    protected class TemplateCondition extends TemplateNode {
        private TemplateDataAttribute attrib;

        public TemplateCondition(String key){
            this.attrib = new TemplateDataAttribute(key);
        }

        public void compile(StringBuilder str) {
            Object obj = attrib.getObject();
            if(obj != null) {
                if(obj instanceof Boolean){
                    if ((Boolean) obj)
                        super.compile(str);
                }
                else if(obj instanceof Integer){
                    if ((Integer) obj != 0)
                        super.compile(str);
                }
                else if(obj instanceof Iterable || obj.getClass().isArray()) {
                    Object prevObj = get(".");
                    set(".", obj);

                    if (obj instanceof Iterable) {
                        for (Object o : (Iterable) obj) { // Iterate through the whole list
                            set(".", o);
                            super.compile(str);
                        }
                    } else if (obj.getClass().isArray()) {
                        int length = Array.getLength(obj);
                        for (int i = 0; i < length; i++) {
                            set(".", Array.get(obj, i));
                            super.compile(str);
                        }
                    }

                    // Reset map to parent object
                    if(prevObj != null)
                        set(".", prevObj);
                    else
                        remove(".");
                }
                else
                    super.compile(str);

            }
        }
    }

    protected class TemplateNegativeCondition extends TemplateNode {
        private TemplateDataAttribute attrib;

        public TemplateNegativeCondition(String key){
            this.attrib = new TemplateDataAttribute(key);
        }

        public void compile(StringBuilder str) {
            Object obj = attrib.getObject();
            if(obj == null)
                super.compile(str);
            else {
                if(obj instanceof Boolean) {
                    if ( ! (Boolean) obj)
                        super.compile(str);
                }
                else if(obj instanceof Integer){
                    if ((Integer) obj == 0)
                        super.compile(str);
                }
                else if(obj instanceof Collection) {
                    if (((Collection) obj).isEmpty())
                        super.compile(str);
                }
                else if(obj.getClass().isArray()) {
                    if (((Object[]) obj).length <= 0)
                        super.compile(str);
                }
            }
        }
    }

    protected class TemplateStaticString implements TemplateEntity {
        private String text;

        public TemplateStaticString(String text){
            this.text = text;
        }

        public void compile(StringBuilder str) {
            str.append(text);
        }
    }

    protected class TemplateDataAttribute implements TemplateEntity {
        private String tag;
        private String[] keys;

        public TemplateDataAttribute(String tag){
            this.tag = tag;
            this.keys = tag.trim().split("\\.");
            if(this.keys.length == 0)
                this.keys = new String[]{"."};
            if(this.keys[0].isEmpty()) // if tag starts with "."
                this.keys[0] = ".";
        }


        public Object getObject(){
            if (data.containsKey(tag))
                return data.get(tag);
            else if (data.containsKey(keys[0]) && data.get(keys[0]) != null) {
                Object obj = data.get(keys[0]);
                for(int i=1; i<keys.length; ++i){
                    obj = getFieldValue(obj, keys[i]);
                    if(obj == null)
                        return null;
                }
                return obj;
            }
            return null;
        }
        protected Object getFieldValue(Object obj, String attrib){
            try {
                if(attrib.endsWith("()")){ // Is this a function call?
                    if(attrib.length() > 2) {
                        String funcName = attrib.substring(0, attrib.length()-2);
                        // Using a loop as the direct lookup throws a exception if no field was found
                        // So this is probably a bit faster
                        for (Method m : obj.getClass().getMethods()) {
                            if (m.getParameterTypes().length == 0 && m.getName().equals(funcName)) {
                                m.setAccessible(true);
                                return m.invoke(obj);
                            }
                        }
                    }
                }
                else if(obj.getClass().isArray() && "length".equals(attrib))
                    return Array.getLength(obj);
                else if(obj instanceof Collection && "length".equals(attrib))
                    return ((Collection) obj).size();
                else {
                    // Using a loop as the direct lookup throws a exception if no field was found
                    // So this is probably a bit faster
                    for (Field field : obj.getClass().getFields()) { // Only look for public fields
                        if (field.getName().equals(attrib)) {
                            field.setAccessible(true);
                            return field.get(obj);
                        }
                    }
                }
            }catch (IllegalAccessException e){
                log.log(Level.WARNING, null, e);
            } catch (InvocationTargetException e) {
                log.log(Level.WARNING, null, e);
            }
            return null;
        }


        public void compile(StringBuilder str) {
            Object obj = getObject();
            if(obj != null)
                str.append(obj.toString());
            else
                str.append("{{").append(tag).append("}}");
        }
    }
}
