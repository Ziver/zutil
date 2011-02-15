package zutil.net.http.soap;

/**
 * This generates an client fault message 
 * when used with SOAPHttpPage
 * 
 * @author Ziver
 */
public class SOAPException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public SOAPException(String string) {
		super(string);
	}
}
