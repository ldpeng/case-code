import java.lang.*;
import java.lang.Long;
import java.text.*;
import java.util.*;

/**
 * <p>Title: 日期时间转化</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 *
 * @author ccs
 * @version 1.0
 */

public final class DateUtils
{
    /**
     * 日期时间格式
     */
    private static List<String> formats = new ArrayList<String>();

    /**
     * 默认的日期时间格式
     */
    private static String defaultFormat;

    private static String defaultDateFormat;

    private static String defaultTimeFormat;

    private static String defaultTimestampFormat;

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 一秒的毫秒数
     */
    public static final long MILLIS_IN_SECOND = 1000;

    /**
     * 一分钟的毫秒数
     */
    public static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60;

    /**
     * 一小时的毫秒数
     */
    public static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;

    /**
     * 一天的毫秒数
     */
    public static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;

    private static final List<Integer> MONTHS = CollectionUtils.range(1, 12);

    private static final List<String> HOURS;

    private static final List<String> MINUTESORSECONDS;

    static
    {
        String[] ss = new String[24];
        for (int i = 0; i < 24; i++)
        {
            String s;
            if(i < 10)
                s = "0" + i;
            else
                s = Integer.toString(i);
            ss[i] = s;
        }

        HOURS = Collections.unmodifiableList(Arrays.asList(ss));
    }

    static
    {
        String[] ss = new String[60];
        for (int i = 0; i < 60; i++)
        {
            String s;
            if(i < 10)
                s = "0" + i;
            else
                s = Integer.toString(i);
            ss[i] = s;
        }

        MINUTESORSECONDS = Collections.unmodifiableList(Arrays.asList(ss));
    }

