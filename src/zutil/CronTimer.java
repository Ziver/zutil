package zutil;

import java.util.*;

/**
 * This is a utility class that will generate timestamps from a Cron formatted String.
 * <br>
 * A cron string consists of 5 to 6 sections separated by a space:
 * <ol>
 *     <li>Minute in Hour</li>
 *     <li>Hour in a Day</li>
 *     <li>Day of Month</li>
 *     <li>Month</li>
 *     <li>Day of Week</li>
 *     <li>Year</li>
 * </ol>
 * Each section is defined by a number or by special characters:
 * <ul>
 *     <li>*: any value, wildcard</li>
 *     <li>,: separator for multiple expressions, e.g. 3,4,5</li>
 *     <li>-: defines a range, e.g. 3-6</li>
 *     <li>&#47;: step values, e.g. *&#47;10 (every tenth)</li>
 *     <li>?: sets value as start time, when the cron was initialized. NOT SUPPORTED</li>
 * </ul>
 * <br>
 * Examples (from Cron Format Specification):
 * <ul>
 *     <li>"* * * * * *": Each minute</li>
 *     <li>"0 0 * * * *": Daily at midnight</li>
 *     <li>"* * * 1,2,3 * *": Each minute in January, February or March</li>
 * </ul>
 * <br>
 * Note that this class will only calculate the next cron up to 50 years in the future ant not more.
 *
 * @see <a href="http://www.nncron.ru/help/EN/working/cron-format.htm">Cron Format Specification</a>
 * @see <a href="https://crontab.guru/">Cron calculator</a>
 * @see <a href="http://stackoverflow.com/a/322058/833746">Stackoverflow implementation reference</a>
 */
public class CronTimer implements Iterator<Long>, Iterable<Long>{

    private TimeZone timeZone;

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
                    if (from > rangeFrom || rangeTo > to)
                        throw new IllegalArgumentException("Invalid range "+rangeFrom+"-"+rangeTo+" must be between: "+from+"-"+to);
                    for (int i = rangeFrom; i <= rangeTo; ++i)
                        list.add(i);
                } else {
                    int value = Integer.parseInt(str);
                    if (from > value || value > to)
                        throw new IllegalArgumentException("Valid values are between "+from+"-"+to+" but got: "+value);
                    list.add(value);
                }
            }
        }
        return list;
    }


    /**
     * Set the TimeZone that should be used by the cron algorithm
     */
    public void setTimeZone(TimeZone timeZone){
        this.timeZone = timeZone;
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
        throw new UnsupportedOperationException();
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
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MINUTE, 1); // skipp current time

        while (true) {
            int index;

            int year = cal.get(Calendar.YEAR);
            index = Arrays.binarySearch(years, year);
            if (index < 0) { // index not found in array
                if (Math.abs(index) > years.length)
                    return -1L; // We have reach the limit no more years left
                else
                    cal.set(Calendar.YEAR, years[Math.abs(index + 1)]);
                cal.set(Calendar.MONTH, months[0] - 1);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[0]);
                cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                cal.set(Calendar.MINUTE, minutes[0]);
                continue;
            }

            int month = cal.get(Calendar.MONTH); // month ids are between 0-11 :(
            index = Arrays.binarySearch(months, month + 1);
            if (index < 0) { // index not found in array
                if (Math.abs(index) > months.length) {
                    cal.set(Calendar.MONTH, months[0] - 1);
                    cal.add(Calendar.YEAR, 1);
                } else
                    cal.set(Calendar.MONTH, months[Math.abs(index + 1)] - 1);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[0]);
                cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                cal.set(Calendar.MINUTE, minutes[0]);
                continue;
            }

            int day = cal.get(Calendar.DAY_OF_MONTH);
            index = Arrays.binarySearch(dayOfMonths, day);
            if (index < 0) { // index not found in array
                if (Math.abs(index) > dayOfMonths.length ||
                        // check if month have that many days in it
                        dayOfMonths[Math.abs(index + 1)] > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[0]);
                    cal.add(Calendar.MONTH, 1);
                } else
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonths[Math.abs(index + 1)]);
                cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                cal.set(Calendar.MINUTE, minutes[0]);
                continue;
            }

            // Calendar DAY_OF_WEEK is weird so we need to convert it to a logical number
            int dayOfWeek = getDayOfWeekID(cal.get(Calendar.DAY_OF_WEEK));
            index = Arrays.binarySearch(dayOfWeeks, dayOfWeek);
            if (index < 0) { // index not found in array
                if (Math.abs(index) > dayOfWeeks.length) {
                    cal.set(Calendar.DAY_OF_WEEK, getDayOfWeekEnum(dayOfWeeks[0]));
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                } else
                    cal.set(Calendar.DAY_OF_WEEK, getDayOfWeekEnum(dayOfWeeks[Math.abs(index + 1)]));
                cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                cal.set(Calendar.MINUTE, minutes[0]);
                continue;
            }

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            index = Arrays.binarySearch(hours, hour);
            if (index < 0) { // not found in array
                if (Math.abs(index) > hours.length) {
                    cal.set(Calendar.HOUR_OF_DAY, hours[0]);
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                } else
                    cal.set(Calendar.HOUR_OF_DAY, hours[Math.abs(index + 1)]);
                cal.set(Calendar.MINUTE, minutes[0]);
                continue;
            }

            int minute = cal.get(Calendar.MINUTE);
            index = Arrays.binarySearch(minutes, minute);
            if (index < 0) { // not found in array
                if (Math.abs(index) > minutes.length) {
                    cal.set(Calendar.MINUTE, minutes[0]);
                    cal.add(Calendar.HOUR_OF_DAY, 1);
                } else
                    cal.set(Calendar.MINUTE, minutes[Math.abs(index + 1)]);
                continue;
            }

            // If we reach the end that means that we got match for all parameters
            break;
        }
        return cal.getTimeInMillis();
    }

    protected Calendar getCalendar(long timestamp){
        Calendar cal = Calendar.getInstance();
        if (timeZone != null)
            cal.setTimeZone(timeZone);
        cal.setTimeInMillis(timestamp);
        return cal;
    }

    /**
     * Converts Calendar DAY_OF_WEEK enum to id starting from 1 (Monday) to 7 (Sunday)
     */
    private int getDayOfWeekID(int calDayOfWeek){
        switch (calDayOfWeek){
            case Calendar.MONDAY:    return 1;
            case Calendar.TUESDAY:   return 2;
            case Calendar.WEDNESDAY: return 3;
            case Calendar.THURSDAY:  return 4;
            case Calendar.FRIDAY:    return 5;
            case Calendar.SATURDAY:  return 6;
            case Calendar.SUNDAY:    return 7;
        }
        return -1;
    }
    private int getDayOfWeekEnum(int dayId){
        switch (dayId){
            case 1: return Calendar.MONDAY;
            case 2: return Calendar.TUESDAY;
            case 3: return Calendar.WEDNESDAY;
            case 4: return Calendar.THURSDAY;
            case 5: return Calendar.FRIDAY;
            case 6: return Calendar.SATURDAY;
            case 7: return Calendar.SUNDAY;
        }
        return -1;
    }
}
