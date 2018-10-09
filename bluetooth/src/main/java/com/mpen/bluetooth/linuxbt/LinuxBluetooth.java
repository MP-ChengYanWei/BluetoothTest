package com.mpen.bluetooth.linuxbt;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;

import com.mpen.bluetooth.androidbt.AndroidBluetooth;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.utils.SPUtils;

import java.lang.reflect.Method;

import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.Enviroment;
import cn.ingenic.glasssync.Enviroment.EnviromentCallback;
import cn.ingenic.glasssync.SystemModule;

/**
 * Created by LYH on 2017/10/25.
 * <p>
 * 适配 （君正 linux 蓝牙）蓝牙
 * <p>
 * 外部接口 initController, getContext － static
 * </P>
 */
public class LinuxBluetooth implements EnviromentCallback {

    private static final String TAG = "LinuxBluetooth";

    private static LinuxBluetooth linuxBluetooth = null;

    private Context mContext = null;
    public DefaultSyncManager mManager;
    public SyncDataModule syncDataModule = null;

    private String adr = "";//记录当前想要 绑定的蓝牙地址

    public static String linuxName = "MPENLS";//linux 笔前缀 例如现在 android笔信息为 MPENF1:AC:C9:16:C3:8A MPEN为前缀 F1:AC:C9:16:C3:8A 为MAC地址
    public static String addr;// 当前连接的蓝牙地址
    //linux 没定下来

    private LinuxBluetooth(Context context) {
        mContext = context;
        init();
        register();//注册广播
    }

    private void init() {
        Enviroment.init(this);
        DefaultSyncManager manager = DefaultSyncManager.init(mContext);
        SystemModule systemModule = new SystemModule();
        if (manager.registModule(systemModule)) {
            Log.i(TAG, "SystemModule is registed.");
        }
        syncDataModule = SyncDataModule.getInstance(mContext);

        mManager = DefaultSyncManager.getDefault();
    }

    private void register() { //广播
        IntentFilter controlFilter = new IntentFilter();

        controlFilter.addAction(DefaultSyncManager.RECEIVER_ACTION_STATE_CHANGE);//蓝颜绑定成功广播
        controlFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//蓝牙连接状态改变广播
        controlFilter.addAction(DefaultSyncManager.RECEIVER_ACTION_DISCONNECTED);//蓝牙解绑广播
        mContext.registerReceiver(linuxBluetoothReceiver, controlFilter);
    }

