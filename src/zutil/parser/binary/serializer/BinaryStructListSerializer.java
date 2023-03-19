package zutil.parser.binary.serializer;

import zutil.parser.binary.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * This serializer handles a List field that contains the same type of objects.
 * This class needs to be extended by a more specific subclass that can provide
 * the list class type and handling the flow control of the read action.
 *
 * @param <T> defines the class type of the items in the List.
 */
public abstract class BinaryStructListSerializer<T extends BinaryStruct> implements BinaryFieldSerializer<List<T>> {

    private Class<T> listClass;


    protected BinaryStructListSerializer(Class<T> clazz) {
        listClass = clazz;
    }


    @Override
    public List<T> read(InputStream in, BinaryFieldData field) throws IOException {
        return null;
    }

    @Override
    public List<T> read(InputStream in, BinaryFieldData field, Object parentObject) throws IOException {
        BinaryStructInputStream structIn = new BinaryStructInputStream(in);

        List<T> list = new ArrayList<>();
        try {
            int bytesRead = 0;
            while (readNext(list.size(), bytesRead, field, parentObject)) {
                T obj = listClass.getDeclaredConstructor().newInstance();
                bytesRead += structIn.read(obj);
                list.add(obj);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Method is used to determine if the next object should be read from the stream.
     *
     * @param objIndex  the number of objects that have been read so far.
     * @param bytesRead number of bytes that have been read from the stream so far.
     * @param field        meta-data about the target field that will be assigned.
     * @param parentObject the parent object that owns the field.
     * @return true if another object can be read from the stream, false if this is the end of the struct field.
     */
    protected abstract boolean readNext(int objIndex, int bytesRead, BinaryFieldData field, Object parentObject);

    @Override
    public void write(OutputStream out, List<T> list, BinaryFieldData field) throws IOException {
        BinaryStructOutputStream structOut = new BinaryStructOutputStream(out);

        for (T obj : list) {
            structOut.write(obj);
        }
    }
}
