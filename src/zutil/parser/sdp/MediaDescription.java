/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.parser.sdp;

import java.util.HashMap;
import java.util.Map;

/**
 * A data class containing information about a specific media
 */
public class MediaDescription {
    protected String type;
    protected int transportPort;
    protected String transport;

    protected String label;

    protected Map<String,String> attributes = new HashMap<>();


    public String getType() {
        return type;
    }

    public int getTransportPort() {
        return transportPort;
    }

    public String getTransport() {
        return transport;
    }

    public String getLabel() {
        return label;
    }


    public String toString() {
        StringBuffer output = new StringBuffer();

        output.append("m=").append(type).append(' ').append(transportPort).append(' ').append(transport).append('\n'); // TODO: media formats

        if (label != null) output.append("i=").append(label).append('\n');
        // TODO: [optional] c=<network type> <address type> <connection address> // Media specific connection information
        // TODO: [optional] b=(bandwidth information)
        // TODO: [optional] k=(encryption key)

        // TODO: [optional] a=<media attribute>:<value>
        // TODO: [optional] a=rtpmap:<attribute>:<value>

        return output.toString().trim();
    }
}