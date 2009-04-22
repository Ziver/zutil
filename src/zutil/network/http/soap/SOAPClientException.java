package zutil.network.http.soap;

/**
 * This generates an client fault message 
 * when used with SOAPHttpPage
 * 
 * @author Ziver
 */
public class SOAPClientException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public SOAPClientException(String string) {
		super(string);
	}
}
