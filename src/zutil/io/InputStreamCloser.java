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

package zutil.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple Class that mirrors a InputStream but
 * also has an additional Closeable objects that
 * will be closed when the this object is closed.
 *
 * @author Ziver
 */
public class InputStreamCloser extends InputStream{
    private Closeable[] c;
    private InputStream in;

    public InputStreamCloser(InputStream in, Closeable... c) {
        this.c = c;
        this.in = in;
    }

    public void close() throws IOException {
        in.close();
        for (Closeable stream : c)
            stream.close();
    }

    // Mirror functions
    public int read() throws IOException                { return in.read(); }
    public int read(byte b[]) throws IOException        { return in.read(b); }
    public int read(byte b[], int off, int len) throws IOException { return in.read(b, off, len); }
    public long skip(long n) throws IOException         { return in.skip(n); }
    public int available() throws IOException           { return in.available(); }
    public synchronized void mark(int readlimit)        { in.mark(readlimit); }
    public synchronized void reset() throws IOException { in.reset(); }
    public boolean markSupported()                      { return in.markSupported(); }
}