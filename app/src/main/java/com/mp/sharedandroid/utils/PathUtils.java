package com.mp.sharedandroid.utils;

import android.content.Context;
import android.os.Environment;

/**
 * Created by wutingyou on 2018/4/3.
 * 应用本地文件路径模块
 * TODO:应用所有路径在此添加
 */

public class PathUtils {
    private static Context context;
    public static String BASE_FOLDER_PATH;
    public static String BASE_PATH;
    public static String BASE_VIDEO_PATH;

    public static String VIDEO_PATH;
    public static String IMAGE_PATH;
    public static String FILE_PATH;

    public static String APK_PATH;
    public static String ROOT_PATH;

    /**
     * 初始化pathUtils
     * @param context
     */
    public static void initContext(Context context) {
        PathUtils.context = context;
        initPath();
    }

    /**
     * 初始化所有相关路径
     */
    private static void initPath() {
        BASE_FOLDER_PATH = context.getFilesDir().toString();
        BASE_PATH = BASE_FOLDER_PATH;
        BASE_VIDEO_PATH = BASE_FOLDER_PATH;
        VIDEO_PATH = BASE_VIDEO_PATH + "/video/";
        IMAGE_PATH = BASE_PATH + "/image/";
        FILE_PATH = BASE_PATH + "/file/";
        APK_PATH = Environment.getExternalStorageDirectory().getPath() + "/update/";
        ROOT_PATH = "/";
    }
}
