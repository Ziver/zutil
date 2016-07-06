package zutil.net.http.multipart;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * JUnit test for MultipartParser class
 *
 * Created by Ziver on 2016-07-06.
 */
public class MultipartParserTest {

    @Test
    public void singleFormDataField(){
        String input =
                        "------------------------------83ff53821b7c\n" +
                        "Content-Disposition: form-data; name=\"foo\"\n" +
                        "\n" +
                        "bar\n" +
                        "------------------------------83ff53821b7c--";
        MultipartParser parser = new MultipartParser(
                new BufferedReader(new StringReader(input)),
                "----------------------------83ff53821b7c",
                input.length());

        int count = 0;
        for(MultipartField field : parser){
            assertEquals(0, count);
            if (field instanceof MultipartStringField) {
                MultipartStringField stringField = (MultipartStringField)field;
                assertEquals("foo", stringField.getName());
                assertEquals("bar", stringField.getValue());
            }
            else fail("Field is not an instance of "+MultipartStringField.class);
            ++count;
        }
    }

    //@Test
    public void singleFileUpload(){
        String input =
                "------------------------------83ff53821b7c\n" +
                "Content-Disposition: form-data; name=\"img\"; filename=\"a.png\"\n" +
                "Content-Type: application/octet-stream\n" +
                "\n" +
                "?PNG\n" +
                "\n" +
                "IHD?wS??iCCPICC Profilex?T?kA?6n??Zk?x?\"IY?hE?6?bk\n" +
                "Y?<ߡ)??????9Nyx?+=?Y\"|@5-?\u007FM?S?%?@?H8??qR>?\u05CB??inf???O?????b??N?????~N??>?!?\n" +
                "??V?J?p?8?da?sZHO?Ln?}&???wVQ?y?g????E??0\n" +
                " ??\n" +
                "   IDAc????????-IEND?B`?\n" +
                "------------------------------83ff53821b7c--";
        MultipartParser parser = new MultipartParser(
                new BufferedReader(new StringReader(input)),
                "----------------------------83ff53821b7c",
                input.length());

        // TODO
    }

    //Test
    public void multiFileUpload(){
        String input =
                "   --AaB03x\n" +
                "   Content-Disposition: form-data; name=\"submit-name\"\n" +
                "\n" +
                "   Larry\n" +
                "   --AaB03x\n" +
                "   Content-Disposition: form-data; name=\"files\"\n" +
                "   Content-Type: multipart/mixed; boundary=BbC04y\n" +
                "\n" +
                "   --BbC04y\n" +
                "   Content-Disposition: file; filename=\"file1.txt\"\n" +
                "   Content-Type: text/plain\n" +
                "\n" +
                "   ... contents of file1.txt ...\n" +
                "   --BbC04y\n" +
                "   Content-Disposition: file; filename=\"file2.gif\"\n" +
                "   Content-Type: image/gif\n" +
                "   Content-Transfer-Encoding: binary\n" +
                "\n" +
                "   ...contents of file2.gif...\n" +
                "   --BbC04y--\n" +
                "   --AaB03x--";
        MultipartParser parser = new MultipartParser(
                new BufferedReader(new StringReader(input)),
                "AaB03x",
                input.length());
    }
}
