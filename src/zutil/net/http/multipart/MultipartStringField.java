package zutil.net.http.multipart;

import java.io.InputStream;

/**
 * Created by ezivkoc on 2016-07-06.
 */
public class MultipartStringField implements MultipartField {
    private String value;

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    public String getValue() {
        return value;
    }
}
