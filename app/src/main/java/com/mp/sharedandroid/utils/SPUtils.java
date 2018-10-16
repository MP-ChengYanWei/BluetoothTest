package com.mp.sharedandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mp.bluetooth.application.MyApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by wutingyou on 2017/3/22.
 * SharedPreferences 读取和保存
 * 参考了原来的方式
 */

public class SPUtils {
    public SPUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = Contants.USERINFO_FILE;

    //君正蓝牙会在 settings.xml unique_address 写入蓝牙地址
    private static final String LinuxFileNanem = "settings";
    private static final String LinuxAddr = "unique_address";
    /**
     * 有关用户的信息
     */
    public static final String AUTOLOGIN = "false";//是否自动登录
    public static final String LOGINID = "loginId";//登录Id
    public static final String PASSWORD = "password";//登录密码
    public static final String TRUENAME = "trueName";//昵称或真名
    public static final String PENNICKNAME = "nickname";//昵称或真名
    public static final String BINDDEVICE = "bindDevice";//绑定的笔
    public static final String MACADDRESS = "macAddress";//绑定的笔的蓝牙地址
    public static final String PHOTO = "photo";//头像
    public static final String SEX = "sex";//性别
    public static final String GRADE = "grade";//年级
    public static final String GRADE_ID = "";//年级的id
    public static final String ADDRESS = "address";//收货地址
    public static final String AGE = "age";//年龄
    public static final String READ_LEVEL = "readLevel";//阅读等级
    public static final String SCHOOL_ADDRESS = ""; // 学校地址
    public static final String REFERENCE = "reference"; // 下载相关
    public static final String REFERENCE_MD5 = "reference_md5"; // 下载升级文件后的md5
    public static final String EMAIL = "email"; // Email
    public static final String TEST_DATE = "test_date"; // 阅读测试日期
    public static final String TEST_TIMES = "test_times"; // 阅读测试次数

    public static final String LNGANDLAT = "lngandlat"; // 经度
    public static final String FORMAT_ADDRESS = "formataddress"; // 格式化地址
    public static final String LAST_SEND_ADDRESS_TIME = "lastsendaddresstime"; // 格式化地址
    public static final String DEVICETYPE = "deviceType";//绑定的笔的设备类型

    public static final String LOGIN_INFO = "login_info";//用户登录信息

    /**
     * 有关笔的信息
     */
    //笔的全部存储空间
    public static final String ALLSPACE = "allSpace";
    //笔中剩余的存储空间
    public static final String SURPLUSSPACE = "surplusSpace";
    //笔的电量
    public static final String LEVEL = "level";
    //笔中App的版本号
    public static final String VERSION = "version";
    //笔的Id(唯一标识）
    public static final String PENID = "penId";
    //笔当前连接到的wifi名称（可能为空）
    public static final String WIFINAME = "wifiName";
    //语音类型
    public static final String VOICE_TYPE = "voice_type";

    //商城功能里面的搜索记录
    public static final String SEARCH_HISTORY = "search_history";
    //所有的商品名称
    public static final String ALL_SHOP_NAMES = "all_shop_names";
    //用户是否是第一次使用支付
    public static final String IS_FIRST_PAY = "is_first_pay";
    /**
     * 用户中心cookie
     */
    public static final String COOKIE_UCENTERKEY = "ucenterKey";

    private static Context CONTEXT;

    /**
     * 初始化context，需要此方法，可以模块化单元化测试
     *
     * @param context
     */
    public static void initContext(Context context) {
        CONTEXT = context;
    }


    /***
     * 判断 传入的地址 是否与 LinuxAdd
     * @return true linux false android
     */
    public static boolean isLinux(String spaddr) {

        final String strAddress = MyApplication.application.getSharedPreferences("settings",
                Context.MODE_PRIVATE).getString("unique_address", " XXXXXX ");

        Log.d(" ********* ", "isLinux: " + strAddress + "  " + spaddr);

        return spaddr.equals(strAddress);

    }

    /**
     * 保存数据的方法，拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static synchronized void put(String key, Object object) {
        Log.i(Contants.SPUTILS_TAG, "存入sp的内容" + key + " " + object.toString());
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，根据默认值得到保存的数据的具体类型，然后调用相对应的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject) {
        if (CONTEXT == null) {
            CONTEXT = MyApplication.application;
        }
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public static Map<String, ?> getAll() {
        SharedPreferences sp = CONTEXT.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }
}
