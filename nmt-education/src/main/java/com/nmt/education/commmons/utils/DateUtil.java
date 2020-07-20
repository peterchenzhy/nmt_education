package com.nmt.education.commmons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 2019-03-15 yemingxing 升级为使用joda-time工具
 *
 * @author DuKaijun
 * @modifier yemingxing
 * @summary 日期工具类
 * @Copyright (c) 2020, PeterChen Group All Rights Reserved.
 * @since 2018/5/23 10:41
 */
@Slf4j
public class DateUtil {

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static final String DEFAULT_SETTLEMONTH_FORMAT = "yyyyMM";

    private static final String DEFAULT_DATE_FORMAT_2 = "yyyyMMdd";

    private static final String DEFAULT_MONTH_FORMAT = "yyyyMM";

    private static final String COMMMON_DATETIME_FORMAT = "yyyyMMddhhmmss";

    private static DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);

    private static DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

    private static DateTimeFormatter COMMON_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(COMMMON_DATETIME_FORMAT);

    private static DateTimeFormatter DEFAULT_MONTH_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_MONTH_FORMAT);

    private DateUtil() {
    }

    public static Date defaultDate2Null(Date date) {
        if (defaultDateTime().equals(date)) {
            return null;
        } else {
            return date;
        }
    }

    /**
     * 返回默认时间，1970-01-02 00:00:00
     *
     * @return Date
     */
    public static Date defaultDateTime() {
        // 设置为2号，屏蔽时区引起的问题
        return DateUtil.customTime(1970, 1, 2, 0, 0, 0);
    }

    public static Date moveDay(Date date, int interval) {
        return new DateTime(date.getTime()).plusDays(interval).toDate();
    }

    public static Date moveMonth(Date date, int interval) {
        return new DateTime(date.getTime()).plusMonths(interval).toDate();
    }

    public static Date customTime(Date date, int hour, int minute, int second) {
        if (date == null) {
            return null;
        }
        return new DateTime(date.getTime()).withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(second)
                .withMillisOfSecond(0).toDate();
    }

    /**
     * 获取当月最后一天的末尾时间
     *
     * @param baseTime
     * @return
     */
    public static Date currentMonthLastDate(Date baseTime) {
        return new DateTime(baseTime.getTime()).dayOfMonth().withMaximumValue().withTimeAtStartOfDay().plusDays(1)
                .plusMillis(-1).toDate();
    }

    /**
     * 获取当月第一天的初始时间
     *
     * @param baseTime
     * @return
     */
    public static Date currentMonthFirstDate(Date baseTime) {
        return new DateTime(baseTime.getTime()).withDayOfMonth(1).withTimeAtStartOfDay().toDate();
    }

    public static Date clearHourMinuteSecond(Date baseTime) {
        return new DateTime(baseTime.getTime()).withTimeAtStartOfDay().toDate();
    }

    public static Date customTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return new DateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(dayOfMonth).withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute).withSecondOfMinute(second).withMillisOfSecond(0).toDate();
    }

    public static Date addOrSubSomeDay(Date date, int someDay) {
        return new DateTime(date.getTime()).plusDays(someDay).toDate();
    }

    public static Date addOrSubSomeMinutes(Date date, int someMinutes) {
        return new DateTime(date.getTime()).plusMinutes(someMinutes).toDate();
    }

    public static String formatDateTime(Date dateTime) {
        if (dateTime == null || defaultDateTime().equals(dateTime)) {
            return "";
        }
        return new DateTime(dateTime.getTime()).toString(DEFAULT_DATETIME_FORMAT);
    }

    public static Date parseDateTime(String dateTime) {
        if (dateTime == null || StringUtils.isEmpty(dateTime.trim())) {
            return null;
        }
        return DateTimeFormat.forPattern(DEFAULT_DATETIME_FORMAT).parseDateTime(dateTime).toDate();
    }

    public static String formatDate(Date date) {
        if (date == null || defaultDateTime().equals(date)) {
            return "";
        }
        return new DateTime(date.getTime()).toString(DEFAULT_DATE_FORMAT);
    }

    public static String formatDate2TaxFriend(Date date) {
        if (Objects.isNull(date) || date.compareTo(defaultDateTime()) <= 0) {
            return null;
        }
        return new DateTime(date.getTime()).toString(DEFAULT_DATE_FORMAT);
    }

    /**
     * 注意：上海时区在1992年之前有夏令时，此处转换会有问题 eg ： long date = 576860400000L; 应该是1988-4-13
     * 但是转出来是1988-4-12 替换方案 --> formatDate2LocalDate
     *
     * @param date
     * @return
     */
    public static String formatDate2(Date date) {
        return new DateTime(date.getTime()).toString(DEFAULT_DATE_FORMAT_2);
    }

    public static String formatDate2LocalDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT_2);
        return sdf.format(date);
    }

    public static Date parseDate(String date) {
        if (date == null || StringUtils.isEmpty(date.trim())) {
            return null;
        }
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT).parseDateTime(date).toDate();
    }

    public static Date parseDate2(String date) {
        if (date == null || StringUtils.isEmpty(date.trim())) {
            return null;
        }
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT_2).parseDateTime(date).toDate();
    }

    public static String formatSettleMonth(Date dateTime) {
        return new DateTime(dateTime.getTime()).toString(DEFAULT_SETTLEMONTH_FORMAT);
    }

    public static Date parseSettleMonth(String date) {
        return DateTimeFormat.forPattern(DEFAULT_SETTLEMONTH_FORMAT).parseDateTime(date).toDate();
    }

    public static String format(String format, Date date) {
        return new DateTime(date.getTime()).toString(format);
    }

    /**
     * 判断两个结算月大小
     *
     * @param indexMonth
     * @param compareMonth
     * @return null月份不合法
     */
    public static Boolean isGreaterThan(String indexMonth, String compareMonth) {
        Date indexM = parseSettleMonth(indexMonth);
        Date compareM = parseSettleMonth(compareMonth);
        if (indexM == null || compareM == null) {
            return null;
        }
        if (indexM.getTime() > compareM.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 获取当月最后一天的初始时间
     *
     * @param yyyyMM
     * @return
     */
    public static Date getThisMonthLastDay(String yyyyMM) {
        return DateTimeFormat.forPattern("yyyyMM").parseDateTime(yyyyMM).dayOfMonth().withMaximumValue()
                .withTimeAtStartOfDay().toDate();
    }

    /**
     * 时间戳转换为Date
     *
     * @param timeStamp 时间戳
     * @return 日期
     */
    public static Date timeStampToDate(Long timeStamp) {
        if (null == timeStamp) {
            return null;
        }
        return new Date(timeStamp);
    }

    /**
     * 解析左开放时间，如果传递时间为2018-09-01,解析为2018-09-01 00:00:00
     *
     * @param dateString 日期时间
     * @return 日期
     * @author 戴志强
     * @modifier 戴志强
     * @since 2018-11-14 10:44:23
     */
    public static Date parseOpenDate(String dateString) {
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT).parseDateTime(dateString).withTimeAtStartOfDay().toDate();
    }

    public static Date parseOpenDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return new DateTime(date).withTimeAtStartOfDay().toDate();
    }


    /**
     * 解析左开放时间，如果传递时间为2018-09-01,解析为2018-09-01 00:00:00
     *
     * @param dateString 日期时间
     * @return 日期
     * @author 戴志强
     * @modifier 戴志强
     * @since 2018-11-14 10:44:23
     */
    public static Date parseOpenDate_2(String dateString) {
        if (!org.springframework.util.StringUtils.hasText(dateString) || "null".equals(dateString) || dateString.length() != 8) {
            return null;
        }
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT_2).parseDateTime(dateString).withTimeAtStartOfDay()
                .toDate();
    }

    /**
     * 解析右关闭时间，如果传递事件为2018-09-01,解析为2018-09-01 23:59:59
     *
     * @param dateString 日期时间
     * @return 日期
     * @author 戴志强
     * @modifier 戴志强
     * @since 2018-11-14 10:45:58
     */
    public static Date parseCloseDate(String dateString) {
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT).parseDateTime(dateString).withTimeAtStartOfDay()
                .plusDays(1).plusMillis(-1).toDate();
    }

    /**
     * 解析右关闭时间，如果传递事件为2018-09-01,解析为2018-09-01 23:59:59
     *
     * @param dateString 日期时间
     * @return 日期
     * @author 戴志强
     * @modifier 戴志强
     * @since 2018-11-14 10:45:58
     */
    public static Date parseCloseDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return new DateTime(date).withTimeAtStartOfDay()
                .plusDays(1).plusMillis(-1).toDate();
    }

    /**
     * 解析右关闭时间，如果传递事件为2018-09-01,解析为2018-09-01 23:59:59
     *
     * @param dateString 日期时间
     * @return 日期
     * @author 戴志强
     * @modifier 戴志强
     * @since 2018-11-14 10:45:58
     */
    public static Date parseCloseDate2(String dateString) {
        if (!org.springframework.util.StringUtils.hasText(dateString) || "null".equals(dateString)) {
            return null;
        }
        return DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT_2).parseDateTime(dateString).withTimeAtStartOfDay()
                .plusDays(1).plusMillis(-1).toDate();
    }

    /**
     * 检查日期 1、是否符合yyyy-MM-dd 格式 2、日期必须小于当前日期
     *
     * @param date 日期
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2019/7/8 16:31
     */
    public static Boolean checkDate(String date) {
        Date req;
        try {
            req = parseOpenDate(date);
        } catch (Exception e) {
            log.error("日期转换失败，传入参数格式不为yyyy-MM-dd ，参数：" + date);
            return false;
        }
        Date now = customTime(new Date(), 0, 0, 0);
        return now.after(req);
    }

    /**
     * 检查日期格式 1、是否符合yyyy-MM-dd 格式
     *
     * @param date
     * @author PeterChen
     * @version v1
     * @since 2019/7/8 17:03
     */
    @SuppressWarnings("unused")
    public static Boolean checkDateFormat(String date) {
        try {
            Date req = parseOpenDate(date);
        } catch (Exception e) {
            log.error("日期转换失败，传入参数格式不为yyyy-MM-dd ，参数：" + date);
            return false;
        }
        return true;
    }

    /**
     * 获取指定日期的 最近两个月 例： 入参 2019-7-19 返回 格式倒序--> 201907,201906
     *
     * @author PeterChen
     * @version v1
     * @since 2019/7/19 11:34
     */
    public static List<String> getLast2Month(Date date) {
        List<String> result = new ArrayList<>(2);
        String currentMonth = format(DEFAULT_MONTH_FORMAT, date);
        result.add(currentMonth);
        String lastMonth = format(DEFAULT_MONTH_FORMAT, moveMonth(date, -1));
        result.add(lastMonth);
        return result;
    }

    /**
     * 获取月份 201907
     *
     * @param date 日期
     * @author PeterChen
     * @version v1
     * @since 2019/7/19 11:46
     */
    public static String getMonth(Date date) {
        return format(DEFAULT_MONTH_FORMAT, date);
    }

    /**
     * 日期转timeMills
     *
     * @param date
     * @return java.lang.Long
     * @version v1
     * @summary
     * @author hejing
     * @since 2019-11-24
     */
    public static Long formatTimeMills(Date date) {
        if (Objects.isNull(date) || date.equals(defaultDateTime())) {
            return null;
        }
        return date.getTime();
    }

    public static void main(String[] args) {
//        Integer incomeMonth = 201911;
//        Date lastIncomeMonthDate = DateUtil
//                .currentMonthLastDate(DateUtil.parseSettleMonth(String.valueOf(incomeMonth)));
//        log.info(lastIncomeMonthDate.toString());

        log.info(parseOpenDate(new Date()).toString());
        log.info(parseCloseDate(new Date()).toString());
    }

    /**
     * 判断日期是否是 默认值或者是空
     *
     * @param date
     * @author PeterChen
     * @modifier PeterChen
     * @version v1
     * @since 2020/2/22 14:49
     */
    public static boolean isNull(Date date) {
        if (Objects.isNull(date) || defaultDateTime().compareTo(date) == 0) {
            return true;
        }
        return false;
    }

    /**
     * Get current Date (yyyy-MM-dd)
     *
     * @return String
     */
    public static String currDate() {
        return DEFAULT_DATE_FORMATTER.format(LocalDate.now());
    }

    /**
     * Get current DateTime (yyyy-MM-dd HH:mm:ss)
     *
     * @return String
     */
    public static String currDateTime() {
        return DEFAULT_DATETIME_FORMATTER.format(LocalDateTime.now());
    }

    /**
     * Get current DateTime (yyyyMMddhhmmss)
     *
     * @return String
     */
    public static String currDateTime2() {
        return COMMON_DATETIME_FORMATTER.format(LocalDateTime.now());
    }

    /**
     * Get current YM
     *
     * @return int
     */
    public static int currYM() {
        String ym = DEFAULT_MONTH_FORMATTER.format(LocalDateTime.now());
        return Integer.valueOf(ym);
    }

    /**
     * Get date (yyyy-MM-dd)
     *
     * @param date
     * @return
     */
    public static String date2str(Date date) {
        if (date == null) {
            return null;
        }
        return DEFAULT_DATE_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

}
