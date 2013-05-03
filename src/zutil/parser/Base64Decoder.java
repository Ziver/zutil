package zutil.parser;

import zutil.converters.Converter;
import zutil.io.DynamicByteArrayStream;

public class Base64Decoder {
	public static final char[] B64_ENCODE_TABLE = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',	'I', 
		'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 
		'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 
		's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
		
		'0', '1', '2', '3', '4', '5', '6', '7', '8', 
		'9', '+', '/'
	};
	
	private DynamicByteArrayStream output;
	private byte rest_data;
	private int rest = 0;
	
	public Base64Decoder(){
		output = new DynamicByteArrayStream();
	}
	
	public static String decodeToHex( String data ){
		Base64Decoder base64 = new Base64Decoder();
		base64.write( data );
		return Converter.toHexString( base64.getByte() );
	}
	
	public static String decode( String data ){
		Base64Decoder base64 = new Base64Decoder();
		base64.write( data );
		return base64.toString();
	}
	
	public void write( String data ){
		byte[] buffer = new byte[ (data.length()*6/8) + 1 ];
		int buffi = 0;
		if( rest != 0 )
			buffer[0] = rest_data;
		
		for( int i=0; i<data.length(); i++){
			char c = data.charAt(i);
			if( c == '='){
				rest = (rest + 2) % 8;
				continue;
			}
			byte b = getByte(c);
			
			switch(rest){
			case 0:
				buffer[buffi] = (byte) ((b << 2) & 0xFC);
				break;
			case 2:
				buffer[buffi]  |= (byte) ((b >> 4) & 0x03);
				buffer[++buffi] = (byte) ((b << 4) & 0xF0);
				break;
			case 4:
				buffer[buffi]  |= (byte) ((b >> 2) & 0x0F);
				buffer[++buffi] = (byte) ((b << 6) & 0xC0);
				break;
			case 6:
				buffer[buffi++]|= (byte) (b & 0x3F);
				break;
			}
			
			rest = (rest + 2) % 8;
		}
		
		if( rest != 0 )
			rest_data = buffer[buffi--];
		output.append( buffer, 0, buffi );
	}
	
	public String toString(){
		return output.getString();
	}
	
	public byte[] getByte(){
		return output.getByte();
	}
	
	public void clear(){
		output.clear();
		rest = 0;
		rest_data = 0;
	}
	
	public static String addPadding( String data ){
		int padding = 4 - (data.length() % 4);
		switch( padding ){
		case 0: return data;
		case 1: return data + "=";
		case 2: return data + "==";
		case 3: return data + "===";
		}
		return null;
	}
	
	private byte getByte( char c ){
		switch(c){
		case 'A': return (byte)( 0 & 0xff);
		case 'B': return (byte)( 1 & 0xff);
		case 'C': return (byte)( 2 & 0xff);
		case 'D': return (byte)( 3 & 0xff);
		case 'E': return (byte)( 4 & 0xff);
		case 'F': return (byte)( 5 & 0xff);
		case 'G': return (byte)( 6 & 0xff);
		case 'H': return (byte)( 7 & 0xff);
		case 'I': return (byte)( 8 & 0xff);
		case 'J': return (byte)( 9 & 0xff);
		case 'K': return (byte)(10 & 0xff);
		case 'L': return (byte)(11 & 0xff);
		case 'M': return (byte)(12 & 0xff); 
		case 'N': return (byte)(13 & 0xff);
		case 'O': return (byte)(14 & 0xff);
		case 'P': return (byte)(15 & 0xff);
		case 'Q': return (byte)(16 & 0xff);
		case 'R': return (byte)(17 & 0xff);
		case 'S': return (byte)(18 & 0xff);
		case 'T': return (byte)(19 & 0xff);
		case 'U': return (byte)(20 & 0xff);
		case 'V': return (byte)(21 & 0xff);
		case 'W': return (byte)(22 & 0xff);
		case 'X': return (byte)(23 & 0xff);
		case 'Y': return (byte)(24 & 0xff);
		case 'Z': return (byte)(25 & 0xff);
		
		case 'a': return (byte)(26 & 0xff);
		case 'b': return (byte)(27 & 0xff);
		case 'c': return (byte)(28 & 0xff);
		case 'd': return (byte)(29 & 0xff);
		case 'e': return (byte)(30 & 0xff);
		case 'f': return (byte)(31 & 0xff);
		case 'g': return (byte)(32 & 0xff);
		case 'h': return (byte)(33 & 0xff);
		case 'i': return (byte)(34 & 0xff);
		case 'j': return (byte)(35 & 0xff);
		case 'k': return (byte)(36 & 0xff);
		case 'l': return (byte)(37 & 0xff);
		case 'm': return (byte)(38 & 0xff);
		case 'n': return (byte)(39 & 0xff);
		case 'o': return (byte)(40 & 0xff);
		case 'p': return (byte)(41 & 0xff);
		case 'q': return (byte)(42 & 0xff);
		case 'r': return (byte)(43 & 0xff);
		case 's': return (byte)(44 & 0xff);
		case 't': return (byte)(45 & 0xff);
		case 'u': return (byte)(46 & 0xff);
		case 'v': return (byte)(47 & 0xff);
		case 'w': return (byte)(48 & 0xff);
		case 'x': return (byte)(49 & 0xff);
		case 'y': return (byte)(50 & 0xff);
		case 'z': return (byte)(51 & 0xff);
	
		case '0': return (byte)(52 & 0xff); 
		case '1': return (byte)(53 & 0xff);
		case '2': return (byte)(54 & 0xff);
		case '3': return (byte)(55 & 0xff);
		case '4': return (byte)(56 & 0xff);
		case '5': return (byte)(57 & 0xff);
		case '6': return (byte)(58 & 0xff);
		case '7': return (byte)(59 & 0xff);
		case '8': return (byte)(60 & 0xff);
		case '9': return (byte)(61 & 0xff);
		case '+': return (byte)(62 & 0xff);
		case '/': return (byte)(63 & 0xff);
		default: return -1;
		}
	}
}
