package zutil.parser;

import java.util.HashMap;

/**
 * Parses BBCode and replaces them with the corresponding HTML.
 * 
 * @author Ziver
 */
public class BBCodeParser {
	/** Contains all the BBCodes and the corresponding HTML **/
	private HashMap<String, String> bbcodes;
	
	public static void main(String[] args){
		BBCodeParser parser = new BBCodeParser();
		System.out.println(parser.parse("jshdkj [u]lol [apa]lol[/apa]"));
		System.out.println(parser.parse("jshdkj [m]lol[/k] [i]lol[/i]"));
		System.out.println(parser.parse("jshdkj [m"));
		System.out.println(parser.parse("jshdkj [/m"));
		System.out.println(parser.parse("jshdkj m]"));
		System.out.println(parser.parse("jshdkj <br />"));
	}
	
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
	public String parse(String text){
		StringBuilder out = new StringBuilder();
		StringBuilder t = new StringBuilder(text);
		
		parse(t, out, "");
		
		return out.toString();
	}
	
	private void parse(StringBuilder text, StringBuilder out, String rootBBC){
		StringBuilder bbcode = null;
		boolean closeTag = false;
		while(text.length() > 0){
			char c = text.charAt(0);
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
					param = parseParam(text);
				case ']':
					String bbcode_cache = bbcode.toString();
					if(closeTag){
						if(!rootBBC.equals(bbcode_cache)){
							out.append("[/").append(bbcode).append("]");
						}
						return;
					}
					String value = parseValue(text, bbcode_cache);
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
			if(text.length()>0)
				text.deleteCharAt(0);
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
	private String parseParam(StringBuilder text){
		StringBuilder param = new StringBuilder();
		while(text.length() > 0){
			char c = text.charAt(0);
			if(c == ']')
				break;
			else if(c != '=') 
				param.append(c);
			text.deleteCharAt(0);
		}
		return param.toString();
	}
	
	/**
	 * Parses the value in the BBCodes e.g. [url]value[/url]
	 * 
	 * @param text is the text to parse the value from
	 */
	private String parseValue(StringBuilder text, String bbcode){
		StringBuilder value = new StringBuilder();
		while(text.length() > 0){
			char c = text.charAt(0);
			if(c == '['){
				parse(text, value, bbcode);
				break;
			}
			else if(c != ']') 
				value.append(c);
			text.deleteCharAt(0);
		}
		return value.toString();
	}
}
