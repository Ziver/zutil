package zutil.parser;

import zutil.parser.DataNode.DataType;

/**
 * http://wiki.theory.org/BitTorrentSpecification
 * @author Ziver
 *
 */
public class BEncodedParser {

	/**
	 * Returns the representation of the data in the BEncoded string
	 * 
	 * @param data The data to be decoded
	 * @return
	 */
	public static DataNode parse(String data){
		return decode_BEncoded(new StringBuffer(data));
	}

	/**
	 * Returns the representation of the data in the BEncoded string
	 * 
	 * @param data The data to be decoded
	 * @param index The index in data to start from
	 * @return
	 */
	private static DataNode decode_BEncoded(StringBuffer data){
		String tmp;
		char c = ' ';

		switch (data.charAt(0)) {
		/**
		 * Integers are prefixed with an i and terminated by an e. For
		 * example, 123 would bEcode to i123e, -3272002 would bEncode to
		 * i-3272002e.
		 */
		case 'i':
			//System.out.println("Found Integer at "+index);
			data.deleteCharAt(0);
			tmp = data.substring(0, data.indexOf("e"));
			data.delete(0, tmp.length() + 1);
			//System.out.println(tmp);
			return new DataNode( new Long(tmp));
		/**
		 * Lists are prefixed with a l and terminated by an e. The list
		 * should contain a series of bEncoded elements. For example, the
		 * list of strings ["Monduna", "Bit", "Torrents"] would bEncode to
		 * l7:Monduna3:Bit8:Torrentse. The list [1, "Monduna", 3, ["Sub", "List"]]
		 * would bEncode to li1e7:Mondunai3el3:Sub4:Listee
		 */
		case 'l':
			//System.out.println("Found List at "+index);
			data.deleteCharAt(0);
			DataNode list = new DataNode( DataType.List );
			c = data.charAt(0);
			while(c != 'e'){
				list.add( decode_BEncoded(data) );
				c = data.charAt(0);
			}
			data.deleteCharAt(0);
			//MultiPrintStream.out.dump(list);
			if(list.size() == 1) return list.get(0);
			else return list;
		/**
		 * Dictionaries are prefixed with a d and terminated by an e. They
		 * are similar to list, except that items are in key value pairs. The
		 * dictionary {"key":"value", "Monduna":"com", "bit":"Torrents", "number":7}
		 * would bEncode to d3:key5:value7:Monduna3:com3:bit:8:Torrents6:numberi7ee
		 */
		case 'd':
			//System.out.println("Found Dictionary at "+index);
			data.deleteCharAt(0);
			DataNode map = new DataNode( DataType.Map );
			c = data.charAt(0);
			while(c != 'e'){
				DataNode tmp2 = decode_BEncoded(data);
				map.set(tmp2.getString(), decode_BEncoded(data));
				c = data.charAt(0);
			}
			data.deleteCharAt(0);
			//MultiPrintStream.out.dump(map);
			return map;
		/**
		 * Strings are prefixed with their length followed by a colon.
		 * For example, "Monduna" would bEncode to 7:Monduna and "BitTorrents"
		 * would bEncode to 11:BitTorrents.
		 */
		default:
			//System.out.println("Found String at "+index);
			tmp = data.substring(0, data.indexOf(":"));
			int length = Integer.parseInt(tmp);
			data.delete(0, tmp.length()+1);
			String ret = data.substring(0, length);
			data.delete(0, length);
			//System.out.println(data.substring(i, i+length));
			return new DataNode( ret );
		}
	}
}
