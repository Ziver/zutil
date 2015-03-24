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

import zutil.struct.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for generating dynamic text/code from set data.
 * The syntax is similar to the javascript mustache library.
 *
 * Supported tags:
 *  * {{key}}: will be replaced with the string from the key
 *
 *
 * Created by Ziver on 2015-03-23.
 */
public class Templator {
    private HashMap<String,Object> data;
    private TemplateEntity tmplRoot;

    public Templator(String tmpl){
        this.data = new HashMap<String, Object>();
        parseTemplate(tmpl);
    }

    public void setData(String key, Object data){
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
        TemplateNode node = new TemplateNode();
        parseTemplate(node, tmpl, new MutableInt());
        tmplRoot = node;
    }
    private void parseTemplate(TemplateNode root, String tmpl, MutableInt m){
        StringBuilder data = new StringBuilder();
        StringBuilder tags = new StringBuilder();
        boolean tagOpen = false;

        for(; m.i<tmpl.length(); ++m.i){
            char c = tmpl.charAt(m.i);
            String d = ""+ c + (m.i+1<tmpl.length() ? tmpl.charAt(m.i+1) : ' ');
            switch( d ){
                case "{{":
                    root.addEntity(new TmplStaticString(data.toString()));
                    data.delete(0, data.length());
                    tags.delete(0, data.length());
                    tags.append("{{");
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
                    root.addEntity(new TmplDataAttribute(data.toString()));
                    data.delete(0, data.length());
                    break;
                default:
                    data.append(c);
                    break;
            }
        }
        if(tagOpen)
            data.insert(0, tags);
        if(data.length() > 0)
            root.addEntity(new TmplStaticString(data.toString()));
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

        public void addEntity(TemplateEntity s){
            entities.add(s);
        }

        public void compile(StringBuilder str) {
            for(TemplateEntity sec : entities)
                sec.compile(str);
        }
    }

    protected class TmplStaticString implements TemplateEntity {
        private String text;

        public TmplStaticString(String text){
            this.text = text;
        }

        public void compile(StringBuilder str) {
            str.append(text);
        }
    }

    protected class TmplDataAttribute implements TemplateEntity {
        private String key;

        public TmplDataAttribute(String key){
            this.key = key;
        }

        public void compile(StringBuilder str) {
            if(data.containsKey(key))
                str.append(data.get(key).toString());
            else
                str.append("{{").append(key).append("}}");
        }
    }
}
