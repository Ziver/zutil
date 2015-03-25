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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for generating dynamic text/code from set data.
 * The syntax is similar to the javascript mustache library.
 *
 * <br /><br />
 * Supported tags:
 * <ul>
 *  <li><b> {{key}} </b><br>
 *      will be replaced with the string from the key</li>
 *  <li><b> {{#key}}...{{/key}} </b><br>
 *      will display content between the tags if key is defined</li>
 * </ul>
 *
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
                    root.add(new TmplStaticString(data.toString()));
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
                        case '#':
                            ++m.i;
                            root.add(parseTemplate(new TemplateCondition(tagName),
                                            tmpl, m, tagName));
                            break;
                        case '/':
                            // Is this tag closing the parent?
                            if(parentTag != null && tagName.endsWith(parentTag.substring(1)))
                                return root;
                            log.severe("Closing non-opened tag: {{"+tagName+"}}");
                            root.add(new TmplStaticString("{{"+tagName+"}}"));
                            break;
                        default:
                            root.add(new TmplDataAttribute(tagName));
                    }
                    break;
                default:
                    if(Character.isWhitespace(c)) // Not a valid tag if it contains whitespace
                        tagOpen = false;
                    data.append(c);
                    break;
            }
        }
        if(tagOpen) // Incomplete tag, insert it as normal text
            data.insert(0, "{{");
        if(data.length() > 0) // Still some text left, add to node
            root.add(new TmplStaticString(data.toString()));

        // If we get to this point means that this node is incorrectly close
        // or this is the end of the file, so we convert it to a normal node
        if(parentTag != null) {
            root = new TemplateNode(root);
            String tagName = "{{"+parentTag+"}}";
            log.severe("Missing closure of tag: "+tagName);
            root.addFirst(new TmplStaticString(tagName));
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
        private String key;

        public TemplateCondition(String key){
            this.key = key;
        }

        public void compile(StringBuilder str) {
            if(data.containsKey(key))
                super.compile(str);
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
