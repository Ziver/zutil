package zutil.net.dns.packet;

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A serializer class that can read and write a DNS FQDN in binary format.
 */
public class FQDNStringSerializer implements BinaryFieldSerializer<String> {

    public String read(InputStream in, BinaryFieldData field) throws IOException {
        StringBuilder str = new StringBuilder();
        int c = in.read();
        // Is this a pointer
        if ((c & 0b1100_0000) == 0b1100_0000) {
            int offset = (c & 0b0011_1111) << 8;
            offset |= in.read() & 0b1111_1111;
            str.append(offset);
        }
        // Normal Domain String
        else {
            while (c > 0) {
                for (int i = 0; i < c; ++i) {
                    str.append((char) in.read());
                }
                c = in.read();
                if (c > 0)
                    str.append('.');
            }
        }
        return str.toString();
    }

    public void write(OutputStream out, String domain, BinaryFieldData field) throws IOException {
        if (domain != null) {
            String[] labels = domain.split("\\.");
            for (String label : labels) {
                out.write(label.length());
                out.write(label.getBytes());
            }
        }
        out.write(0);
    }

}
