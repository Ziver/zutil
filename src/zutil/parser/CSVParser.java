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

import zutil.struct.MutableInt;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by Ziver
 */
public class CSVParser extends Parser{

    private Reader in;
    private char delimiter;
    private boolean parseHeader;

    private DataNode headers;


    public CSVParser(Reader in){
        this(in, false, ',');
    }
    public CSVParser(Reader in, boolean inclusedHeader){
        this(in, inclusedHeader, ',');
    }
    public CSVParser(Reader in, boolean includesHeader, char delimiter){
        this.in = in;
        this.delimiter = delimiter;
        this.parseHeader = includesHeader;
    }

    public DataNode getHeaders() {
        if(parseHeader) {
            try {
                parseHeader = false;
                headers = read();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return headers;
    }

    /**
     * Starts parsing from a string
     *
     * @param 	csv	is the JSON String to parse
     * @return a DataNode object representing the JSON in the input String
     */
    public static DataNode read(String csv){
        try{
            return new CSVParser(new StringReader(csv)).read();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Starts parsing from the input.
     * This method will block until one row has been parsed.
     *
     * @return a DataNode object representing a row in the CSV
     */
    @Override
    public DataNode read() throws IOException {
        // Make sure we have parsed headers
        if(parseHeader) {
            getHeaders();
        }

        DataNode data = new DataNode(DataNode.DataType.List);
        StringBuilder value = new StringBuilder();
        boolean quoteStarted = false;
        int c;
        while((c=in.read()) >= 0 && c != '\n'){
            if(c == delimiter && !quoteStarted){
                data.add(value.toString());
                value.delete(0, value.length()); // Reset StringBuilder
            }
            else if(c == '\"' &&  // Ignored quotes
                    (value.length() == 0 || quoteStarted)){
                quoteStarted = !quoteStarted;
            }
            else
                value.append((char)c);
        }
        if(value.length() > 0)
            data.add(value.toString());
        if(data.size() == 0)
            return null;
        return data;
    }
}
