/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.parser;

import java.util.HashMap;

import zutil.struct.MutableInt;

/**
 * Parses BBCode and replaces them with the corresponding HTML.
 * 
 * @author Ziver
 */
public class BBCodeParser {
	/** Contains all the BBCodes and the corresponding HTML **/
	private HashMap<String, String> bbcodes;
	
	
	/**
	 * Initiates a instance of the parser with the most used BBCodes.
	 */
	public BBCodeParser(){
		bbcodes = new HashMap<String,String>();
		addBBCode("b", "<b>%2</b>");
		addBBCode("i", "<i>%2</i>");
		addBBCode("u", "<u>%2</u>");
		addBBCode("url", "<a href=\"%1\">%2</a>");
		addBBCode("img", "<img src=\"%2\"></img>");
		addBBCode("quote", "<blockquote>%2</blockquote>");
		addBBCode("code", "<pre>%2</pre>");
		addBBCode("size", "<span style=\"font-size:%1px;\">%2</span>");
		addBBCode("color", "<span style=\"color: %1;\">%2</span>");
		addBBCode("ul", "<ul>%2</ul>");
		addBBCode("li", "<li>%2</li>");
	}
	
	/**
	 * Registers a new BBCode to the parser. Only one type of BBCode allowed.
	 * 
	 * @param bbcode is the BBCode e.g. "b" or "url"
	 * @param html is the corresponding HTML e.g. "<a href='%1'>%2</a>" 
	 * 				where the %x corresponds to BBCode like this: [url=%1]%2[/url] 
	 */
	public void addBBCode(String bbcode, String html){
		bbcodes.put(bbcode, html);
	}
	
	/**
	 * Removes a BBCode definition from the parser
	 * 
	 * @param bbcode is the bbcode to remove
	 */
	public void removeBBCode(String bbcode){
		bbcodes.remove(bbcode);
	}
	
	/**
	 * Parses the text with BBCode and converts it to HTML
	 * 
	 * @param text is a String with BBCode
	 * @return a String where all BBCode has been replaced by HTML
	 */
	public String read(String text){
		StringBuilder out = new StringBuilder();
		StringBuilder t = new StringBuilder(text);
		
		read(new MutableInt(), t, out, "");
		
		return out.toString();
	}
	
	private void read(MutableInt index, StringBuilder text, StringBuilder out, String rootBBC){
		StringBuilder bbcode = null;
		boolean closeTag = false;
		while(index.i < text.length()){
			char c = text.charAt(index.i);
			if(c == '['){
				bbcode = new StringBuilder();
			}
			else if(bbcode!=null && c == '/'){
				closeTag = true;
			}
			else if(bbcode!=null){
				String param = "";
				switch(c){
				case '=':
					param = parseParam(index, text);
				case ']':
					String bbcode_cache = bbcode.toString();
					if(closeTag){
						if(!rootBBC.equals(bbcode_cache)){
							out.append("[/").append(bbcode).append("]");
						}
						return;
					}
					String value = parseValue(index, text, bbcode_cache);
					if(bbcodes.containsKey( bbcode_cache )){
						String html = bbcodes.get( bbcode_cache );
						html = html.replaceAll("%1", param);
						html = html.replaceAll("%2", value);
						out.append(html);
					}
					else{
						out.append('[').append(bbcode);
						if(!param.isEmpty()) 
							out.append('=').append(param);
						out.append(']').append(value);
						out.append("[/").append(bbcode).append("]");
					}
					bbcode = null;
					break;
				default:
					bbcode.append(c);
					break;
				}
			}
			else
				out.append(c);
			
			index.i++;
		}
		if(bbcode!=null)
			if(closeTag)out.append("[/").append(bbcode);
			else 		out.append('[').append(bbcode);
	}
	
	/**
	 * Parses a parameter from a BBCode block: [url=param]
	 * 
	 * @param text is the text to parse from
	 * @return only the parameter string
	 */
	private String parseParam(MutableInt index, StringBuilder text){
		StringBuilder param = new StringBuilder();
		while(index.i < text.length()){
			char c = text.charAt(index.i);
			if(c == ']')
				break;
			else if(c != '=') 
				param.append(c);
			index.i++;
		}
		return param.toString();
	}
	
	/**
	 * Parses the value in the BBCodes e.g. [url]value[/url]
	 * 
	 * @param text is the text to parse the value from
	 */
	private String parseValue(MutableInt index, StringBuilder text, String bbcode){
		StringBuilder value = new StringBuilder();
		while(index.i < text.length()){
			char c = text.charAt(index.i);
			if(c == '['){
				read(index, text, value, bbcode);
				break;
			}
			else if(c != ']') 
				value.append(c);
			index.i++;
		}
		return value.toString();
	}
}
