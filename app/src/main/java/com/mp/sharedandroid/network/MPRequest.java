package com.mp.sharedandroid.network;

import android.os.Handler;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mp.sharedandroid.utils.FileUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

/**
 * Created by wutingyou on 2017/1/9.
 * 网络请求实体
 */

public final class MPRequest {
    private static final String TAG = "MPRequest";
    private int mMethod;
    private DataFormat mDataFormat;
    private String mDetailUrl;
    private Map<String, String> mParams;            // String format的参数
    private String mJsonStringParams;
    private boolean mUseCDN;
    private Request mReqeust;
    private MPNetworkListener mListener;
    private MPAbsParse mParse;
    private HashMap<String, String> mHeaders = new HashMap<String, String>();
    private WeakReference<MPNetworkAgent> mAgent;
    private Handler mHandler;      // 网络模块内部处理handler（目前主要是处理数据解析），由外部传入

    public int mDataType = 0;     // 自定义数据类型
    public Object mData = null;   // 自定义数据

    private Gson gson = new Gson();

    /**
     * 请求方式类
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
    }

    /**
     * 请求类型
     */
    public enum DataFormat {
        STRING_FORMAT,
        JSON_FORMAT,
        BINARY_FORMAT
    }

    /**
     * url 构造
     *
     * @param request 请求实体
     * @return 实际请求url
     */
    private String buildUrl(MPRequest request) {
        if (request.getDetailUrl().startsWith("http")) {
            return request.getDetailUrl();
        } else {
            if (request.useCDN()) {
                return MPNetworkConfig.CDN_URL + request.getDetailUrl();
            } else {
                return MPNetworkConfig.BASE_URL + request.getDetailUrl();
            }
        }
    }

