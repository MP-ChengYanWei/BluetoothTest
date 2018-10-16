package com.mp.sharedandroid.network;

/**
 * Created by wutingyou on 2017/1/6.
 * <p>
 * 网络回调
 */

public interface MPNetworkListener {

    enum FailedReason {
        AUTH_FAILURE_ERROR,         // 身份验证失败
        NO_CONNECTION_ERROR,        // 没有网络连接
        TIMEOUT_ERROR,              // 超时错误，服务器忙或者网络超时
        SERVER_ERROR,               // 服务器响应错误
        NETWORK_ERROR,              // socket关闭，服务器宕机，DNS错误
        PARSE_ERROR                 // 解析错误
    }


    /**
     * 即将请求回调
     */
    void onPreRequest(final MPRequest request);

    /**
     * response回调
     *
     * @param obj
     * @param strResponse
     */
    void onResponse(final MPRequest request, Object obj, String strResponse);

    /**
     * 错误回调
     *
     * @param strResponse
     * @param reason
     * @param failureMessage
     */
    void onFailed(final MPRequest request, String strResponse, FailedReason reason, String failureMessage);
}
