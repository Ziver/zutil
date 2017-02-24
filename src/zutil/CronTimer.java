package zutil;


import com.mysql.fabric.xmlrpc.base.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a utility class that will generate timestamps from a Cron formatted String.
 *
 * @see <a hraf="http://www.nncron.ru/help/EN/working/cron-format.htm">Cron Format Specification</a>
 */
public class CronTimer {


    /**
     * A Constructor that takes a String containing 5 (or 6 for extended) individual fields in Cron format
     */
    public CronTimer(String cron){
        String[] arr = cron.split("\\s");
        if (arr.length < 5 || arr.length > 6)
            throw new IllegalArgumentException(
                    "String must contain between 5-6 fields, but got("+arr.length+" fields): "+cron);
        init(arr[0], arr[1], arr[2], arr[3], arr[4], (arr.length>5 ? arr[5]: "*"));
    }
    /**
     * A Constructor that takes separate Strings for each field
     */
    public CronTimer(String minute, String hour, String dayOfMonth, String monthOfYear, String dayOfWeek){
        this(minute, hour, dayOfMonth, monthOfYear, dayOfWeek, "*");
    }
    /**
     * A Constructor that takes separate Strings for each field with an extended year field
     */
    public CronTimer(String minute, String hour, String dayOfMonth, String monthOfYear, String dayOfWeek, String year){
        init(minute, hour, dayOfMonth, monthOfYear, dayOfWeek, year);
    }

    private void init(String minute, String hour, String dayOfMonth, String monthOfYear, String dayOfWeek, String year){

    }
    protected static List<Integer> getRange(String number){
        ArrayList<Integer> list = new ArrayList<>();
        
        return list;
    }
}
