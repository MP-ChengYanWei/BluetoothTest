package com.mp.sharedandroid.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;

/**
 * Created by wutingyou on 2017/1/9.
 * volley代理类
 */

public final class MPNetworkAgent {

    private static final int VOLLEY = 0;
    private static final int OK_HTTP = 1;
    private Context mContext;
    private RequestQueue mQueue;
    private int mEngineType;

    private AtomicInteger mRequestNumber = new AtomicInteger(0);

    private MPNetworkAgent() {

    }

    private static MPNetworkAgent INSTANCE = new MPNetworkAgent();

    /**
     * 对象获取
     *
     * @return
     */
    public static MPNetworkAgent getInstance() {
        return INSTANCE;
    }

    /**
     * RequestQueue初始化
     *
     * @param context
     */
    public void initContext(Context context) {
        mContext = context;
        //设置https访问
        OkHttpClient client = HTTPSTrustManager.allowOkHttpSSL();
        // 默认以OKHttp作为传输层；
        mQueue = Volley.newRequestQueue(mContext, new OkHttp3Stack(client));
        mQueue.getCache().clear();
    }


    public static int NETWORK_TIMEOUT_IN_MS = 6000; // normal 2500

    public static DefaultRetryPolicy RETRY_POLICY = new DefaultRetryPolicy(
            NETWORK_TIMEOUT_IN_MS,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    /**
     * 添加请求实体，触发网络访问
     *
     * @param request
     */
    public void addRequest(MPRequest request) {
        Log.d("MPNetworkAgent", "addRequest");
        if (request == null || request.getRequest() == null) {
            return;
        }
        request.setAgent(this);
        request.getRequest().setRetryPolicy(RETRY_POLICY);
        if (mQueue != null) {
            mQueue.add(request.getRequest());
        }
        increaseRequestNumber();
    }

    public void setEngine(int type) {
        mEngineType = type;
        if (mEngineType == VOLLEY) {
            mQueue = Volley.newRequestQueue(mContext);
        } else {
            mQueue = Volley.newRequestQueue(mContext, new OkHttp3Stack(new OkHttpClient()));
        }
    }

    public int getRequestNumber() {
        return mRequestNumber.get();
    }

    public void increaseRequestNumber() {
        mRequestNumber.incrementAndGet();
    }

    public void decreaseRequestNumber() {
        mRequestNumber.decrementAndGet();
    }
}
