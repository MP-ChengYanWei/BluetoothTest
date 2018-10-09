package com.mpen.bluetooth.controller;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.mpen.bluetooth.androidbt.AndroidBluetooth;
import com.mpen.bluetooth.bluetooth.Packet;
import com.mpen.bluetooth.bluetooth.PacketBuilder;
import com.mpen.bluetooth.bluetooth.PacketReceiver;
import com.mpen.bluetooth.common.DataRecord;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.init.MpenBluetooth;
import com.mpen.bluetooth.linuxbt.LinuxBluetooth;

import java.util.ArrayList;

import cn.ingenic.glasssync.services.SyncException;

/**
 * 蓝牙基础管理类，内部处理了Android和Linux笔的蓝牙数据处理，包括发送和接受
 * 统一处理后对外提供了接口 BluetoothListener
 * <p>
 * * readme：
 * 1.蓝牙搜索功能、停止扫描等 都统一调用 AndroidBluetooth *
 * 2.连接、配对、发送数据、断开连接 根据 connect方法  btType 确定 *
 * 3.接收数据 统一将数据传递到 onReveiveData 方法中。不需要区分对端是Android linux *
 */
public class BluetoothManager implements DeviceListener {
    private static final String TAG = "BluetoothManager";


    public static String linuxName = "MPENLS";
    public static String androidName = "MPEN";

    //笔和App是否连接
    public static boolean isConnenct = false;
    public static boolean isSendPaly = false;

    private final ArrayList<String> deviceName = new ArrayList<>(); //搜索出来的设备名称

    private PacketReceiver.PacketStatus packetStatus;

    public interface BluetoothListener {//业务层 数据监听

        void onReveiveData(String data, Context context);
    }

    public enum deviceType {//BT_TYPE, BLE_TYPE
        ANDROID_BT_TYPE, LINUX_BT_TYPE
    }

    private Context context;

    private BluetoothListener listener;//业务数据

    public void setListener(BluetoothManager.BluetoothListener listener) {
        this.listener = listener;
    }

    private deviceType curBtType; //当前连接蓝牙类型

    private static BluetoothManager INSTANCE;

    private AndroidBluetooth androidBluetooth; //BT 蓝牙
    private LinuxBluetooth linuxBluetooth;// Linux 蓝牙

    public deviceType getCurBtType() {
        return curBtType;
    }

    public void setCurBtType(deviceType curBtType) {
        this.curBtType = curBtType;
    }

