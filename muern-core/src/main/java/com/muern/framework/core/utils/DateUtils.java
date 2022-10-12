package com.muern.framework.core.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

/**
 * @author gegeza
 * @date 2019-11-26 5:31 PM
 */
public final class DateUtils {



    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_MINUTES = "HH:mm";
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIMESTAMP = "yyyyMMddHHmmssSSSS";
    private static final DateTimeFormatter DATE_DAY_LONG = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_MONTH_LONG = DateTimeFormatter.ofPattern("yyyyMM");
    public static final String[] WEEKDAYS = {"一","二", "三", "四", "五", "六","日"};
    public static final String DATE_TYPE_DAY = "day";
    public static final String DATE_TYPE_HOUR = "hour";
    public static final String DATE_TYPE_MINUTES = "minutes";

    private DateUtils() {}

    /** 格式化当前时间 */
    public static String formatNowTimeStamp() {
        return formatNow(PATTERN_TIMESTAMP);
    }

    /** 格式化时间 */
    public static String formatTime(LocalDateTime localDateTime) {
        return format(localDateTime, PATTERN_DATETIME);
    }

    /** 格式化当前时间 */
    public static String formatNowDateTime() {
        return formatNow(PATTERN_DATETIME);
    }

    /** 格式化当前时间 */
    public static String formatNowDate() {
        return formatNow(PATTERN_DATE);
    }

    /** 格式化当前时间 */
    public static String formatNow(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /** 将日期字符串转换为另一种格式 */
    public static String format(String date, String sourcePattern, String targetPattern) {
        return format(parse(date, sourcePattern), targetPattern);
    }

    /** 格式化时间 */
    public static String format(TemporalAccessor temporalAccessor, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(temporalAccessor);
    }

    /** String 转 LocalDateTime */
    public static LocalDateTime parse(String dateStr, String pattern) {
        if (StringUtils.hasText(dateStr) && StringUtils.hasText(pattern)) {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }

    /** String 转 LocalDate */
    public static LocalDate toLocalDate(String date, String pattern) {
        if (StringUtils.hasText(date)) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }

    /** Date转LocalDateTime */
    public static LocalDateTime parse(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime转Date */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /** 时间戳转LocalDateTime */
    public static LocalDateTime secondsToLocalDateTime(long seconds) {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofHours(8));
    }

    /** 返回较大的时间 */
    public static LocalDateTime max(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        return localDateTime1.compareTo(localDateTime2) > 0 ? localDateTime1 : localDateTime2;
    }

    /** 返回较小的时间 */
    public static LocalDateTime min(LocalDateTime localDateTime1, LocalDateTime localDateTime2) {
        return localDateTime1.compareTo(localDateTime2) > 0 ? localDateTime2 : localDateTime1;
    }


    /** 获取某天最小的时间点：yyyyMMdd 00:00:00 */
    public static LocalDateTime getDayStart(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /** 获取某天最后的时间点：yyyyMMdd 23:59:59 */
    public static LocalDateTime getDayEnd(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    /** 取得两个日期的间隔天数 */
    public static long getDiffDays(LocalDateTime day1, LocalDateTime day2) {
        return day1.toLocalDate().toEpochDay() - day2.toLocalDate().toEpochDay();
    }

    public static long getDiffHours(LocalDateTime day1, LocalDateTime day2) {
        return day2.until(day1, ChronoUnit.HOURS);
    }

    /** 获取所在周的星期一的日期 */
    public static LocalDate getFirstDayOfWeek(Date time) {
        //设置时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //设置时间格式
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        //获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        //根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return LocalDate.parse(sdf.format(cal.getTime()), dtf);
    }

    /** 获取现在到月底的秒数 */
    public static long dateTimeToEndOfMonth() {
        return LocalDateTime.of(LocalDate.from(LocalDateTime.now()).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX).toEpochSecond(ZoneOffset.of("+8")) - (System.currentTimeMillis() / 1000);
    }

    /** 根据指定时间的月份(2020-01) 获取对应的月份开始时间 */
    public static LocalDateTime dateMonth(String month) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
        return LocalDateTime.parse(month + "-01 00:00:00", df);
    }

    /**
     * 获取指定时间的开始时间 (月份)
     *
     * @param startTime 时间
     */
    public static LocalDateTime monthStart(LocalDateTime startTime) {
        return LocalDateTime.of(LocalDate.from(startTime.with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN);
    }

    /**
     * 获取指定时间的结束时间 (月份)
     */
    public static LocalDateTime monthEnd(LocalDateTime startTime) {
        return LocalDateTime.of(LocalDate.from(startTime.with(TemporalAdjusters.lastDayOfMonth())), LocalTime.MAX);
    }

    /**
     * 本周第一天 例如:20211228
     */
    public static long weekFirstDay() {
        return Long.parseLong(DATE_DAY_LONG.format(LocalDateTime.now().with(DayOfWeek.MONDAY)));
    }

    /**
     * 本月第一天 例如:20211201
     */
    public static long monthFirstDay() {
        return Long.parseLong(DATE_DAY_LONG.format(LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth())), LocalTime.MIN)));
    }

    /** 本年第一天 例如:20210101 */
    public static long yearFirstDay() {
        return Long.parseLong(DATE_DAY_LONG.format(LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear())), LocalTime.MIN)));
    }

    /** 本年第一月 例如:202101 */
    public static long yearFirstMonth() {
        return Long.parseLong(DATE_MONTH_LONG.format(LocalDateTime.of(LocalDate.from(LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear())), LocalTime.MIN)));
    }

    /** 获取当前月 例如:202101 */
    public static long yearLocalMonth() {
        return Long.parseLong(DATE_MONTH_LONG.format(LocalDateTime.now()));
    }

    /** 获取当前日 例如:20211212 */
    public static long localDay() {
        return Long.parseLong(DATE_DAY_LONG.format(LocalDateTime.now()));
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static LocalDate date2LocalDate(Date date) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime.toLocalDate();
    }

    public static LocalTime date2LocalTime(Date date) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return localDateTime.toLocalTime();
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zoneId).toInstant();
        return Date.from(instant);
    }

    public static Date localDate2Date(LocalDate localDate){
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant();
        return Date.from(instant);
    }


    public static Date localTime2Date(LocalTime localTime){
        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(now, localTime);
        return localDateTime2Date(localDateTime);
    }

    public static String getLastDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

}
