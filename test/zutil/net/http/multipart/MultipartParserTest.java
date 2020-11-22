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

package zutil.net.http.multipart;

import org.junit.Test;
import zutil.io.StringInputStream;

import java.io.IOException;
import java.util.Iterator;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * JUnit test for MultipartParser class
 * <p>
 * Created by Ziver on 2016-07-06.
 */
public class MultipartParserTest {

    @Test
    public void singleFormDataField() {
        String input =
                "------------------------------83ff53821b7c\n" +
                "Content-Disposition: form-data; name=\"foo\"\n" +
                "\n" +
                "bar\n" +
                "------------------------------83ff53821b7c--";
        MultipartParser parser = new MultipartParser(
                new StringInputStream(input),
                "----------------------------83ff53821b7c",
                input.length());

        // Assertions
        Iterator<MultipartField> it = parser.iterator();

        assertTrue(it.hasNext());
        MultipartField field = it.next();
        assertTrue(field instanceof MultipartStringField);
        MultipartStringField stringField = (MultipartStringField) field;
        assertEquals("foo", stringField.getName());
        assertEquals("bar", stringField.getValue());

        assertFalse(it.hasNext());
        assertNull(it.next());
        assertNull(it.next());
    }

    @Test
    public void singleFileUpload() throws IOException {
        String input_start =
                "------------------------------83ff53821b7c\n" +
                "Content-Disposition: form-data; name=\"img\"; filename=\"a.png\"\n" +
                "Content-Type: application/octet-stream\n" +
                "\n";
        String input_data =
                "?PNG\n" +
                "\n" +
                "lkuytrewacvbnmloiuytrewrtyuiol,mnbvdc xswertyuioplm cdsertyuiojlkjgf\n" +
                "kgkfdgfhgfhgkhgvytvjgxslkysiyfedgjdjhfkjhdlgdgjfhfcjhfqiyfyudlmgfeudcfa\n" +
                "bgdljgdjhffjhfdfsgdfg  ryrt dtrd ytfc  uhhiugljfkdkhdjgd\n" +
                " xx\n" +
                "     kuykutfytdh ytrd trutrd trxxx";
        String input_end = "\n------------------------------83ff53821b7c--";
        String input = input_start+input_data+input_end;
        MultipartParser parser = new MultipartParser(
                new StringInputStream(input),
                "----------------------------83ff53821b7c",
                0);

        // Assertions
        Iterator<MultipartField> it = parser.iterator();

        assertTrue(it.hasNext());
        MultipartField field = it.next();
        assertTrue(field instanceof MultipartFileField);
        MultipartFileField fileField = (MultipartFileField) field;
        assertEquals("img", fileField.getName());
        assertEquals("a.png", fileField.getFilename());
        assertEquals("application/octet-stream", fileField.getContentType());
        assertEquals(input_data, new String(fileField.getContent()));

        assertFalse(it.hasNext());
    }

    //Test
    public void multiFileUpload() {
        String input =
                "--AaB03x\n" +
                "Content-Disposition: form-data; name=\"submit-name\"\n" +
                "\n" +
                "Larry\n" +
                "--AaB03x\n" +
                "Content-Disposition: form-data; name=\"files\"\n" +
                "Content-Type: multipart/mixed; boundary=BbC04y\n" +
                "\n" +
                "--BbC04y\n" +
                "Content-Disposition: file; filename=\"file1.txt\"\n" +
                "Content-Type: text/plain\n" +
                "\n" +
                "... contents of file1.txt ...\n" +
                "--BbC04y\n" +
                "Content-Disposition: file; filename=\"file2.gif\"\n" +
                "Content-Type: image/gif\n" +
                "Content-Transfer-Encoding: binary\n" +
                "\n" +
                "...contents of file2.gif...\n" +
                "--BbC04y--\n" +
                "--AaB03x--";
        MultipartParser parser = new MultipartParser(
                new StringInputStream(input),
                "AaB03x",
                input.length());
    }
}