    /**
     * 检查是否服务器返回时间不同步的错误，如果是则调用本地时间同步方法
     */
    private void checkTimeAndSync(String response) {
        Log.d(TAG, "checkTimeAndSync：" + response);
        try {
            final JSONObject responseJson = new JSONObject(response);
            final String errorCode = responseJson.optString("errorCode");
            final String errorMsg = responseJson.optString("errorMsg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMPResponseWithHandler(final Object object) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMPResponse(object);
                }
            });
        } else {
            onMPResponse(object);
        }
    }

    private void onMPErrorResponseWithHandler(final VolleyError volleyError) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onMPErrorResponse(volleyError);
                }
            });
        } else {
            onMPErrorResponse(volleyError);
        }
    }

    private void onMPResponse(Object object) {
        final String s = object.toString();
        checkTimeAndSync(s);
        if (mListener != null) {
            if (mParse != null) {
                Object obj = mParse.parse(s);
                if (obj == null) {
                    mListener.onFailed(MPRequest.this, s, MPNetworkListener.FailedReason.PARSE_ERROR, "parse failure");
                } else {
                    mListener.onResponse(MPRequest.this, obj, s);
                }
            } else {
                mListener.onResponse(MPRequest.this, null, s);
            }
        }
        if (mAgent.get() != null) {
            mAgent.get().decreaseRequestNumber();
        }
    }

    private void onMPErrorResponse(VolleyError volleyError) {
        MPNetworkListener.FailedReason failedReason = MPNetworkListener.FailedReason.NETWORK_ERROR;
        if (volleyError instanceof TimeoutError) {
            failedReason = MPNetworkListener.FailedReason.TIMEOUT_ERROR;
        } else if (volleyError instanceof NoConnectionError) {
            failedReason = MPNetworkListener.FailedReason.NO_CONNECTION_ERROR;
        } else if (volleyError instanceof AuthFailureError) {
            failedReason = MPNetworkListener.FailedReason.AUTH_FAILURE_ERROR;
        } else if (volleyError instanceof ServerError) {
            failedReason = MPNetworkListener.FailedReason.SERVER_ERROR;
        } else if (volleyError instanceof NetworkError) {
            failedReason = MPNetworkListener.FailedReason.NETWORK_ERROR;
        } else if (volleyError instanceof ParseError) {
            failedReason = MPNetworkListener.FailedReason.PARSE_ERROR;
        }

        if (mListener != null) {
            mListener.onFailed(MPRequest.this, volleyError.getMessage(), failedReason, volleyError.getMessage());
        }
        if (mAgent.get() != null) {
            mAgent.get().decreaseRequestNumber();
        }
    }

    /**
     * 根据类型和参数构造请求对象
     *
     * @param dataFormat 请求类型
     * @param params     参数
     */
    private void createRequest(DataFormat dataFormat, Object params) {
        if (DataFormat.STRING_FORMAT == dataFormat) {
            if (params == null) {
                // 主要是为了兼容okhttp模式 这个模式必须要带参数
                mParams = new HashMap<String, String>();
                mParams.put("space_filler_should_not_use", "do_not_use");
            } else {
                if (params instanceof Map) {
                    mParams = (Map<String, String>) params;
                } else {
                    Log.e(TAG, "params error");
                }
            }
            mReqeust = new VolleyRequest(mMethod, buildUrl(this), new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    onMPResponseWithHandler(s);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    onMPErrorResponseWithHandler(volleyError);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return mParams;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return mHeaders;
                }
            };

        } else if (DataFormat.JSON_FORMAT == dataFormat) {
            if (params == null) {
                // 主要是为了兼容okhttp模式 这个模式必须要带参数
                Map temp = new HashMap<String, String>();
                temp.put("space_filler_should_not_use", "do_not_use");
                mJsonStringParams = gson.toJson(temp);
            } else {
                mJsonStringParams = gson.toJson(params);
            }
            Log.d(TAG, "paramsString:" + mJsonStringParams);
            mReqeust = new JsonStringRequest(mMethod, buildUrl(this), mJsonStringParams, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onMPResponseWithHandler(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onMPErrorResponseWithHandler(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return mHeaders;
                }
            };
        } else if (mMethod == Method.GET && DataFormat.BINARY_FORMAT == dataFormat) {
            final String mpDownloadFile = FileUtils.getSDPath() + "/mpDownloadFile";
            final OkHttpClient okHttpClient = new OkHttpClient();
            final okhttp3.Request request = new okhttp3.Request.Builder().url(buildUrl(this)).build();
            okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    if (mListener != null) {
                        mListener.onFailed(MPRequest.this, e.getMessage(), null, e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    if (FileUtils.inputStreamToFile(inputStream, mpDownloadFile)) {
                        if (mListener != null) {
                            mListener.onResponse(MPRequest.this, mpDownloadFile, mpDownloadFile);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onFailed(MPRequest.this, "文件下载异常", null, "文件下载异常");
                        }
                    }
                }
            });
        } else if (mMethod == Method.POST && DataFormat.BINARY_FORMAT == dataFormat) {
            if (params == null) {
                return;
            }
            if (params instanceof Map) {
                mParams = (Map<String, String>) params;
            } else {
                Log.e(TAG, "params error");
                return;
            }

            mReqeust = new MultipartRequest(buildUrl(this), mParams, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    onMPResponseWithHandler(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    onMPErrorResponseWithHandler(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Log.d(TAG, "getHeaders()");
                    return mHeaders;
                }
            };
        }
    }

    /**
     * 添加随机数参数
     *
     * @param url url
     * @return 添加随机数之后的url
     */
    private String addRandomParam(String url) {
        final int random = new Random().nextInt(1024);
        final String param = url.indexOf("?") >= 0 ? "&random=" + random : "?random=" + random;
        return url + param;
    }

    /**
     * 网络请求构造
     *
     * @param method     请求方式
     * @param dataFormat 请求类型
     * @param url        地址
     * @param params     请求参数，STRING_FORMAT类型时，此参数只能是Map类型 JSON_FORMAT,参数类型不限制
     * @param parse      解析对象
     * @param listener   监听
     */
    private MPRequest(int method, DataFormat dataFormat, String url, Object params, MPAbsParse parse, Handler handler, MPNetworkListener listener) {
        this(method, dataFormat, url, true, params, parse, handler, listener);
    }

    /**
     * 网络请求构造
     *
     * @param method     请求方式
     * @param dataFormat 请求类型
     * @param url        地址
     * @param params     请求参数，STRING_FORMAT类型时，此参数只能是Map类型 JSON_FORMAT,参数类型不限制
     * @param parse      解析对象
     * @param listener   监听
     */
    private MPRequest(int method, DataFormat dataFormat, String url, boolean isAddRandom, Object params, MPAbsParse parse, Handler handler, MPNetworkListener listener) {
        mMethod = method;
        mDataFormat = dataFormat;
        // Url 增加随机数参数
        String urlStr = MPNetworkHelper.getUrlDecodeStrTsCH(url);
        mDetailUrl = isAddRandom ? addRandomParam(urlStr) : urlStr;
        Log.d(TAG, "mDetailUrl:" + mDetailUrl);
        mParse = parse;
        mListener = listener;
        mHandler = handler;
        createRequest(dataFormat, params);

        /**
         * 当MPNetworkConfig.SHOULD_CACHE == MPNetworkConfig.CACHE_TYPE.LOCAL_DECISION
         * request自己内部处理
         */
        if (MPNetworkConfig.SHOULD_CACHE != MPNetworkConfig.CACHE_TYPE.LOCAL_DECISION) {
            if (MPNetworkConfig.SHOULD_CACHE == MPNetworkConfig.CACHE_TYPE.FALSE) {
                setShouldCache(false);
            } else {
                setShouldCache(true);
            }
        }
    }

    /**
     * 静态方法构造
     *
     * @param method     请求方式
     * @param dataFormat 请求类型
     * @param url        地址
     * @param params     请求参数，STRING_FORMAT类型时，此参数只能是Map类型 JSON_FORMAT,参数类型不限制
     * @param parse      解析对象
     * @param listener   监听
     */
    public static MPRequest newRequest(int method, DataFormat dataFormat, String url, Object params, MPAbsParse parse, Handler handler, MPNetworkListener listener) {
        return new MPRequest(method, dataFormat, url, params, parse, handler, listener);
    }

    /**
     * 静态方法构造
     *
     * @param method     请求方式
     * @param dataFormat 请求类型
     * @param url        地址
     * @param params     请求参数，STRING_FORMAT类型时，此参数只能是Map类型 JSON_FORMAT,参数类型不限制
     * @param parse      解析对象
     * @param listener   监听
     */
    public static MPRequest newRequest(int method, DataFormat dataFormat, String url, boolean isAddRandom, Object params, MPAbsParse parse, Handler handler, MPNetworkListener listener) {
        return new MPRequest(method, dataFormat, url, isAddRandom, params, parse, handler, listener);
    }

    /**
     * 获取参数
     *
     * @return
     */
    public Map<String, String> getParams() {
        return mParams;
    }

    /**
     * 设置cdn
     *
     * @param cdn
     */
    public void setCDN(boolean cdn) {
        mUseCDN = cdn;
    }

    /**
     * 获取是否用了cdn
     *
     * @return
     */
    public boolean useCDN() {
        return mUseCDN;
    }

    /**
     * 获取请求方式
     *
     * @return
     */
    public int getMethod() {
        return mMethod;
    }

    /**
     * 获取详细url
     *
     * @return
     */
    public String getDetailUrl() {
        return mDetailUrl;
    }

    /**
     * 设置header
     *
     * @param map
     */
    public void setHeader(HashMap<String, String> map) {
        mHeaders = map;
    }

    /**
     * 获取header
     *
     * @return
     */
    public HashMap<String, String> getHeader() {
        return mHeaders;
    }

    /**
     * 获取StringRequest
     *
     * @return
     */
    public Request getRequest() {
        if (mListener != null) {
            mListener.onPreRequest(MPRequest.this);
        }
        return mReqeust;
    }

    /**
     * 设置是否请求用到缓存
     *
     * @param shouldCache
     */
    public void setShouldCache(boolean shouldCache) {
        mReqeust.setShouldCache(shouldCache);

    }

    public void setAgent(MPNetworkAgent agent) {
        mAgent = new WeakReference<MPNetworkAgent>(agent);
    }

    public void setDecodeCharSet(String charSet){
        MPNetworkHelper.setDecodeCharSet(charSet);
    }

}
