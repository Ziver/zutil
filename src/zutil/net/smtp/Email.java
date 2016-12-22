package zutil.net.smtp;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.net.smtp.SmtpClient;

/**
 * Simplifies sending of a email
 * 
 * @author Ziver
 */
public class Email {
	public enum ContentType{
		PLAIN, HTML
	}
	private static final SimpleDateFormat dateFormatter = 
		new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private String from;
	private String niceFrom = null;
	private String to;
	private String replyTo = null;
	private ContentType type = ContentType.PLAIN;
	private String subject;
	private String message;



	public Email(){	}
	
	
	public void setFrom(String address){
		from = address;
	}
	public void setFrom(String address, String niceName){
		from = address;
		niceFrom = niceName;
	}
    public String getFromAddress(){
        return from;
    }
	public void setReplyTo(String rpt){
		replyTo = rpt;
	}
	public void setTo(String t){
		to = t;
	}
	public String getTo(){
		return to;
	}
	public void setContentType(ContentType t){
		type = t;
	}
	public void setSubject(String s){
		subject = s;
	}
	public void setMessage(String msg){
		message = msg;
	}


    /**
     * Will write the data from this object into a PrintStream.
     *
     * @throws IllegalArgumentException if from address and to address has not been set
     */
	public void write(PrintStream out) throws IOException{
        if(from == null)
            throw new IllegalArgumentException("From value cannot be null!");
        if(to == null)
            throw new IllegalArgumentException("To value cannot be null!");

		//************ Headers
		if (niceFrom!=null)
			out.println("From: \""+niceFrom+"\" <"+from+">");
		else
			out.println("From: <"+from+">");
		if ( replyTo != null )
			out.println("Reply-To: <"+replyTo+">");
		out.println("To: " + to);
		out.println("Subject: "+subject);
		// Date
		out.println("Date: "+dateFormatter.format(new Date(System.currentTimeMillis())));
		// Content type
		switch( type ){
		case HTML:
			out.println("Content-Type: text/html;"); break;
		default:
			out.println("Content-Type: text/plain;"); break;
		}
		out.println();
		
		//*********** Mesasge
		out.println( message );
	}
}