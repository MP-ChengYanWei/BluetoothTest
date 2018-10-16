package com.mp.sharedandroid.network;

/**
 * Created by wutingyou on 2017/1/6.
 * <p>
 * 网络配置
 */

public class MPNetworkConfig {

    enum CACHE_TYPE {
        LOCAL_DECISION, TRUE, FALSE
    }

    /**
     * host
     */
    public static String BASE_URL;

    /**
     * cdn
     */
    public static String CDN_URL;

    /**
     * should cache
     * 全局配置，默认LOCAL_DECISION
     */
    public static CACHE_TYPE SHOULD_CACHE = CACHE_TYPE.FALSE;
}
