package zutil.net.http.multipart;


import java.util.HashMap;

/**
 * Created by Ziver on 2016-07-06.
 */
public class MultipartStringField implements MultipartField {
    private String name;
    private String value;

    protected MultipartStringField(String name){
        this.name = name;
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
