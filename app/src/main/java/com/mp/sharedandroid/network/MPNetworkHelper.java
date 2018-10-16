package com.mp.sharedandroid.network;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by wutingyou on 2017/1/6.
 * <p>
 * 网络相关帮助类
 */

public class MPNetworkHelper {

    /**
     * 将参数转换为字符串
     *
     * @param params 参数
     */
    public static String convertMapToString(Map<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            content.append(entry.getKey()).append('=');
            if (entry.getValue() != null) {
                content.append(entry.getValue());
            }
            content.append('&');
        }
        content.setLength(content.length() - 1);
        return content.toString();
    }


    /**
     * 将参数进行URLEncode编码转换
     *
     * @param params
     * @return
     */
    public static Map<String, String> getURLEncodeParams(Map<String, String> params) {
        if (params.isEmpty()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(entry.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            map.put(entry.getKey(), value);
        }
        return map;
    }

    /**
     * 拼接url和参数，用于Get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        params = getURLEncodeParams(params);
        String paramsStr = convertMapToString(params);
        if (!paramsStr.isEmpty()) {
            if (!url.endsWith("?")) {
                url += "?";
            }
            url += paramsStr;
        }
        return url;
    }

    /**
     * 将普通字符串转化为url规范字符串
     *
     * @param str
     * @return
     */
    public static String getUrlEncodeString(String str) {
        String value = null;
        try {
            value = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 将url规范字符串转化为普通字符串
     *
     * @param str
     * @return
     */
    public static String getUrlDecodeString(String str) {
        String value = null;

        try {
            value = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * 将url里的中文进行编码转换
     *
     * @param url
     * @return
     */
    public static String getUrlDecodeStrTsCH(String url) {
        String str = Uri.encode(url, "-![.:/,%?&=]()$+'~@;?|\\\\");
        return str;
    }

    /**
     * gzip 解析
     *
     * @param response
     * @return
     */
    public static String gzipParse(NetworkResponse response) {
        String parseStr = null;
        if (response.headers.containsKey("Content-Encoding") &&
                response.headers.get("Content-Encoding").equals("gzip")) {
            final StringBuilder output = new StringBuilder();
            try {
                final GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(response.data));
                final InputStreamReader reader = new InputStreamReader(gStream);
                final BufferedReader in = new BufferedReader(reader, 16384);
                String read;
                while ((read = in.readLine()) != null) {
                    output.append(read).append("\n");
                }
                reader.close();
                in.close();
                gStream.close();
            } catch (IOException e) {
                return null;
            }
            parseStr = output.toString();
        } else {
            try {
                parseStr = new String(response.data, TextUtils.isEmpty(charset) ? HttpHeaderParser.parseCharset(response.headers) : charset);
            } catch (UnsupportedEncodingException e) {
                parseStr = new String(response.data);
            }
        }
        return parseStr;
    }

    private static String charset;

    public static void setDecodeCharSet(String decodeCharSet) {
        charset = decodeCharSet;
    }
}
