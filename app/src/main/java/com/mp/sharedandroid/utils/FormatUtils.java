package com.mp.sharedandroid.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 格式化数据工具类
 * 格式化日期，float
 * Created by cyw on 2018/6/25.
 */

public class FormatUtils {

    public static final String YMD = "yyyyMMdd";
    public static final String YMD_YEAR = "yyyy";
    public static final String YM_BREAK = "yyyy-MM";
    public static final String YMD_BREAK = "yyyy-MM-dd";
    public static final String YMDHMS = "yyyyMMddHHmmss";
    public static final String MS_BREAK = "mm:ss";
    public static final String YMDHMS_BREAK = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHMS_BREAK_HALF = "yyyy-MM-dd HH:mm";

    /**
     * 计算时间差
     */
    public static final int CAL_MINUTES = 1000 * 60;
    public static final int CAL_HOURS = 1000 * 60 * 60;
    public static final int CAL_DAYS = 1000 * 60 * 60 * 24;


    /**
     * 格式化单精度浮点数
     *
     * @param source 数据源
     * @param scale  保留几位小数
     * @return 格式化后的数据
     */
    public static float formatFloat(float source, int scale) {
        int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal((double) source);
        bd = bd.setScale(scale, roundingMode);
        return bd.floatValue();
    }

    /**
     * @param source
     * @param scale
     * @return
     */
    public static float formatFloat(String source, int scale) {
        if (TextUtils.isEmpty(source)) {
            return 0;
        }
        try {
            Float sourcef = Float.valueOf(source);
            int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
            BigDecimal bd = new BigDecimal((double) sourcef);
            bd = bd.setScale(scale, roundingMode);
            return bd.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 获取日期格式化后的值
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getDateText(Date date, String pattern) {
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化日期
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取时间戳
     *
     * @param date
     * @return
     */
    public static Long getTime(Date date) {
        return date.getTime();
    }

    /**
     * 获取两个时间的时间差
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     * @throws ParseException
     */
    public static long getTimeDifference(String startDate) throws ParseException {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat(YMDHMS_BREAK);
        Date start = format.parse(startDate);
        long startTime = start.getTime();
        return currentTimeMillis - startTime;
    }

    /**
     * 计算时间差
     *
     * @param startDate
     * @param endDate
     * @param calType   计算类型,按分钟、小时、天数计算
     * @return
     */
    public static int calDiffs(Date startDate, Date endDate, int calType) {
        Long start = FormatUtils.getTime(startDate);
        Long end = FormatUtils.getTime(endDate);
        int diff = (int) ((end - start) / calType);
        return diff;
    }

    /**
     * 计算时间差值以某种约定形式显示
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static String timeDiffText(Date startDate, Date endDate) {
        int calDiffs = FormatUtils.calDiffs(startDate, endDate, FormatUtils.CAL_MINUTES);
        if (calDiffs == 0) {
            return "刚刚";
        }
        if (calDiffs < 60) {
            return calDiffs + "分钟前";
        }
        calDiffs = FormatUtils.calDiffs(startDate, endDate, FormatUtils.CAL_HOURS);
        if (calDiffs < 24) {
            return calDiffs + "小时前";
        }
        if (calDiffs < 48) {
            return "昨天";
        }
        return FormatUtils.getDateText(startDate, FormatUtils.YMDHMS_BREAK_HALF);
    }

    /**
     * 显示某种约定后的时间值,类似微信朋友圈发布说说显示的时间那种
     *
     * @param date
     * @return
     */
    public static String showTimeText(Date date) {
        return FormatUtils.timeDiffText(date, new Date());
    }

}
