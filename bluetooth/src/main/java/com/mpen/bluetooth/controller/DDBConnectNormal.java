package com.mpen.bluetooth.controller;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mpen.bluetooth.common.BtDataResult;
import com.mpen.bluetooth.common.OperationType;
import com.mpen.bluetooth.init.MpenBluetooth;
import com.mpen.bluetooth.utils.ResultUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by LYH on 2018/2/5.
 * 根据业务数据，将map 转成json *
 *
 */

public class DDBConnectNormal  {
    private static final String TAG = "DDBConnectNormal";
    private Context context;

    public DDBConnectNormal() {
        this.context = MpenBluetooth.getInstance().getContext();
    }

    public void sendRequest(OperationType operate, Object obj) {
        if (obj == null || obj instanceof Map || obj instanceof List || obj instanceof BtDataResult) {
            String send = this.turnToJson(operate, obj);
            Log.d(TAG, "sendRequest: " + send);
            sendMsg(send);
        } else {
            Log.e(TAG, "只支持Map和List类型");
        }
    }

    public void sendRequest(int operate, Object obj) {
        if (obj == null || obj instanceof Map || obj instanceof List || obj instanceof BtDataResult) {
            String send = this.turnToJson(operate, obj);
            Log.d(TAG, "sendRequest: " + send);
            sendMsg(send);
        } else {
            Log.e(TAG, "只支持Map和List类型");
        }
    }


    /**
     * 调用蓝牙发送程序
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        BluetoothManager.getInstance().sendData(msg);
    }

    public final Gson gson = new Gson();

    /***
     * Object 转 Json
     * json :{type:operate, data: map}
     * @param operate type
     * @param map   data
     * @return
     */
    public String turnToJson(OperationType operate, Object map) {
        Object obj= ResultUtils.resultToStandardData(map, operate);
        String msg=gson.toJson(obj);
        return msg;
    }

    /***
     * Object 转 Json
     * json :{type:operate, data: map}
     * @param operate type
     * @param map   data
     * @return
     */
    public String turnToJson(int operate, Object map) {
        Object obj= ResultUtils.resultToStandardData(map, operate);
        String msg=gson.toJson(obj);
        return msg;
    }
}
