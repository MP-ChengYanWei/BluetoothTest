package com.mp.sharedandroid.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.mp.sharedandroid.utils.FUtils;
import com.mp.sharedandroid.utils.SPUtils;
//import com.mp.shared.common.bean.UserInfo;
//import com.mp.shared.utils.FUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static java.lang.String.valueOf;

/**
 * Created by wutingyou on 2017/1/6.
 * <p>
 * 网络请求类
 * 此模块因为与系统网络模块名字冲突增加了前缀MP
 */

public class MPNetwork {

    public static final int VOLLEY = 0;
    public static final int OK_HTTP = 1;
    private static String PEN_ID;
    private static Context context;

    /**
     * 网络框架初始化
     *
     * @param context
     */
    public static void initContext(Context context) {
        MPNetwork.context = context;
        MPNetworkAgent.getInstance().initContext(context);
    }

    /**
     * 网络引擎设置
     *
     * @param type
     */
    public static void setEngine(int type) {
        MPNetworkAgent.getInstance().setEngine(type);
    }

    /**
     * 网络请求添加
     *
     * @param request 请求实体
     */
    public static void addRequest(MPRequest request) {
        MPNetworkAgent.getInstance().addRequest(request);
    }

//    /**
//     * 网络请求添加
//     *
//     * @param request 请求实体
//     */
//    public static void addTokenRequest(MPRequest request) {
//        HashMap header = new HashMap();
//        UserInfo.Token token = accessToken;
//        header.put("Authorization", token.getToken_type() + " " + token.getAccess_token());
//        request.setHeader(header);
//        MPNetworkAgent.getInstance().addRequest(request);
//    }

    public static void addTokenRequest(MPRequest request) {
        if (header != null) {
            request.setHeader(header);
        }
        MPNetworkAgent.getInstance().addRequest(request);
    }

    private static HashMap header = null;

    public static void setHeader(HashMap customHeader) {
        header = customHeader;
    }

    /**
     * 网络请求添加
     *
     * @param request 请求实体
     */
//    public static void addHeaderAndRequest(MPRequest request) {
//        final HashMap header = new HashMap();
//        final String userAgent = getSolvedAgent();
//        String str = "loginId=" + SPUtils.get(SPUtils.LOGINID, "");
//        header.put("cookie", str);
//        header.put("User-Agent", userAgent);
//        header.put("Referer", "mpenAndroid");
//        header.put("Content-Encoding", "gzip");
//        request.setHeader(header);
//        Log.d("request", "request url " + request.getDetailUrl());
//        MPNetworkAgent.getInstance().addRequest(request);
//    }

    /**
     * 网络请求添加
     *
     */
//    public static void addHeaderAndRequest(MPRequest request, String decodeCharset) {
//        request.setDecodeCharSet(decodeCharset);
//        final HashMap header = new HashMap();
//        final String userAgent = getSolvedAgent();
//        String str = "loginId=" + SPUtils.get(SPUtils.LOGINID, "");
//        header.put("cookie", str);
//        header.put("User-Agent", userAgent);
//        header.put("Referer", "mpenAndroid");
//        header.put("Content-Encoding", "gzip");
//        request.setHeader(header);
//        Log.d("request", "request url " + request.getDetailUrl());
//        MPNetworkAgent.getInstance().addRequest(request);
//    }

    public static int getRequestNumber() {
        return MPNetworkAgent.getInstance().getRequestNumber();
    }

    /**
     * 检测当的网络状态
     *
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取user-agent
     *
     * @return
     */
    private static String getSolvedAgent() {
        try {
            final String randomStr = String.format("%04d", new Random().nextInt(9999));
            final String timeStr = String.valueOf(System.currentTimeMillis() / 1000);
            final String md5Str = FUtils.MD5(randomStr + timeStr);
            final int len = md5Str.length();
            final String md52IntResultStr = String.valueOf(Integer.valueOf(md5Str.substring(len - 4, len), 36));
            final String secret = md52IntResultStr.length() > 4 ?
                    md52IntResultStr.substring(0, 4) : String.format("%04d", Integer.valueOf(md52IntResultStr));
            return randomStr + timeStr + secret;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取cookie
     *
     * @return
     */
    private static String getSolvedCookie() {
        final HashMap sessionMap = new HashMap();
        sessionMap.put("penId", PEN_ID);
        final String cookie = "sessionId=" + new Gson().toJson(sessionMap);
        return cookie;
    }

    /**
     * 用户中心携带的cookie发起的请求
     */
    public static void addUCHeaderAndRequest(MPRequest request) {
        final String userAgent = getSolvedAgent();
        HashMap header = new HashMap();
        String loginId = (String) SPUtils.get(SPUtils.LOGINID, "");
        String str = "loginId=" + loginId + ";" + SPUtils.get(SPUtils.COOKIE_UCENTERKEY, "");
        header.put("cookie", str);
        header.put("User-Agent", userAgent);
        header.put("Referer", "mpenAndroid");
        header.put("Content-Encoding", "gzip");
        request.setHeader(header);
        MPNetworkAgent.getInstance().addRequest(request);
    }

    /**
     * 上传图片与音频
     */
    public static <T> void upLoadFile(String url, HashMap<String, String> paramsMap, String key, String filePath, Callback callback) {
        try {
            File file = new File(filePath);
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            MediaType MEDIA_TYPE;
            if (filePath.endsWith("amr")) {
                MEDIA_TYPE = MediaType.parse("audio/*");
            } else {
                MEDIA_TYPE = MediaType.parse("image/*");
            }
            if (file != null) {
                requestBody.addFormDataPart(key, file.getName(), RequestBody.create(MEDIA_TYPE, file));
            }
            if (paramsMap != null) {
                for (Map.Entry entry : paramsMap.entrySet()) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody.build())
                    .addHeader("Authorization", (String) header.get("Authorization"))
                    .tag(context)
                    .build();
            client.newBuilder().readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build().newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.e("MpnetWork", "uploadFailed!");
            Log.e("MpnetWork", e.getMessage());
        }
    }

    /**
     * 上传图片与音频
     */
    public static void upLoadMoreFile(String url, HashMap<String, String> paramsMap, String key, ArrayList<String> pathList, Callback callback) {
        try {
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i < pathList.size(); i++) {
                MediaType MEDIA_TYPE;
                File file = new File(pathList.get(i));
                if (pathList.get(i).endsWith("amr")) {
                    MEDIA_TYPE = MediaType.parse("audio/*");
                } else {
                    MEDIA_TYPE = MediaType.parse("image/*");
                }
                if (file != null) {
                    requestBody.addFormDataPart(key + "[" + i + "]", file.getName(), RequestBody.create(MEDIA_TYPE, file));
                }
            }
            if (paramsMap != null) {
                for (Map.Entry entry : paramsMap.entrySet()) {
                    requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
                }
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody.build())
                    .addHeader("Authorization", (String) header.get("Authorization"))
                    .tag(context)
                    .build();
            client.newBuilder().readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).build().newCall(request).enqueue(callback);
        } catch (Exception e) {
            Log.e("MpnetWork", "uploadFailed!");
            Log.e("MpnetWork", e.getMessage());
        }
    }
//
//    public static void setToken(UserInfo.Token token) {
//        accessToken = token;
//    }

}
