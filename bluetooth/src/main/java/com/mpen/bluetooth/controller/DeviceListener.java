package com.mpen.bluetooth.controller;

/**
 * 设备状态接口,提供连接监听和收数据接口
 */

public interface DeviceListener {
    /**
     * 监听设备连接状态的变化
     *
     * @param btType 设备类型，区分Android和Linux笔
     * @param state  设备状态
     */
    void onConnectionStateChange(BluetoothManager.deviceType btType, int state);

    /**
     * 监听接收数据的变化
     *
     * @param message  String的data
     */
    void onReveiveData(String message, byte[] data);

    /**
     * 尝试连接 对端
     * @param btType 对端设备类型,区分Android和Linux笔(笔中需要确认 蓝牙类型 BT or BLE)
     * @param address
     */
    void connect(BluetoothManager.deviceType btType, String address);
}
