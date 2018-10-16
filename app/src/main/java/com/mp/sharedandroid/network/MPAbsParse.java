package com.mp.sharedandroid.network;

/**
 * Created by wutingyou on 2017/1/9.
 */

public interface MPAbsParse {
    /**
     * response 解析抽象类
     *
     * @param response
     * @return
     */
    Object parse(String response);
}