    static
    {
        //默认添加国际通用格式

        addFormat0("yyyy-MM-dd HH:mm:ss");
        addFormat0("yyyy-MM-dd HH:mm");
        addFormat0("yy-MM-dd HH:mm:ss");
        addFormat0("yy-MM-dd HH:mm");
        addFormat0("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        addFormat0("yyyy-MM-dd'T'HH:mm:ssz");
        addFormat0("yyyy-MM-dd'T'HH:mmz");
        addFormat0("yyyy-MM-dd'T'HH:mm:ss");
        addFormat0("yyyy-MM-dd'T'HH:mm");
        addFormat0("yyyy-MM-dd");
        addFormat0("yyyyMMdd");
        addFormat0("yyyy.MM.dd");
        addFormat0("MM/dd/yyyy");
        addFormat0("yyyy/MM/dd");
        addFormat0("yy/MM/dd");
        addFormat0("HH:mm:ss");
        addFormat0("HH:mm");
        addFormat0("MM/dd/yyyy HH:mm:ss");
        addFormat0("MM/dd/yyyy HH:mm");
        addFormat0("yyyy/MM/dd HH:mm:ss");
        addFormat0("yyyy/MM/dd HH:mm");
        addFormat0("yy/MM/dd HH:mm:ss");
        addFormat0("yy/MM/dd HH:mm");
        addFormat0("yyyyMMddHHmmss");
        addFormat0("MM-dd");
        addFormat0("MM.dd");
        addFormat0("MM/dd");
        addFormat0("MMdd");
        addFormat0("yyyy-MM");
        addFormat0("yyyy.MM");
        addFormat0("yyyy/MM");
        addFormat0("yyyyMM");
        setDefaultFormat("yyyy-MM-dd HH:mm:ss");
        setDefaultDateFormat("yyyy-MM-dd");
        setDefaultTimeFormat("HH:mm:ss");
        setDefaultTimestampFormat("yyyy-MM-dd HH:mm:ss");
    }

    private DateUtils()
    {
    }

    private static void addFormat0(String format)
    {
        if(!formats.contains(format))
            formats.add(format);
    }

    /**
     * 添加一种日期时间格式
     *
     * @param format 日期格式字符串
     */
    public static synchronized void addFormat(String format)
    {
        if(!formats.contains(format))
        {
            List<String> formats = new ArrayList<String>(DateUtils.formats);
            formats.add(format);
            DateUtils.formats = formats;
        }
    }

    public static void setDefaultFormat(String format)
    {
        defaultFormat = format;
    }

    public static void setDefaultDateFormat(String format)
    {
        defaultDateFormat = format;
    }

    public static void setDefaultTimeFormat(String format)
    {
        defaultTimeFormat = format;
    }

    public static void setDefaultTimestampFormat(String format)
    {
        defaultTimestampFormat = format;
    }

    public static DateFormat getDateFormat(String format)
    {
        DateFormat dateForamt;

        //根据当前语言选择locale
        Language language = Language.getCurrentLanguage();
        if(language != null && language.getLocale() != null)
            dateForamt = new SimpleDateFormat(format, language.getLocale());
        else
            dateForamt = new SimpleDateFormat(format);

        //严格控制输入，以避免多个兼容的格式之间匹配错误
        dateForamt.setLenient(false);

        return dateForamt;
    }

    /**
     * 将时间日期转化为字符串
     *
     * @param date 要转化的日期
     * @return 日期字符串
     */
    public static String toString(Date date)
    {
        if(date == null)
            return "";

        if(date instanceof java.sql.Date)
            return getDateFormat(defaultDateFormat).format(date);
        else if(date instanceof java.sql.Time)
            return getDateFormat(defaultTimeFormat).format(date);
        else if(date instanceof java.sql.Timestamp)
            return getDateFormat(defaultTimestampFormat).format(date);

        return getDateFormat(defaultFormat).format(date);
    }

    /**
     * 格式化日期
     *
     * @param date   要格式化的日期
     * @param format 格式化字符串
     * @return 日期字符串
     */
    @SuppressWarnings("unchecked")
    public static String toString(Date date, String format)
    {
        if(date == null)
            return "";

        if(format == null)
            return toString(date);

        return getDateFormat(format).format(date);
    }

    public static String toDefaultString(Date date)
    {
        return getDateFormat(FORMAT).format(date);
    }

    /**
     * 将字符串转化为时间日期
     *
     * @param s 日期字符串
     * @return 转化后的日期时间
     */
    public static Date toDate(String s)
    {
        for (String format : formats)
        {
            try
            {
                String s1 = s;
                if(format.contains("d") && !format.contains("yy"))
                {
                    format = "yyyy " + format;
                    s1 = "2000 " + s;
                }

                return getDateFormat(format).parse(s1);
            }
            catch (ParseException ex)
            {
                //一个格式不符合，尝试下一个格式
            }
        }

        return null;
    }

    /**
     * 获得年份
     *
     * @param date 要获得年份的日期
     * @return 年份
     */
    public static int getYear(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获得当前年份
     *
     * @return 年份
     */
    public static int getYear()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    /**
     * 获得月份
     *
     * @param date 要获得月份的日期
     * @return 月份, 1月份为0，以此类推
     */
    public static int getMonth(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    /**
     * 获得当前月份
     *
     * @return 月份, 1月份为0，以此类推
     */
    public static int getMonth()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    /**
     * 获得日期
     *
     * @param date 要获得日期的日期
     * @return 日期, 1号为1，以此类推
     */
    public static int getDate(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DATE);
    }

    /**
     * 获得当前日期
     *
     * @return 日期, 1号为1，以此类推
     */
    public static int getDate()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

    /**
     * 获得星期
     *
     * @param date 要获得星期期的日期
     * @return 星期，星期天为1，星期1为2，以此类推
     */
    public static int getDay(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得当前星期
     *
     * @return 星期，星期天为1，星期1为2，以此类推
     */
    public static int getDay()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得季度
     *
     * @param date 日期
     * @return 季度，第一季度为0，第二季度为1，第三季度为2，第三季度为3
     */
    public static int getQuarter(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) / 3;
    }

    /**
     * 获得当前季度
     *
     * @return 季度，第一季度为0，第二季度为1，第三季度为2，第三季度为3
     */
    public static int getQuarter()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) / 3;
    }

