package com.mp.sharedandroid.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by wutingyou on 2017/1/21.
 * 自定义request
 */

public class VolleyRequest extends Request<String> {
    private final Response.Listener<String> mListener;

    /**
     * 创建一个request
     *
     * @param method
     * @param url
     * @param listener
     * @param errorListener
     */
    public VolleyRequest(int method, String url, Response.Listener<String> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }


    /**
     * 创建一个get request
     *
     * @param url
     * @param listener
     * @param errorListener
     */
    public VolleyRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        final String data = MPNetworkHelper.gzipParse(response);
        if (data == null) {
            return Response.error(new ParseError());
        }
        return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
