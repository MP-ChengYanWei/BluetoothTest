package com.mpen.bluetooth.common;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by wutingyou on 2017/4/10.
 * 和手机端共享的蓝牙统一协议数据格式封装
 * type:操作类型
 * data：数据内容
 */

public class BtDataResult {
    public int type = -1;
    public Object data;
    private Type typeGson;

    private String dataStr;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setDataStr(String str) {
        dataStr = str;
    }

    private static Gson gson = new Gson();
    private static Type btDataResultType = new TypeToken<BtDataResult>() {
    }.getType();
    //按照顺序定义转换类型
    private static final Type[] OP_TYPES={
            new TypeToken<WifiSet>() {}.getType(),
            new TypeToken<BookDownloadIds>() {}.getType(),
            new TypeToken<BookDownloadId>() {}.getType(),
            new TypeToken<VoiceType>() {}.getType(),
            new TypeToken<ScreenName>() {}.getType(),
            new TypeToken<AppInfo>() {}.getType(),
            new TypeToken<BoxInfo>() {}.getType(),
            new TypeToken<RegisterInfo>() {}.getType()
    };

    /**
     * 获取对象
     *
     * @param <T>
     * @return
     */
    public <T> T getObject() {
        return gson.fromJson(dataStr, typeGson);
    }

    public static BtDataResult build(String json) {
        BtDataResult btDataResult = gson.fromJson(json, btDataResultType);
        final String objString = parseGetJsonData(json);
        if(!TextUtils.isEmpty(objString)){
            btDataResult.setDataStr(objString);
        }
        return btDataResult;
    }

    /**
     * 临时处理iphone4s-iphone5传输数据解析
     * @param json
     * @return
     */
    //// TODO: 2017/9/1  因为在btdataresult.object.toString()时候，会将“”转换为成员变量null，导致后续出错，
    //// TODO: 2017/9/1  所以此处进行临时转换，防止Object某个成员变量为“”时崩溃报错
    private static String parseGetJsonData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            final String data = jsonObject.optString("data");
            return data;
        } catch (JSONException e) {
            Log.e("btResult",e.toString());
            return null;
        }
    }
    /**
     * 转化String
     *
     * @return
     */
    public String toJson() {
        return gson.toJson(this);
    }
}
