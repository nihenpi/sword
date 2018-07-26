package com.skaz.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Dates {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINITE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS   = MINITE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS    = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS   = DAY_IN_MILLIS * 7;

    public static final String FORMAT_DATE                 = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME             = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_T_TIME          = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_DATETIME_SSS         = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_DATE_T_TIME_SSS      = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String FORMAT_ISO                  = "yyyy-MM-dd'T'HH:mm:ss'Z'";       // ISO8601
    public static final String FORMAT_ISO_SSS              = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";   // ISO8601.SSS
    public static final String FORMAT_NONE                 = "yyyyMMddHHmmssSSS";
    public static final String FORMAT_DATE_NONE            = "yyyyMMdd";
    public static final String FORMAT_TIME_NONE            = "HHmmss";
    public static final String FORMAT_DATETIME_NONE        = "yyyyMMddHHmm";
    public static final String FORMAT_DATETIME_NONE_SS     = "yyyyMMddHHmmss";
    public static final String FORMAT_DATETIME_NONE_SS_SSS = "yyyyMMddHHmmssSSS";

    public static final String STD_DATE_PATTERN     = FORMAT_DATE;
    public static final String STD_TIME_PATTERN     = "HH:mm:ss";
    public static final String STD_DATETIME_PATTERN = FORMAT_DATETIME;

    public static final String W3C_DATETIME_PATTERN         = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     * Date对象自带toString的格式
     */
    public static final String FORMAT_DATE_TOSTRING_DEFAULT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String RFC822_DATETIME_PATTERN      = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    //@formatter:off
    private static final String[] RFC822_PATTENRS = {
            "EEE, dd MMM yy HH:mm:ss z",
            "EEE, dd MMM yy HH:mm z",
            "dd MMM yy HH:mm:ss z",
            "dd MMM yy HH:mm z",
    };

    private static final String[] W3CDATETIME_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSz",
            "yyyy-MM-dd't'HH:mm:ss.SSSz",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd't'HH:mm:ss.SSS'z'",
            "yyyy-MM-dd'T'HH:mm:ssz",
            "yyyy-MM-dd't'HH:mm:ssz",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd't'HH:mm:ss'z'",
            "yyyy-MM-dd'T'HH:mmz",
            "yyyy-MM'T'HH:mmz",
            "yyyy'T'HH:mmz",
            "yyyy-MM-dd't'HH:mmz",
            "yyyy-MM-dd'T'HH:mm'Z'",
            "yyyy-MM-dd't'HH:mm'z'",
            "yyyy-MM-dd",
            "yyyy-MM",
            "yyyy",
    };

    private static final String[] STD_PATTERNS = {
            "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd HH:mm:ss,SSS",
            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM/dd",
            "yyyyMMddHHmmss",
            "yyyyMMdd",
            "hh:mm:ss,SSS",
            "hh:mm:ss.SSS",
            "hh:mm:ss",
    };
    //@formatter:on

    public static final String DEFAULT_FORMAT = FORMAT_DATETIME;

    private Dates() {
    }

    public static Date newDate() {
        return new Date();
    }

    public static Date newDate(long date) {
        return new Date(date);
    }

    public static Date newDate(int year, int month, int day) {
        return newLocalDate(year, month, day).toDate();
    }

    /**
     * 根据指定的格式转换字符串为Date类型
     */
    public static Date newDate(String date, String format) {
        Date d = null;
        try {
            if (format == null) {
                if (isFormat(date, FORMAT_DATE_TOSTRING_DEFAULT)) {
                    format = FORMAT_DATE_TOSTRING_DEFAULT;
                } else if (isFormat(date, FORMAT_DATE)) {
                    format = FORMAT_DATE;
                } else if (isFormat(date, FORMAT_DATETIME) || isFormat(date, FORMAT_DATE_T_TIME)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME;
                    } else {
                        format = FORMAT_DATETIME;
                    }
                } else if (isFormat(date, FORMAT_DATETIME_SSS) || isFormat(date, FORMAT_DATE_T_TIME_SSS)) {
                    if (Strings.isContains(date, "T")) {
                        format = FORMAT_DATE_T_TIME_SSS;
                    } else {
                        format = FORMAT_DATETIME_SSS;
                    }
                } else if (isFormat(date, FORMAT_ISO)) {
                    format = FORMAT_ISO;
                } else if (isFormat(date, FORMAT_ISO_SSS)) {
                    format = FORMAT_ISO_SSS;
                } else {
                    format = FORMAT_NONE;
                }
            }
            d = newDateFormat(format, Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            throw Exceptions.unchecked(e);
        }
        return d;
    }

    public static Date newDate(String date) {
        return newDate(date, null);
    }

    public static Date newDateTime(String date) {
        return newDate(date, FORMAT_DATETIME);
    }

    public static Date newDateWithDateTimeFormat(String date) {
        return newDate(date, FORMAT_DATETIME);
    }

    public static Date newDateISO(String date) {
        return newDate(date, FORMAT_ISO);
    }

    public static Date newDateISO_SSS(String date) {
        return newDate(date, FORMAT_ISO_SSS);
    }

    public static LocalDate newLocalDate() {
        return new LocalDate();
    }

    public static LocalDate newLocalDate(String date) {
        return newLocalDate(newDate(date));
    }

    public static LocalDate newLocalDate(String date, String format) {
        return newLocalDate(newDate(date, format));
    }

    public static LocalDate newLocalDate(Date date) {
        return new LocalDate(date);
    }

    public static LocalDate newLocalDate(int year, int month, int day) {
        return new LocalDate(year, month, day);
    }

    public static Date newDateTime(int year, int month, int day, int hour, int minute, int second) {
        return newLocalDateTime(year, month, day, hour, minute, second).toDate();
    }

    public static LocalDateTime newLocalDateTime(int year, int month, int day, int hour, int minute, int second) {
        return new LocalDateTime(year, month, day, hour, minute, second);
    }

    public static LocalDateTime newLocalDateTime(String date) {
        return newLocalDateTime(newDate(date));
    }

    public static LocalDateTime newLocalDateTime(String date, String format) {
        return newLocalDateTime(newDate(date, format));
    }

    public static LocalDateTime newLocalDateTime(Date date) {
        return new LocalDateTime(date);
    }

    public static SimpleDateFormat newDateFormat(String format) {
        return new SimpleDateFormat(format);
    }

    public static SimpleDateFormat newDateFormat(String format, Locale locale) {
        return new SimpleDateFormat(format, locale);
    }

    public static SimpleDateFormat newDateFormatForDate() {
        return newDateFormat(FORMAT_DATE);
    }

    public static SimpleDateFormat newDateFormatForDateTime() {
        return newDateFormat(FORMAT_DATETIME);
    }

    public static SimpleDateFormat newDateFormatForDateISO() {
        return newDateFormat(FORMAT_ISO);
    }

    public static String newDateString(String pattern) {
        return toString(newDate(), pattern);
    }

    public static String newDateStringOfFormatDate() {
        return toString(newDate(), FORMAT_DATE);
    }

    public static String newDateStringOfFormatDateTime() {
        return toString(newDate(), FORMAT_DATETIME);
    }

    public static String newDateStringOfFormatDateISO() {
        return toString(newDate(), FORMAT_ISO);
    }

    public static String newDateStringOfFormatNone() {
        return toString(newDate(), FORMAT_NONE);
    }

    public static String newDateStringOfFormatDateTimeNone() {
        return toString(newDate(), FORMAT_DATETIME_NONE);
    }

    public static String newDateStringOfFormatDatetimeNoneSSS() {
        return toString(newDate(), FORMAT_DATETIME_NONE_SS);
    }

    public static String newDateStringOfFormatDateNone() {
        return toString(newDate(), FORMAT_DATE_NONE);
    }

    public static String newDateStringOfFormatTimeNone() {
        return toString(newDate(), FORMAT_TIME_NONE);
    }

    public static String newDateStringOfFormat_DATETIME_SSS() {
        return toString(newDate(), FORMAT_DATETIME_SSS);
    }

    public static String newDateStringOfFormatDateTimeSSSNoneSpace() {
        return toString(newDate(), FORMAT_DATETIME_NONE_SS_SSS);
    }

    /**
     * 判断字符串是否是日期
     */
    public static boolean isDate(String timeString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        try {
            format.parse(timeString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 用尝试多种格式解析日期时间.
     *
     * @param date 时间字符串
     * @return 如果无法解析，那么返回 {@code null}
     */
    public static Date parse(String date) {
        Date d = parse(date, STD_PATTERNS);
        if (d == null) {
            d = parseRFC822Date(date);
        }
        if (d == null) {
            d = parseW3CDateTime(date);
        }
        if (d == null) {
            try {
                d = DateFormat.getInstance().parse(date);
            } catch (ParseException e) {
                d = null;
            }
        }
        return d;
    }

    /**
     * 用指定的格式解析日期时间.
     *
     * @param date    时间字符串
     * @param pattern see {@link SimpleDateFormat}
     * @return 如果无法解析，那么返回 {@code null}
     */
    public static Date parse(String date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        df.setLenient(false);
        try {
            ParsePosition pp = new ParsePosition(0);
            Date d = df.parse(date, pp);
            if (d != null && pp.getIndex() == date.length()) {
                return d;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 用指定的格式解析日期时间.
     *
     * @param date     时间字符串
     * @param patterns 多个模式，see {@link SimpleDateFormat}
     * @return 如果无法解析，那么返回 {@code null}
     */
    public static Date parse(String date, String[] patterns) {
        if (date == null || date.length() == 0) {
            return null;
        }

        date = date.trim();
        for (String pattern : patterns) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            df.setLenient(false);
            try {
                ParsePosition pp = new ParsePosition(0);
                Date d = df.parse(date, pp);
                if (d != null && pp.getIndex() == date.length()) {
                    return d;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static Date parseRFC822Date(String date) {
        int ipos = date.indexOf(" UT");
        if (ipos > -1) {
            String pre = date.substring(0, ipos);
            String post = date.substring(ipos + 3);
            date = pre + " GMT" + post;
        }
        return parse(date, RFC822_PATTENRS);
    }

    public static Date parseW3CDateTime(String date) {
        // if sDate has time on it, it injects 'GTM' before de TZ displacement
        // to allow the SimpleDateFormat parser to parse it properly
        int tIndex = date.indexOf("T");
        if (tIndex > -1) {
            if (date.endsWith("Z")) {
                date = date.substring(0, date.length() - 1) + "+00:00";
            }
            int tzdIndex = date.indexOf("+", tIndex);
            if (tzdIndex == -1) {
                tzdIndex = date.indexOf("-", tIndex);
            }
            if (tzdIndex > -1) {
                String pre = date.substring(0, tzdIndex);
                int secFraction = pre.indexOf(",");
                if (secFraction > -1) {
                    pre = pre.substring(0, secFraction);
                }
                String post = date.substring(tzdIndex);
                date = pre + "GMT" + post;
            }
        } else {
            date += "T00:00GMT";
        }
        return parse(date, W3CDATETIME_PATTERNS);
    }

    /**
     * 用指定的格式格式化当前时间.
     */
    public static String format(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 用指定的格式格式化指定时间.
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static String formatRFC822(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(RFC822_DATETIME_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    public static String formatW3CDateTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(W3C_DATETIME_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 获取过去的天数
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 取得Date的开始
     */
    public static Date getDateStart(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 取得Date的结束
     */
    public static Date getDateEnd(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = sdf.parse(formatDate(date, "yyyy-MM-dd") + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 取得今天的开始
     */
    public static Date getStartOfToday() {
        return getStartOfTheDate(new Date());
    }

    /**
     * 取得今天的结束
     */
    public static Date getEndOfToday() {
        return getEndOfTheDate(new Date());
    }

    /**
     * 取得指定date当天的开始
     * date:yyyy-mm-dd
     */
    public static Date getStartOfThe(String date, String format) {
        LocalDate dt = newLocalDate(newDate(date, format));
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的结束
     * date:yyyy-mm-dd
     */
    public static Date getEndOfThe(String date, String format) {
        LocalDate d = newLocalDate(newDate(date, format));
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定date当天的开始
     * date:yyyy-mm-dd
     */
    public static Date getStartOfTheDate(String date) {
        LocalDate dt = newLocalDate(date);
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的开始
     */
    public static Date getStartOfTheDate(Date date) {
        LocalDate dt = newLocalDate(date);
        return dt.toLocalDateTime(new LocalTime(0, 0, 0)).toDate();
    }

    /**
     * 取得指定date当天的结束
     * date:yyyy-mm-dd
     */
    public static Date getEndOfTheDate(String date) {
        LocalDate d = newLocalDate(date);
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定date当天的结束
     */
    public static Date getEndOfTheDate(Date date) {
        LocalDate d = newLocalDate(date);
        LocalDateTime dt = d.toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的开始
     * date:yyyy-mm-dd hh:mm:ss
     */
    public static Date getStartOfTheDateTime(String datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(0, 0, 0));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的开始
     */
    public static Date getStartOfTheDateTime(Date datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(0, 0, 0));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的结束
     * date:yyyy-mm-dd hh:mm:ss
     */
    public static Date getEndOfTheDateTime(String datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 取得指定datetime当天的结束
     */
    public static Date getEndOfTheDateTime(Date datetime) {
        LocalDateTime dt = newLocalDateTime(datetime);
        dt = dt.toLocalDate().toLocalDateTime(new LocalTime(23, 59, 59));
        return dt.toDate();
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date, String format) {
        if (format == null) {
            format = DEFAULT_FORMAT;
        }
        return newDateFormat(format).format(date);
    }

    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String toString(Date date) {
        return toString(date, null);
    }

    /**
     * 转换为FORMAT_DATE字符串
     */
    public static String toStringWithDateFormatDate(Date date) {
        return toString(date, FORMAT_DATE);
    }

    /**
     * 转换为FORMAT_DATETIME字符串
     */
    public static String toStringWithDateFormatDateTime(Date date) {
        return toString(date, FORMAT_DATETIME);
    }

    /**
     * 转换为FORMAT_ISO字符串
     */
    public static String toStringWithDateFormatISO(Date date) {
        return toString(date, FORMAT_ISO);
    }

    public static boolean isAfter(Date date, Date when) {
        return date.after(when);
    }

    public static boolean isAfterOfNow(Date date) {
        return isAfter(date, newDate());
    }

    public static boolean isBefore(Date date, Date when) {
        return date.before(when);
    }

    public static boolean isBeforeOfNow(Date date) {
        return isBefore(date, newDate());
    }

    public static boolean isToday(Date date) {
        return isAfter(date, getStartOfTheDate(newDate()));
    }

    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 在现在增加数个整月
     */
    public static Date addMonthOfNow(int n) {
        return addMonth(Dates.newDate(), n);
    }

    /**
     * 在日期上加多少天
     */
    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_WEEK, n);
        return cal.getTime();
    }

    /**
     * 在现在日期上加多少天
     */
    public static Date addDayOfNow(int n) {
        return addDay(Dates.newDate(), n);
    }

    /**
     * 在日期上增加数个整小时
     */
    public static Date addHour(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, n);
        return cal.getTime();
    }

    /**
     * 在现在时间上增加数个整小时
     */
    public static Date addHourOfNow(int n) {
        return addHour(Dates.newDate(), n);
    }

    /**
     * 在日期上增加数个整分钟
     */
    public static Date addMinute(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);
        return cal.getTime();
    }

    /**
     * 在现在时间上增加数个整分钟
     */
    public static Date addMinuteOfNow(int n) {
        return addMinute(Dates.newDate(), n);
    }

    /**
     * 在日期上增加数个整秒
     */
    public static Date addSecond(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, n);
        return cal.getTime();
    }

    /**
     * 在现在增加数个整秒
     */
    public static Date addSecondOfNow(int n) {
        return addSecond(Dates.newDate(), n);
    }

    public static int getLengthOfFormat(String format) {
        return format.replaceAll("\'", "").length();
    }

    public static boolean isFormat(String date, String format) {
        return date.length() == getLengthOfFormat(format);
    }

    // 获得当前日期与本周一相差的天数
    public static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }

    // 获得当前周- 周一的日期
    public static String getCurrentMonday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    // 获得当前周- 周日 的日期
    public static String getPreviousSunday() {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

}
