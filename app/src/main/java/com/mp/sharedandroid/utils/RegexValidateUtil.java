package com.mp.sharedandroid.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 号码校验工具类
 * copy from appLib
 */
public class RegexValidateUtil {

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        if (TextUtils.isEmpty(email)) {
            flag = true;
        } else {
            try {
                String check = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
                Pattern regex = Pattern.compile(check);
                Matcher matcher = regex.matcher(email);
                flag = matcher.matches();
            } catch (Exception e) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 验证手机号码
     *
     * @param mobileNumber 要检验的手机号
     * @return true:合法手机号；false:非法手机号
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        if (!TextUtils.isEmpty(mobileNumber)) {
            try {
                Pattern regex = Pattern.compile("^[1][0-9]{10}$");
                Matcher matcher = regex.matcher(mobileNumber);
                flag = matcher.matches();
            } catch (Exception e) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 验证QQ号码
     *
     * @param QQ
     * @return
     */
    public static boolean checkQQ(String QQ) {
        String regex = "^[1-9][0-9]{4,}$";
        return check(QQ, regex);
    }

    /**
     * 验证QQ号码
     *
     * @param str
     * @param regex
     * @return
     */
    public static boolean check(String str, String regex) {
        boolean flag = false;
        if (!TextUtils.isEmpty(str)) {
            try {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(str);
                flag = matcher.matches();
            } catch (Exception e) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        // 验证带区号的
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");
        // 验证没有区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");
        m = str.length() > 9 ? p1.matcher(str) : p2.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 判断是否为整数
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
