package com.mp.sharedandroid.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

/**
 * Created by wutingyou on 2017/3/27.
 * 自定义 JsonStringRequest 请求
 */

public class JsonStringRequest extends JsonRequest<String> {

    /**
     * Creates a new request.
     *
     * @param method            the HTTP method to use
     * @param url               URL to fetch the JSON from
     * @param jsonStringRequest A {@link String} to post with the request. Null is allowed and
     *                          indicates no parameters will be posted along with request.
     * @param listener          Listener to receive the String response
     * @param errorListener     Error listener, or null to ignore errors.
     */
    public JsonStringRequest(int method, String url, String jsonStringRequest,
                             Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonStringRequest == null) ? null : jsonStringRequest, listener,
                errorListener);
    }

    /**
     * parseNetworkResponse
     *
     * @param response response数据
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        final String data = MPNetworkHelper.gzipParse(response);
        if (data == null) {
            return Response.error(new ParseError());
        }
        return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