    /**
     * 获得小时
     *
     * @param date 要获得小时的日期
     * @return 小时，二十时小时制，凌晨从0开始，以此类推
     */
    public static int getHour(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得当前小时
     *
     * @return 小时，二十时小时制，凌晨从0开始，以此类推
     */
    public static int getHour()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得分钟
     *
     * @param date 要获得分钟的日期
     * @return 分钟
     */
    public static int getMinute(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获得当前分钟
     *
     * @return 分钟
     */
    public static int getMinute()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获得秒钟
     *
     * @param date 日期
     * @return 秒钟
     */
    public static int getSecond(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 获得秒钟
     *
     * @return 秒钟
     */
    public static int getSecond()
    {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.SECOND);
    }

    /**
     * 对一个日期取整
     *
     * @param date 要取整的日期
     * @return 取整后的日期
     */
    public static Date truncate(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    public static Date truncate()
    {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 增加若干天
     *
     * @param date 增加前的日期
     * @param day  增加的天数
     * @return 增加后的日期
     */
    public static Date addDate(Date date, int day)
    {
        return new Date(date.getTime() + day * MILLIS_IN_DAY);
    }

    /**
     * 增加月
     *
     * @param date  增加前的日期
     * @param month 增加的月数
     * @return 增加后的日期
     */
    public static Date addMonth(Date date, int month)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 增加年
     *
     * @param date 增加前的日期
     * @param year 增加的年数
     * @return 增加后的日期
     */
    public static Date addYear(Date date, int year)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + year);

        return c.getTime();
    }

    /**
     * 增加小时
     *
     * @param date 增强前的日期
     * @param hour 增加的小时数
     * @return 增加后的日期
     */
    public static Date addHour(Date date, int hour)
    {
        return new Date(date.getTime() + hour * MILLIS_IN_HOUR);
    }

    /**
     * 增加分钟
     *
     * @param date   增强前的日期
     * @param minute 增加的分钟数
     * @return 增加后的日期
     */
    public static Date addMinute(Date date, int minute)
    {
        return new Date(date.getTime() + minute * MILLIS_IN_MINUTE);
    }

    /**
     * 增加秒钟
     *
     * @param date  增强前的日期
     * @param scond 增加的分钟数
     * @return 增加后的日期
     */
    public static Date addSecond(Date date, int scond)
    {
        return new Date(date.getTime() + scond * MILLIS_IN_SECOND);
    }

    /**
     * 增加星期
     *
     * @param date 增加前的日期
     * @param week 增加的星期数
     * @return 增加后的日期
     */
    public static Date addWeek(Date date, int week)
    {
        return addDate(date, week * 7);
    }

    /**
     * 增加季度
     *
     * @param date    增加前的日期
     * @param quarter 增加的季度数
     * @return 增加后的日期
     */
    public static Date addQuarter(Date date, int quarter)
    {
        return addMonth(date, quarter * 3);
    }

    /**
     * 获得一周开始日期
     *
     * @param date     日期
     * @param startDay 从哪一天开始算一星期的开始，0表示星期天，1表示星期一
     * @return 周的开始
     */
    public static Date getWeekStart(Date date, int startDay)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int offset = c.get(Calendar.DAY_OF_WEEK) - 1 - startDay;
        if(offset < 0)
            offset += 7;

        return new Date(c.getTimeInMillis() - offset * MILLIS_IN_DAY);
    }

    /**
     * 获得一周开始日期，星期天为每周第一天
     *
     * @param date 日期
     * @return 周的开始
     */
    public static Date getWeekStart(Date date)
    {
        return getWeekStart(date, 0);
    }

    /**
     * 获得一周结束
     *
     * @param date     日期
     * @param startDay 从哪一天开始算一星期的开始，0表示星期天，1表示星期一
     * @return 周的结束
     */
    public static Date getWeekEnd(Date date, int startDay)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        int offset = 8 - c.get(Calendar.DAY_OF_WEEK) + startDay;
        if(offset > 7)
            offset -= 7;

        return new Date(c.getTimeInMillis() + offset * MILLIS_IN_DAY);
    }

    /**
     * 获得一周结束，星期天为每周第一天
     *
     * @param date 日期
     * @return 周的结束
     */
    public static Date getWeekEnd(Date date)
    {
        return getWeekEnd(date, 0);
    }

    /**
     * 获得当月的开始
     *
     * @param date 日期
     * @return 月开始
     */
    public static Date getMonthStart(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);