    private final BroadcastReceiver linuxBluetoothReceiver = new BroadcastReceiver() {//监听广播

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "linuxBluetoothReceiver == onReceive: " + intent.getAction());
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                switch (state) {
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "BOND_NONE 删除配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "BOND_BONDING 正在配对");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "BOND_BONDED 配对成功");
                        if (name.startsWith(linuxName)) {//如果是linux 笔 就進行綁定
                            Log.d(TAG, "CONNECT address = " + device.getAddress()
                                    + "name = " + device.getName() + " DefaultSyncManager State = " + mManager.getState());
                            if (adr.equals(device.getAddress())) {//配对成功的 linux笔 是想要绑定的linux
                                if (device.getAddress().equals(SPUtils.get(SPUtils.MACADDRESS, ""))) {//已经绑定 的情况出现
                                    return;
                                }
                                createBind(device);
                            }
                        }

                        break;
                }
            } else if (DefaultSyncManager.RECEIVER_ACTION_DISCONNECTED.equals(intent.getAction())) {
                Log.d(TAG, "linuxBluetoothReceiver == onReceive: 绑定失败！！！ " + intent.getAction());
                Log.e(TAG, "绑定失败！！！");
            } else if (DefaultSyncManager.RECEIVER_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                Log.w(TAG, "RECEIVER_ACTION_STATE_CHANGE !");
                int state = intent.getIntExtra(DefaultSyncManager.EXTRA_STATE,
                        DefaultSyncManager.IDLE);
                boolean isConnect = (state == DefaultSyncManager.CONNECTED) ? true : false;
                Log.i(TAG, isConnect + "    isConnect");

                if (isConnect) {
                    addr = mManager.getLockedAddress();
                    if (addr.equals("")) {
                        Log.w(TAG,
                                "local has disconnect,but remote not get notificaton.notify again!");
                        mManager.disconnect();
                    } else {
                        mManager.setLockedAddress(addr);
                        Log.d(TAG, "onReceive: 发送请求！！！！！！！！！！！！");
                        BluetoothManager.getInstance().onConnectionStateChange(BluetoothManager.deviceType.LINUX_BT_TYPE, 3);
                        // 此广播在上一行代码会发送无须发送多次
//                        mContext.sendBroadcast(new Intent(BTConstants.APP_CONNECT_SUCCESS_ACTION));//发一条配对成功的广播
                    }
                } else {
                    mContext.sendBroadcast(new Intent(BTConstants.APP_CONNECT_ERROR_ACTION));//发一条连接失败的广播
                }
            }
        }
    };

    /****
     *
     * 进行蓝牙配对
     * @param address
     * @return
     * 经过测试，解除linux 绑定状态，但手机会继续保持配对的状态。
     * 这个时候搜索到的蓝牙名右面会突然增加nc
     *
     * 所以直接在解除配对的时候，清除配对信息的操作
     *
     */
    public void tryPair(String address) {

        adr = address;
        address = address.toUpperCase();
        BluetoothDevice btDevice = AndroidBluetooth.getmBluetoothSPP().getBluetoothAdapter().getRemoteDevice(address);

        Log.d(TAG, "createBond: 建立配对关系 ");

        if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {//防止客户在手机系统中手动配对
            Log.d(TAG, "createBond:  btDevice.getBondState() = " + BluetoothDevice.BOND_BONDED);
            createBind(btDevice);
            return;
        }

        try {
            Method createBondMethod = BluetoothDevice.class
                    .getMethod("createBond");
            createBondMethod.invoke(btDevice);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "GetBind: 蓝颜配对失败");
        }
    }

    /***
     * 刪除配对 信息
     * 测试，发现有时候呢linux解绑后，再次搜索蓝牙就搜索不到
     * @return
     */
    public Boolean unpair(String address) {
        address = address.toUpperCase();
        BluetoothDevice btDevice = AndroidBluetooth.getmBluetoothSPP().getBluetoothAdapter().getRemoteDevice(address);
        try {
            Method m = btDevice.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(btDevice, (Object[]) null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }


    /***
     *
     * 绑定 linux和手机（这里调用的是君正的绑定）
     * 这个属于硬件绑定
     * @param btDevice
     * @return
     *
     */
    public void createBind(BluetoothDevice btDevice) {

        if (DefaultSyncManager.isConnect()) {
            Log.d(TAG, "createBind: mManager " + mManager.toString() + " addrs  " + mManager.getLockedAddress());
            //   return;
        }
        if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            try {
                Log.d(TAG, "handleMessage: " + "绑定中");
                Log.d(TAG, "CONNECT address = " + btDevice.getAddress()
                        + "name =" + btDevice.getName());
                mManager.connect(btDevice.getAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void removeBind() {
        Log.d(TAG, "removeBind: 解除绑定！！！");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mManager.setLockedAddress("", true);
                    try {
                        SystemClock.sleep(800); //防止过快断开，未传完的数据丢失
                    } catch (Exception e) {
                    }
                    mManager.disconnect();
                    //LinuxBluetooth.getContext().removeBond(address);//保险起见 解除绑定后删除配对信息

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /***************外部接口**************/
    public static void initLinuxBluetooth(Context context) {
        Log.d(TAG, "initLinuxBluetooth: create!");
        linuxBluetooth = new LinuxBluetooth(context);
    }

    public static LinuxBluetooth getInstance() {
        if (linuxBluetooth == null) {
            Log.e(TAG, "代码错误：DianduController没有被初始化");
        }
        return linuxBluetooth;
    }

    /************** 外部接口 end ************/
    @Override
    public Enviroment createEnviroment() {
        return new AppEnviroment(mContext);
    }

}