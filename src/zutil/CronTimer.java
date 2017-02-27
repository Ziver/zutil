package zutil;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * This is a utility class that will generate timestamps from a Cron formatted String.
 *
 * @see <a hraf="http://www.nncron.ru/help/EN/working/cron-format.htm">Cron Format Specification</a>
 */
public class CronTimer implements Iterator<Long>, Iterable<Long>{

    private int[] minutes;
    private int[] hours;
    private int[] dayOfMonths;
    private int[] months;
    private int[] dayOfWeeks;
    private int[] years;


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
        minutes = ArrayUtil.toIntArray(getRange(minute, 0, 59));
        hours = ArrayUtil.toIntArray(getRange(hour, 0, 23));
        dayOfMonths = ArrayUtil.toIntArray(getRange(dayOfMonth, 1, 31));
        months = ArrayUtil.toIntArray(getRange(monthOfYear, 1, 12));
        dayOfWeeks = ArrayUtil.toIntArray(getRange(dayOfWeek, 1, 7));
        years = ArrayUtil.toIntArray(getRange(year,
                1970,
                Calendar.getInstance().get(Calendar.YEAR)+30));
    }
    protected static List<Integer> getRange(String str, int from, int to){
        if (str == null || str.isEmpty())
            return Collections.emptyList();

        List<Integer> list = new LinkedList<>();

        String[] commaArr = str.split(",");
        if (commaArr.length > 1){
            for (String section : commaArr)
                list.addAll(getRange(section, from, to));
        }
        else {
            String[] divisionArr = str.split("/", 2);
            if (divisionArr.length == 2) {
                float divider = Integer.parseInt(divisionArr[1]);
                Iterator<Integer> it = getRange(divisionArr[0], from, to).iterator();
                while (it.hasNext()) {
                    Integer i = it.next();
                    if (i%divider == 0)
                        list.add(i);
                }
            }
            else {
                String[] rangeArr;
                if (str.equals("*"))
                    rangeArr = new String[]{""+from, ""+to};
                else
                    rangeArr = str.split("-", 2);
                if (rangeArr.length == 2) {
                    int rangeFrom = Integer.parseInt(rangeArr[0]);
                    int rangeTo = Integer.parseInt(rangeArr[1]);
                    for (int i = rangeFrom; i <= rangeTo; ++i)
                        list.add(i);
                } else {
                    list.add(Integer.parseInt(str));
                }
            }
        }
        return list;
    }



    @Override
    public boolean hasNext() {
        return true;
    }
    @Override
    public Iterator<Long> iterator() {
        return this;
    }
    @Override
    public void remove() {
        throw new NotImplementedException();
    }

    /**
     * @return the next timestamp that triggers this cron timer from now,
     *          -1 if there is no more future trigger points.
     */
    @Override
    public Long next() {
        return next(System.currentTimeMillis());
    }
    /**
     * @param   fromTimestamp   the timestamp offset to check the trigger from. Should be in MS
     * @return the next timestamp that triggers this cron timer from the given timestamp,
     *          -1 if there is no more future trigger points.
     */
    public Long next(long fromTimestamp) {
        Calendar cal = getCalendar(fromTimestamp);

        int minute = cal.get(Calendar.MINUTE);
        int index = Arrays.binarySearch(minutes, minute);
        if (index < 0){ // not found in array
            if (Math.abs(index) > minutes.length) {
                cal.set(Calendar.MINUTE, minutes[0]);
                cal.add(Calendar.HOUR_OF_DAY, 1);
            } else
                cal.set(Calendar.MINUTE, minutes[Math.abs(index+1)]);
        }

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        index = Arrays.binarySearch(hours, hour);
        if (index < 0){ // not found in array
            if (Math.abs(index) > hours.length) {
                cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            } else
                cal.set(Calendar.HOUR_OF_DAY, hours[Math.abs(index+1)]);
        }

        int day = cal.get(Calendar.DAY_OF_MONTH);
        index = Arrays.binarySearch(dayOfMonths, day);
        if (index < 0){ // index not found in array
            // check if month have that many days in it
            if (Math.abs(index) > dayOfMonths.length ||
                    dayOfMonths[Math.abs(index+1)] > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[0]);
                cal.add(Calendar.MONTH, 1);
            } else
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[Math.abs(index+1)]);
        }

        int month = cal.get(Calendar.MONTH);
        index = Arrays.binarySearch(months, month+1);
        if (index < 0){ // index not found in array
            if (Math.abs(index) > months.length) {
                cal.set(Calendar.MONTH, months[0]-1);
                cal.add(Calendar.YEAR, 1);
            } else
                cal.set(Calendar.MONTH, months[Math.abs(index+1)]-1);
        }
/*
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        index = Arrays.binarySearch(dayOfWeeks, dayOfWeek);
        if (index < 0){ // index not found in array
            if (Math.abs(index) > dayOfWeeks.length) {
                cal.set(Calendar.DAY_OF_WEEK, dayOfWeeks[0]);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
            } else
                cal.set(Calendar.DAY_OF_WEEK, dayOfWeeks[Math.abs(index+1)]);
        }
*/
        int year = cal.get(Calendar.YEAR);
        index = Arrays.binarySearch(years, year);
        if (index < 0){ // index not found in array
            if (Math.abs(index) > years.length)
                return -1L;
            else
                cal.set(Calendar.YEAR, years[Math.abs(index+1)]);
        }

        return cal.getTimeInMillis();
    }

    protected Calendar getCalendar(long timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal;
    }
}
