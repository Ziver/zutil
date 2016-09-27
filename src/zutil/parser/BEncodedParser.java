/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

import zutil.parser.DataNode.DataType;
import zutil.struct.MutableInt;

import java.nio.charset.MalformedInputException;
import java.text.ParseException;

/**
 * http://wiki.theory.org/BitTorrentSpecification
 * @author Ziver
 *
 */
public class BEncodedParser {

	/**
	 * Returns the representation of the data in the BEncoded string
	 * 
	 * @param	data	is the data to be decoded
	 * @return
	 */
	public static DataNode read(String data) throws ParseException {
		return decode_BEncoded(new MutableInt(), new StringBuilder(data));
	}

	/**
	 * Returns the representation of the data in the BEncoded string
	 * 
	 * @param	data	is the data to be decoded
	 * @param	index	is the index in data to start from
	 * @return
	 */
	private static DataNode decode_BEncoded(MutableInt index, StringBuilder data) throws ParseException {
		String tmp;
		char c = ' ';
        int end;

		switch (data.charAt(index.i)) {
		/**
		 * Integers are prefixed with an i and terminated by an e. For
		 * example, 123 would bEcode to i123e, -3272002 would bEncode to
		 * i-3272002e.
		 */
		case 'i':
			index.i++;
            end = data.indexOf("e", index.i);
            if (end < 0)
                throw new ParseException("Corrupt bEncoding", index.i);
			tmp = data.substring(index.i, end);
			index.i += tmp.length() + 1;
			return new DataNode( new Long(tmp));
		/**
		 * Lists are prefixed with a l and terminated by an e. The list
		 * should contain a series of bEncoded elements. For example, the
		 * list of strings ["Monduna", "Bit", "Torrents"] would bEncode to
		 * l7:Monduna3:Bit8:Torrentse. The list [1, "Monduna", 3, ["Sub", "List"]]
		 * would bEncode to li1e7:Mondunai3el3:Sub4:Listee
		 */
		case 'l':
			index.i++;
			DataNode list = new DataNode( DataType.List );
			c = data.charAt(index.i);
			while(c != 'e'){
				list.add( decode_BEncoded(index, data) );
				c = data.charAt(index.i);
			}
			index.i++;
			if(list.size() == 1) return list.get(0);
			else return list;
		/**
		 * Dictionaries are prefixed with a d and terminated by an e. They
		 * are similar to list, except that items are in key value pairs. The
		 * dictionary {"key":"value", "Monduna":"com", "bit":"Torrents", "number":7}
		 * would bEncode to d3:key5:value7:Monduna3:com3:bit:8:Torrents6:numberi7ee
		 */
		case 'd':
			index.i++;
			DataNode map = new DataNode( DataType.Map );
			c = data.charAt(index.i);
			while(c != 'e'){
				DataNode tmp2 = decode_BEncoded(index, data);
				map.set(tmp2.getString(), decode_BEncoded(index, data));
                if (index.i >= data.length())
                    throw new ParseException("Incorrect bEncoding ending of element", index.i);
				c = data.charAt(index.i);
			}
			index.i++;
			return map;
		/**
		 * Strings are prefixed with their length followed by a colon.
		 * For example, "Monduna" would bEncode to 7:Monduna and "BitTorrents"
		 * would bEncode to 11:BitTorrents.
		 */
		default:
			end = data.indexOf(":", index.i);
			if (end < 0)
				throw new ParseException("Corrupt bEncoding", index.i);
			tmp = data.substring(index.i, end);
			int length = Integer.parseInt(tmp);
			index.i += tmp.length() + 1;
			String ret = data.substring(index.i, index.i+length);
			index.i += length;
			return new DataNode( ret );
		}
	}
}
