package com.mpen.bluetooth.androidbt;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.mpen.bluetooth.controller.BluetoothManager;

/**
 * Created by LYH on 2018/1/23.
 * 类似于 DDBBluetoothService
 * Android 蓝牙
 */

public class AndroidBluetooth implements BluetoothSPP.BluetoothConnectionListener, BluetoothSPP.OnDataReceivedListener {

    private static final String TAG = "AndroidBluetooth";
    private static BluetoothSPP mBluetoothSPP;

    public static BluetoothSPP getmBluetoothSPP() {
        return mBluetoothSPP;
    }

    private static AndroidBluetooth androidBluetooth = null;

    /***************外部接口**************/
    public static void initAndroidBluetooth(Context context) {
        Log.d(TAG, "btService onCreate  initAndroidBluetooth");
        mBluetoothSPP = new BluetoothSPP(context);
        androidBluetooth = new AndroidBluetooth();
        if (!mBluetoothSPP.isBluetoothEnabled()) {
            mBluetoothSPP.enable();
        }
        if (!mBluetoothSPP.isServiceAvailable()) {
            mBluetoothSPP.setupService();
            mBluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
        }

        mBluetoothSPP.setBluetoothConnectionListener(androidBluetooth);
        mBluetoothSPP.setOnDataReceivedListener(androidBluetooth);

        Log.e(TAG, "蓝牙服务的状态 " + mBluetoothSPP.getServiceState());
    }

    public static AndroidBluetooth getInstance() {
        if (androidBluetooth == null) {
            Log.e(TAG, "代码错误：DianduController没有被初始化");
        }

        return androidBluetooth;
    }

    /************** 外部接口 end ************/

    /**
     * 去连接
     *
     * @param macAddress 要连接的蓝牙地址
     */
    public void connect(String macAddress) {
        if (TextUtils.isEmpty(macAddress) && !"null".equals(macAddress)) {
            return;
        }
        if (mBluetoothSPP == null) {
            return;
        }
        Log.i(TAG, "连接MAC: " + macAddress);
        mBluetoothSPP.disconnect();
        mBluetoothSPP.connect(macAddress);
    }

    /***
     * 主动断开 蓝牙
     */
    public void onDisconnected() {
        mBluetoothSPP.disconnect();
        mBluetoothSPP.stopService();
    }

    public void startDiscovery() {

        if (mBluetoothSPP.isDiscovery()) {
            mBluetoothSPP.cancelDiscovery();
            SystemClock.sleep(100);
        }
        mBluetoothSPP.startDiscovery();    //开始搜索
    }


    //停止搜寻设备
    public static void cancelDiscovery() {
        if (mBluetoothSPP.isDiscovery()) {
            mBluetoothSPP.cancelDiscovery();
        }
    }

    //发送数据
    public static void send(String msgdata) {
        mBluetoothSPP.send(msgdata, true);
    }

    /***
     * 蓝牙连接成功
     * @param name
     * @param address
     */
    @Override
    public void onDeviceConnected(String name, String address) {
        Log.d(TAG, "onDeviceConnected: " + name + " 连接成功！" + address);
        BluetoothManager.getInstance().isConnenct = true;
        BluetoothManager.getInstance().onConnectionStateChange(BluetoothManager.deviceType.ANDROID_BT_TYPE, mBluetoothSPP.getServiceState());

    }

    /***
     * 蓝牙连接断开
     */
    @Override
    public void onDeviceDisconnected() {
        Log.d(TAG, "onDeviceDisconnected: " + " app和笔的蓝牙连接断开了");
        BluetoothManager.getInstance().onDeviceDisconnected();
    }

    /***
     * 蓝牙连接失败
     */
    @Override
    public void onDeviceConnectionFailed() {
        Log.i(TAG, "onDeviceConnectionFailed: " + " app和笔的蓝牙连接失败了");
        BluetoothManager.getInstance().isConnenct = false;
        BluetoothManager.getInstance().isSendPaly = false;
        BluetoothManager.getInstance().onDeviceDisconnected();
    }

    @Override
    public void onDataReceived(byte[] data, String message) {
        BluetoothManager.getInstance().onReveiveData(message, data);
    }

}
