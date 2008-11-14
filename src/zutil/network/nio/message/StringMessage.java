package zutil.network.nio.message;

import zutil.network.nio.message.type.EchoMessage;
import zutil.network.nio.message.type.ResponseRequestMessage;



public class StringMessage extends EchoMessage implements ResponseRequestMessage{
	private static final long serialVersionUID = 1L;
	private double responseId;
	
	private String msg;
	
	public StringMessage(String msg){
		this.msg = msg;
		responseId = Math.random();
	}
	
	public String getString(){
		return msg;
	}
	
	public void setString(String msg){
		this.msg = msg;
	}
	
	public String toString(){
		return getString();
	}

	public double getResponseId() {
		return responseId;
	}
}
