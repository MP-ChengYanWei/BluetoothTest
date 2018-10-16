package com.mp.sharedandroid.network;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by feng on 3/25/17.
 *
 * 统一用Gson来解析服务器后台
 *
 * 以下例子说明使用方法：
 static final Type codeInfoResultType =
            new TypeToken<NetworkResult<CodeInfo>>() {}.getType();
 static final ResultParse codeInfoParse =
            new ResultParse(codeInfoResultType);
 *
 */

public final class ResultParse implements MPAbsParse {
    public static final Gson GSON = new Gson();

    private final Type classT;

    public ResultParse(Type classT) {
        this.classT = classT;
    }

    /**
     * response 解析抽象类
     *
     * @param response
     * @return
     */
    @Override
    public Object parse(String response) {
        try {
            return GSON.fromJson(response, classT);
        } catch (Exception e) {
            Log.e("ResultParse", "Exception:" + e.getMessage());
            return null;
        }
    }
}
