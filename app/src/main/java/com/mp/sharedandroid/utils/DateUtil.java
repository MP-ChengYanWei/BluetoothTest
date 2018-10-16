package com.mp.sharedandroid.utils;

import android.widget.Chronometer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 与日期相关的工具类
 *
 * @author copy from appLib
 */
public class DateUtil {

    public static String FORMAT_SHORT = "yyyy-MM-dd";
    public static String FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    public static String FORMAT_HOUR = "yyyy-MM-dd HH";
    public static String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
    public static String FORMAT_FULL_US = "yyyy-MM-dd hh:mm:ss.SSS";
    public static String FORMAT_SHORT_CN = "yyyy年MM月dd";
    public static String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    public static String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";
    public static String FORMAT_NOS = "yyyy-MM-dd HH:mm";
    public static String FORMAT_EASY = "MM-dd HH:mm";
    public static String FORMAT_HOUR_MINUTE = "HH:mm";

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 默认日期格式
     */
    public static String getDatePattern() {
        return FORMAT_LONG;
    }

    /**
     * 根据预设格式返回当前日期
     *
     * @return
     */
    public static String getNow() {
        return format(new Date());
    }

    /**
     * 获取当前日期,根据用户指定格式
     */
    public static String getNow(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 使用预设格式格式化日期
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, getDatePattern());
    }

    /**
     * 使用预设格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @return
     */
    public static Date parse(String strDate) {
        return parse(strDate, getDatePattern());
    }

    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */
    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 得到毫秒值
     *
     * @param strDate
     * @return
     */
    public static long getTime(String strDate) {
        return parse(strDate).getTime();
    }

    /**
     * 获取日期年份
     *
     * @param date 日期
     * @return
     */
    public static String getYear(Date date) {
        return format(date).substring(0, 4);
    }

    /**
     * 判断两个时间是否是10分钟之内
     *
     * @param date
     * @param lastDate
     * @return
     */
    public static boolean isInTenMinute(Date date, Date lastDate) {
        long delta = date.getTime() - lastDate.getTime();
        boolean is = false;
        if (-6000L * 10 < delta && delta < 60000L * 10) {
            is = true;
        }
        return is;
    }

    /**
     *
     * @param cmt  Chronometer控件
     * @return 小时+分钟+秒数  的所有秒数
     */
    public  static String getChronometerSeconds(Chronometer cmt) {
        int totalss = 0;
        String string = cmt.getText().toString();
        if(string.length()==7){
            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours =hour*3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[2]);
            totalss = Hours+Mins+SS;
            return String.valueOf(totalss);
        } else if (string.length()==5){
            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins =min*60;
            int  SS =Integer.parseInt(split[1]);
            totalss =Mins+SS;
            return String.valueOf(totalss);
        }
        return String.valueOf(totalss);
    }

}