        return c.getTime();
    }

    /**
     * 获得当月的结束
     *
     * @param date 日期
     * @return 月的结束
     */
    public static Date getMonthEnd(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.add(Calendar.MONTH, 1);

        return c.getTime();
    }

    /**
     * 获得一月的开始
     *
     * @param year  年度
     * @param month 月份，0为1月份，以此类推
     * @return 年的结束
     */
    public static Date getMonthStart(int year, int month)
    {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 获得一月的结束
     *
     * @param year  年份
     * @param month 月份，0为1月份，以此类推
     * @return 年的开始
     */
    public static Date getMonthEnd(int year, int month)
    {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, month + 1);

        return c.getTime();
    }

    /**
     * 获得季度的开始
     *
     * @param date 日期
     * @return 季度的开始
     */
    public static Date getQuarterStart(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);

        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month - (month % 3));

        return c.getTime();
    }

    /**
     * 获得季度的结束
     *
     * @param date 日期
     * @return 季度结束
     */
    public static Date getQuerterEnd(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);

        int month = c.get(Calendar.MONTH);
        c.set(Calendar.MONTH, month + 3 - (month % 3));

        return c.getTime();
    }

    /**
     * 获得一年的开始
     *
     * @param date 日期
     * @return 年的开始
     */
    public static Date getYearStart(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, 0);

        return c.getTime();
    }

    /**
     * 获得一年的开始
     *
     * @param year 年份
     * @return 年的开始
     */
    public static Date getYearStart(int year)
    {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, 0);

        return c.getTime();
    }

    /**
     * 获得一年的结束
     *
     * @param date 日期
     * @return 年的结束
     */
    public static Date getYearEnd(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);

        return c.getTime();
    }

    /**
     * 获得一年的结束
     *
     * @param year 年份
     * @return 年的开始
     */
    public static Date getYearEnd(int year)
    {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, 0);

        return c.getTime();
    }

    /**
     * 获得半年开始
     *
     * @param date 日期
     * @return 半年的开始
     */
    public static Date getHalfYearStart(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);

        int month = c.get(Calendar.MONTH);
        if(month < 6)
            month = 0;
        else
            month = 6;
        c.set(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 获得半年结束
     *
     * @param date 日期
     * @return 半年的结束
     */
    public static Date getHalfYearEnd(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DATE, 1);

        int month = c.get(Calendar.MONTH);
        if(month < 6)
        {
            c.set(Calendar.MONTH, 6);
        }
        else
        {
            c.set(Calendar.MONTH, 0);
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        }

        return c.getTime();
    }

    /**
     * 获得一个月份的最大天数
     *
     * @param year  年度
     * @param month 月份
     * @return 此月的最大天数
     */
    public static int getMaxDate(int year, int month)
    {
        if(month == 2)
        {
            //2月，分闰年和平年
            if((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
                return 29;
            else
                return 28;
        }

        if(month == 4 || month == 6 || month == 9 || month == 11)
            return 30;

        return 31;
    }

    public static String getDefaultFormat()
    {
        return defaultFormat;
    }

    public static String getDefaultDateFormat()
    {
        return defaultDateFormat;
    }

    public static String getDefaultTimeFormat()
    {
        return defaultTimeFormat;
    }

    public static String getDefaultTimestampFormat()
    {
        return defaultTimestampFormat;
    }

    public static DateFormat getGMTFormat()
    {
        return new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
    }

    public static List<Integer> getMonths()
    {
        return MONTHS;
    }

    public static List<Integer> getDates(int year, int month)
    {
        return CollectionUtils.range(1, getMaxDate(year, month));
    }

    public static List<Integer> getYears(int start, int end, boolean desc)
    {
        if(end == 0)
            end = getYear();

        if(start < 0)
            start = end + start;

        if(start == end)
            return Collections.singletonList(start);

        if(desc)
            return CollectionUtils.range(end, start, -1);
        else
            return CollectionUtils.range(start, end);
    }

    public static List<Integer> getYears(int start, boolean desc)
    {
        return getYears(start, getYear(), desc);
    }

    public static List<Integer> getYears(int start, int end)
    {
        if(start < end)
            return getYears(start, end, false);
        else
            return getYears(end, start, true);
    }

    public static List<Integer> getYears(int start)
    {
        return getYears(start, false);
    }

    public static List<String> getHours()
    {
        return HOURS;
    }

    public static List<String> getMinutes()
    {
        return MINUTESORSECONDS;
    }

    public static List<String> getSeconds()
    {
        return MINUTESORSECONDS;
    }

    public static String getTimeSizeString(long time)
    {
        return getTimeSizeString(time, true);
    }

    public static String getTimeSizeString(long time, boolean showDay)
    {
        StringBuilder buffer = new StringBuilder();

        if(showDay)
        {
            long day = time / MILLIS_IN_DAY;
            if(day > 0)
            {
                buffer.append(day).append("day");

                time = time % MILLIS_IN_DAY;
            }
        }

        long hour = time / MILLIS_IN_HOUR;
        if(hour > 0)
        {
            buffer.append(hour).append("hour");

            time = time % MILLIS_IN_HOUR;
        }

        long minute = time / MILLIS_IN_MINUTE;
        if(minute > 0)
        {
            buffer.append(minute).append("min");

            time = time % MILLIS_IN_MINUTE;
        }

        long second = time / MILLIS_IN_SECOND;
        if(second > 0 || buffer.length() == 0)
        {
            buffer.append(second).append("sec");
        }

        return buffer.toString();
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static long daysBetween(Date smdate, Date bdate) throws Exception
    {
        if(smdate == null || bdate == null) return 0;

        long time1 = truncate(smdate).getTime();
        long time2 = truncate(bdate).getTime();

        return (time2 - time1) / (1000 * 3600 * 24);
    }
}
