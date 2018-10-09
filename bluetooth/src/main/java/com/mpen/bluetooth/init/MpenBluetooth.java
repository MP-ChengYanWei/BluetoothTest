package com.mpen.bluetooth.init;

import android.content.Context;

import com.mpen.bluetooth.controller.BluetoothController;


/**
 * 初始化蓝牙模块
 * Created by cyw on 2018/6/4.
 */

public class MpenBluetooth {

    private static MpenBluetooth instance;
    private Context context;

    public static MpenBluetooth getInstance() {
        if (instance == null) {
            synchronized (MpenBluetooth.class) {
                if (instance == null) {
                    instance = new MpenBluetooth();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化蓝牙
     */
    public void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
            BluetoothController.initController(context);
        }
    }

    /**
     * 单例
     */
    public Context getContext() {
        return context;
    }
}
