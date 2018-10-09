package com.mpen.bluetooth.controller;

import android.content.Context;

/**
 * Created by LYH on 2018/1/24.
 * 蓝牙流程控制
 * 仿照 笔里增加 流程控制
 */

public class BluetoothController {

    private static BluetoothController btController;
    private BluetoothManager bluetoothManager;
    private BluetoothDataProcessor bluetoothDataProcessor;

    private Context context;

    public BluetoothController(Context context) {
        this.context = context;
        bluetoothDataProcessor = new BluetoothDataProcessor();

        bluetoothManager = BluetoothManager.getInstance();
        bluetoothManager.initContext(this.context);
        bluetoothManager.setListener(bluetoothDataProcessor);//业务层 的方法 传递过去
    }

    public static void initController(Context context) {
        btController = new BluetoothController(context);
    }
}
