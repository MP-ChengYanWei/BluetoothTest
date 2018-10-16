package com.mp.sharedandroid.network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by wutingyou on 2017/1/21.
 * 自定义request，此处是参考源码自定义实现。
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private Response.Listener<String> mListener;
    private Map<String, String> mParams;
    private String contentid;

    /**
     * 单个文件+参数上传
     *
     * @param url
     * @param params
     * @param listener
     * @param errorListener
     */
    public MultipartRequest(String url, Map params, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        mParams = params;
        buildMultipartEntity();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        final String data = MPNetworkHelper.gzipParse(response);
        if (data == null) {
            return Response.error(new ParseError());
        }
        return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        Log.d("MultipartRequest", "getBodyContentType");
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        Log.d("MultipartRequest", "getBody");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    private void buildMultipartEntity() {
        if (mParams != null && mParams.size() > 0) {
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                final String value = entry.getValue();
                Log.d("MultipartRequest", "buildMultipartEntity value:" + value);
                if (value.startsWith("@")) {
                    final String fileName = value.substring(1);
                    final File file = new File(fileName);
                    entity.addPart(entry.getKey(), new FileBody(file));
                } else {
                    try {
                        entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