    public static BluetoothManager getInstance() {
        if (INSTANCE == null) {
            synchronized (BluetoothManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothManager();
                }
            }
        }
        return INSTANCE;
    }

    /****
     * bluetoothManager BroadcastReceiver
     * 1.ACTION_FOUND //收缩到的蓝牙设备
     * 2.监听配对状态
     ****/
    private final BroadcastReceiver bluetoothManagerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:

                    BluetoothDevice scanDevice = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


                    if (scanDevice == null || scanDevice.getName() == null)
                        return;
                    String foundDeviceName = scanDevice.getName();
                    if (checkDeviceName(foundDeviceName)) {
                        if (isDeviceNameLinux(foundDeviceName)) {//确定 是linux笔
                            foundDeviceName = foundDeviceName.replaceAll(LinuxBluetooth.linuxName, "");
                            foundDeviceName = foundDeviceName.replaceAll(":", "");

                            if (deviceName.contains(foundDeviceName)) {//deviceName 已经存在了
                                return;
                            }
                        }
                        deviceName.add(foundDeviceName);
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);

                    Log.d(TAG, "onReceive: XXXXXXXXX  " + curBtType);
                    switch (state) {
                        case BluetoothDevice.BOND_NONE:
                            Log.d(TAG, "BOND_NONE 删除配对");
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.d(TAG, "BOND_BONDING 正在配对");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            Log.d(TAG, "BOND_BONDED 配对成功  " + curBtType);
                            if (curBtType == deviceType.LINUX_BT_TYPE) {//配对成功 的是 linux 笔
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void register() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//蓝牙连接状态改变广播

        this.context.registerReceiver(bluetoothManagerReceiver, filter);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void initContext(Context context) {
        this.context = context;
        LinuxBluetooth.initLinuxBluetooth(this.context);//linux
        linuxBluetooth = LinuxBluetooth.getInstance();

        AndroidBluetooth.initAndroidBluetooth(this.context);//Android
        androidBluetooth = AndroidBluetooth.getInstance();

        PacketBuilder.initPlatform(true);//表示 手机端

        register();
    }

    public ArrayList<String> getPairedDeviceName() {
        return deviceName;
    }

    /**
     * 开始扫描
     */
    public void startDiscovery() {
        this.deviceName.clear();//清空之前搜索到的蓝牙
        androidBluetooth.startDiscovery();
    }

    /**
     * 停止扫描
     */
    public void stopDiscovery() {
        androidBluetooth.cancelDiscovery();
    }

    /**
     * 断开蓝牙
     */
    public void onDeviceDisconnected() {
        if (curBtType == null) {
            curBtType = deviceType.ANDROID_BT_TYPE;
        }
        switch (curBtType) {
            case LINUX_BT_TYPE://linux 蓝牙
                linuxBluetooth.removeBind();
                break;
            case ANDROID_BT_TYPE: //Android 蓝牙
                androidBluetooth.onDisconnected();
                break;
            default:
                Log.e(TAG, "curBtType 类型不存 !");
        }
        curBtType = null;
        isConnenct = false;
        isSendPaly = true;
        context.sendBroadcast(new Intent(BTConstants.APP_CONNECT_ERROR_ACTION));
    }

    /**
     * 发送蓝牙数据
     *
     * @param str 数据内容
     */
    public void sendData(final String str) {
        if (!isConnenct) {//未连接
            Log.i(TAG, "sendData: 没有可用连接！！");
        }
        if (curBtType == null) {
            Log.d(TAG, "sendData: curBtType == null");
            return;
        }
        Log.d(TAG, "sendData: curBtType == " + curBtType);
        switch (curBtType) {
            case LINUX_BT_TYPE://linux 蓝牙
                Log.d(TAG, "sendData: " + str);
                try {
                    if (linuxBluetooth.syncDataModule.isConnected()) {
                        Packet[] build = PacketBuilder.build(str, Packet.BLUETOOTH_PACKET_SIZE, Packet.VER_A_BYTE);
                        if (linuxBluetooth.syncDataModule.sendSyncData(build)) {
                            Log.d(TAG, "sendData 237 : " + str);
                        }
                    }
                } catch (SyncException e) {
                    Log.e(TAG, "onReceive: SyncException ");
                    e.printStackTrace();
                }


                break;
            case ANDROID_BT_TYPE: //Android 蓝牙
                androidBluetooth.send(str);
                break;
            default:
                Log.e(TAG, "curBtType 类型不存 !");

        }
    }

    /**
     * 连接状态变化
     *
     * @param btType
     * @param state
     */
    @Override
    public void onConnectionStateChange(deviceType btType, int state) {
        Log.d(TAG, "onConnectionStateChange: " + btType + "  " + state);
        curBtType = btType;
        if (state == 3) {
            isConnenct = true;
            context.sendBroadcast(new Intent(BTConstants.APP_CONNECT_SUCCESS_ACTION));
        }
    }

    /**
     * 接收蓝牙数据回调
     *
     * @param message
     */
    @Override
    public void onReveiveData(String message, byte[] data) {
        if (listener != null) {
            Log.i(TAG, "manager 中接收到的消息" + message);

            switch (Packet.getVer(data)) {
                case OLD://{ 完整数据包 或者是老版本数据
                    listener.onReveiveData(message, context);
                    break;
                case A:
                    onDataReceivedNewA(message, data); //转入到 A协议解析
                    return;
                default:

            }


        }
    }

    //数据层协议解析 （新版本 ）
    private void onDataReceivedNewA(String message, byte[] data) {
        final Packet packetid = PacketReceiver.getPacketMobile(data);
        packetStatus = PacketReceiver.receivePacket(packetid);
        Log.d(TAG, "onDataReceivedNewA: " + message + " packetStatus " + packetStatus);
        switch (packetStatus) {

            case COMPLETE:// 完整

                String msg = PacketReceiver.getMessage(packetid.serialNumber);
                Log.d(TAG, "onDataReceivedNewA: " + msg);

                DataRecord.getInstance().getRequestPackets().remove(String.valueOf(packetid.serialNumber));
                DataRecord.getInstance().getResponsePackets().remove(String.valueOf(packetid.serialNumber));


                listener.onReveiveData(msg, context);// json 数据传递到业务层

                break;
            case NEED_MORE:// 等待更多

                break;
            case NEW:// 一个新的 数据包

                break;
            case ERROR:// 数据包出错
                Log.d(TAG, "蓝牙数据包接收错误: " + packetStatus.ordinal());
                break;

            default:
                Log.d(TAG, "蓝牙数据包接收错误 onDataReceived: ");
                break;
        }

    }

    /***
     *
     * @param btType 对端设备类型,区分Android和Linux笔(笔中需要确认 蓝牙类型 BT or BLE)
     * @param address
     */
    @Override
    public void connect(deviceType btType, String address) {
        curBtType = btType;
        switch (btType) {
            case LINUX_BT_TYPE://linux 蓝牙
                linuxBluetooth.tryPair(address);
                break;
            case ANDROID_BT_TYPE: //Android 蓝牙
                androidBluetooth.connect(address);
                break;
            default:
                Log.e(TAG, "curBtType 类型不存 !");
        }
    }

    /**
     * 检查搜索出来的设备名是否符合要求
     *
     * @param foundDeviceName
     * @return
     */
    private boolean checkDeviceName(String foundDeviceName) {
        Log.d(TAG, "checkDeviceName:  " + foundDeviceName + "__" + foundDeviceName.startsWith(androidName) + "___" + deviceName.contains(foundDeviceName));
        return !TextUtils.isEmpty(foundDeviceName) && foundDeviceName.contains("MPEN") && !deviceName.contains(foundDeviceName);
    }

    /***
     * 判断是否是 linux 笔
     * @param foundDeviceName
     * @return
     */
    private boolean isDeviceNameLinux(String foundDeviceName) {
        Log.d(TAG, "isDeviceNameLinux:  " + foundDeviceName + " " + foundDeviceName.startsWith(linuxName));
        return foundDeviceName.startsWith(linuxName);
    }

    /**
     * 发送停止搜索设备的广播
     */
    public static void stopFoundDiscovery() {
        Intent stopIntent = new Intent(BTConstants.STOP_DISCOVERY_ACTION);
        MpenBluetooth.getInstance().getContext().sendBroadcast(stopIntent);
    }
}
