package zutil.net.http;

import java.net.URL;
import java.util.HashMap;

/**
 * Handles URLs in the HTTP protocol
 * 
 * @author Ziver
 */
public class HttpURL {
	public static final String PROTOCOL_SEPARATOR = "://";
	public static final String PORT_SEPARATOR = ":";
	public static final String PATH_SEPARATOR = "/";
	public static final String PARAMETER_SEPARATOR = "?";
	public static final String ANCHOR_SEPARATOR = "#";

	private String protocol	= "";
	private String host		= "127.0.0.1";
	private int port		= -1;
	private String path;
	private String anchor;
	
	private HashMap<String,String> parameters = new HashMap<String,String>();

	
	public HttpURL(){}
	
	public HttpURL( URL url ){
		this.setProtocol( url.getProtocol() );
		this.setHost( url.getHost() );
		this.setPort( url.getPort() );
		this.setPath( url.getPath() );
	}
	
	
	public String getProtocol( ){
		return protocol;
	}
	public String getHost( ){
		return host;
	}
	public int getPort( ){
		return port;
	}
	public String getPath( ){
		return path;
	}
	public String getAnchor( ){
		return anchor;
	}
	
	public void setProtocol( String prot ){
		this.protocol = prot;
	}
	public void setHost( String host ){
		this.host = host;
	}
	public void setPort( int port ){
		this.port = port;
	}
	public void setPath( String path ){
		if( path.length() >= 1 && !path.startsWith(PATH_SEPARATOR))
			path = PATH_SEPARATOR + path;
		this.path = path;
	}
	public void setAnchor( String anch ){
		this.anchor = anch;
	}
	public void setParameter( String key, String value ){
		this.parameters.put(key, value);
	}
	
	protected void setParameters( HashMap<String,String> pars ){
		this.parameters = pars;
	}
	
	/**
	 * Generates the parameter string in a URL. 
	 * 
	 * e.g.
	 * "key=value&key2=value&..."
	 */
	public String getParameterString(){
		StringBuilder param = new StringBuilder();
		for(String key : parameters.keySet()){
			param.append(key);
			param.append('=');
			param.append( parameters.get(key) );
			param.append('&');
		}
		if( param.length() > 0 )
			param.deleteCharAt( param.length()-1 );
		return param.toString();
	}

	/**
	 * Generates a path that are used in the HTTP header
	 */
	public String getHttpURL(){
		StringBuilder url = new StringBuilder();
		url.append( path );
		if( !parameters.isEmpty() )
			url.append( PARAMETER_SEPARATOR ).append( getParameterString() );
		
		return url.toString();
	}
	
	/**
	 * Generates a full URL
	 */
	public String getURL(){
		return toString();
	}
	
	/**
	 * Generates the whole URL
	 */
	public String toString(){
		StringBuilder url = new StringBuilder();
		url.append( protocol );
		url.append( PROTOCOL_SEPARATOR );
		url.append( host );
		if( port > 0 )
			url.append( PORT_SEPARATOR ).append( port );
		
		if( path != null )
			url.append( path );
		else
			url.append( PATH_SEPARATOR );
			
		if( !parameters.isEmpty() )
			url.append( PARAMETER_SEPARATOR ).append( getParameterString() );
		if( anchor != null )
			url.append( ANCHOR_SEPARATOR ).append( anchor );
		
		return url.toString();
	}
}
