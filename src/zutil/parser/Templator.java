/*
 * Copyright (c) 2015 Ziver
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

import zutil.log.LogUtil;
import zutil.struct.MutableInt;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
 *      Will display content between the tags if key is defined.</li>
 *  <li><b>{{^key}}</b><br>
 *      A negative condition, will display content if key is not set.</li>
 *  <li><b>{{! ignore me }}</b><br>
 *      Comment, will be ignored.</li>
 * </ul>
 *
 * TODO:
 *  * {{#person}}: support for boolean
 *  * {{#person}}: support for iterators
 *  * {{> file}}: include file
 *  * {{=<% %>=}}: change delimiter
 * @author Ziver koc
 */
public class Templator {
    private static final Logger log = LogUtil.getLogger();
    private HashMap<String,Object> data;
    private TemplateEntity tmplRoot;

    public Templator(String tmpl){
        this.data = new HashMap<String, Object>();
        parseTemplate(tmpl);
    }

    public void set(String key, Object data){
        this.data.put(key, data);
    }

    /**
     * Will clear all data attributes
     */
    public void clear(){
        data.clear();
    }

    public String compile(){
        StringBuilder str = new StringBuilder();
        if(tmplRoot != null)
            tmplRoot.compile(str);
        return str.toString();
    }

    /**
     * Will pare or re-parse the source template.
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




    /**************************** Utility functions *************************************/
    public static Object getFieldValue(Object obj, String attrib){
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                if(field.getName().equals(attrib)) {
                    field.setAccessible(true);
                    return field.get(obj);
                }
            }
        }catch (IllegalAccessException e){
            log.log(Level.WARNING, null, e);
        }
        return null;
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
        private String key;

        public TemplateCondition(String key){
            this.key = key.trim();
        }

        public void compile(StringBuilder str) {
            if(data.containsKey(key))
                super.compile(str);
        }
    }

    protected class TemplateNegativeCondition extends TemplateNode {
        private String key;

        public TemplateNegativeCondition(String key){
            this.key = key.trim();
        }

        public void compile(StringBuilder str) {
            if( ! data.containsKey(key))
                super.compile(str);
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
        private String key;
        private String attrib;

        public TemplateDataAttribute(String key){
            String[] s = key.trim().split("\\.", 2);
            this.key = s[0];
            if(s.length > 1)
                this.attrib = s[1];
        }

        public void compile(StringBuilder str) {
            if (data.containsKey(key)) {
                if (attrib != null) {
                    Object obj = getFieldValue(data.get(key), attrib);
                    if(obj != null)
                        str.append(obj.toString());
                    else
                        str.append("{{").append(key).append(".").append(attrib).append("}}");
                }
                else
                    str.append(data.get(key).toString());
            }
            else
                str.append("{{").append(key).append("}}");
        }
    }
}
