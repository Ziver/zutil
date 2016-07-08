package zutil.net.http.multipart;


import zutil.io.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;


/**
 * Created by Ziver on 2016-07-06.
 */
public class MultipartStringField implements MultipartField {
    private String name;
    private String value;

    protected MultipartStringField(Map<String,String> header, BufferedReader in) throws IOException {
        this.name = header.get("name");
        value = IOUtil.getContentAsString(in);
    }

    @Override
    public long getLength() {
        return value.length();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
