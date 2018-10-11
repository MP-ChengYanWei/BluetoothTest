package com.mpen.bluetooth.linuxbt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mpen.bluetooth.bluetooth.Packet;
import com.mpen.bluetooth.common.DataRecord;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.utils.DataController;
import com.mpen.bluetooth.utils.FileUtil;

import java.io.UnsupportedEncodingException;

import cn.ingenic.glasssync.services.SyncData;
import cn.ingenic.glasssync.services.SyncException;
import cn.ingenic.glasssync.services.SyncModule;

/**
 * Created by LYH on 2017/10/26.
 * <p>
 * 1. Module 需要眼镜端和手机端同时存在，并且两端注册的Module的名字(MODULE_NAME)要相同要相同，否则不能够传输数据.
 * <p>
 * 2. 注册SyncModule需要一些时间，所以一般要放在Application中或者Activity的Oncreat中
 * <p>
 * 3. 注册Module的名字长度要小于15
 * <p>
 * 4. 使用SyncModule发送消息时，必须要等到ISyncServiceListener回调，否则发送不成功
 * <p>
 * 也可以通过DefaultSyncManager中的DefaultSyncManager.REVEIVER_ACTION_STATE_CHANGE 这个广播，监听连接的状态
 */

public class SyncDataModule extends SyncModule implements SyncModule.ISyncServiceListener {

    private final String TAG = "SendDataModule";
    public final static String MODULE_NAME = "m"; // 长度必须小于15
    private boolean mIsSyncServiceReady;//绑定成功

    private static SyncDataModule sInstance;
    private Context mContext;

    private SyncDataModule(Context context) {
        super(MODULE_NAME, context);
        setISyncServiceListener(this);
        mContext = context;
    }

    public static SyncDataModule getInstance(Context c) {
        if (null == sInstance)
            sInstance = new SyncDataModule(c);
        return sInstance;
    }

    @Override
    protected void onCreate() {
        Log.i(TAG, "SendDataModule onCreate ...");
    }

    // bind sync service success
    @Override
    public void ISyncServiceReady() {
        Log.i(TAG, "ISyncServiceReady!!!!!!");
        mIsSyncServiceReady = true;
    }

    /**
     * 蓝牙连接状态改变的回调
     * 参数： connect TRUE 连接 FALSE 未连接
     */
    @Override
    protected void onConnectionStateChanged(boolean connect) {
        super.onConnectionStateChanged(connect);
        Log.i(TAG, "onConnectionStateChanged :: connect = " + connect);
        if (!connect) {
            // 与LINUX笔断开连接，刷新界面
            DataController.getInstance().appendData("蓝牙连接断开：" + BluetoothManager.DEVICE_ADDRESS);
            BluetoothManager.isConnenct = false;  //为不波及其它位置，先放于此处
            mContext.sendBroadcast(new Intent(BTConstants.APP_CONNECT_ERROR_ACTION));
        }
        Intent intent = new Intent(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);
        intent.putExtra("connect", connect);
        mContext.sendBroadcast(intent);
    }

    /**
     * 接受对端发送数据的回调
     * 参数 SyncData  对端发送的数据，使用类似于Intent
     */
    @Override
    protected void onRetrive(SyncData data) {
        Log.e("Linux", "<<<<" + data.getString("d"));
        FileUtil.bluetoothDataWrite("接收<<<<" + data.getString("d"));
        super.onRetrive(data);
        Log.d(TAG, ">>>>>>>>>>> onRetrive: " + data.getString("d"));
        try {
            String msg = data.getString("d");
            byte[] tmpbytes = msg.getBytes("UTF-8");
            BluetoothManager.getInstance().onReveiveData(msg, tmpbytes);
        } catch (UnsupportedEncodingException e) {

        }
    }

    /**
     * 调用SyncModule的sendData函数向对端发送数据
     * SyncData 中可以存储的数据类型有 int,boolan,String等,可以根据自己的实际需要进行选择.
     */
    public boolean sendSyncData(Packet[] dataPack) {
        if (!mIsSyncServiceReady) {
            Log.e(TAG, "bind sync service not ready ,try again later");
            return false;
        }
        for (Packet packet : dataPack) {
            SyncData data = new SyncData();
            data.putString("d", packet.objToString());
            Log.d(TAG, "sendSyncData: " + packet.objToString());
            Log.w("Linux", ">>>>" + packet.objToString());
            FileUtil.bluetoothDataWrite("发送>>>>" + packet.objToString());
            try {
                send(data);
            } catch (SyncException e) {
                Log.e(TAG, "---send sync failed:" + e);
            }
        }
        DataRecord.getInstance().getRequestPackets().put(String.valueOf(dataPack[0].serialNumber), dataPack);
        return true;
    }
}