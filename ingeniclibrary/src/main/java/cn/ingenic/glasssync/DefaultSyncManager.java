package cn.ingenic.glasssync;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import cn.ingenic.glasssync.LogTag.Mgr;
import cn.ingenic.glasssync.data.DefaultProjo;
import cn.ingenic.glasssync.data.FeatureConfigCmd;
import cn.ingenic.glasssync.data.FileSendCmd;
import cn.ingenic.glasssync.data.FileSendCmd.FileSendCmdColumn;
import cn.ingenic.glasssync.data.ModeSendCmd;
import cn.ingenic.glasssync.data.Projo;
import cn.ingenic.glasssync.data.ProjoList;
import cn.ingenic.glasssync.data.ProjoList.ProjoListColumn;
import cn.ingenic.glasssync.services.SyncData;
import cn.ingenic.glasssync.services.SyncProjo;
import cn.ingenic.glasssync.transport.TransportManager;

/**
 * @author tli
 */
public class DefaultSyncManager extends Handler {
    protected Context mContext;

    //	private static final int MSG_BASE = 0;
//	public static final int MSG_STOP_TRANSACTION = MSG_BASE + 1;
    private static final String TAG = "DefaultSyncManager";
    private static final String UNBONDED_TAG = "unbonded";
    public static final int SUCCESS = 0;
    public static final int NO_CONNECTIVITY = SUCCESS - 1;
    public static final int FEATURE_DISABLED = SUCCESS - 2;
    public static final int NO_LOCKED_ADDRESS = SUCCESS - 3;
    public static final int DELAYED = SUCCESS - 4;
    public static final int UNKNOW = SUCCESS - 5;

    public static final int SAVING_POWER_MODE = 0;
    public static final int RIGHT_NOW_MODE = 1;

    public static final int NON_REASON = 0;
    public static final int IDLE = 10;
    public static final int CONNECT_FAILED = NON_REASON - 1;
    public static final int CONNECTING = IDLE + 1;
    public static final int CONNECTED = IDLE + 2;
    public static final int CONNECTED_WITH_INIT = NON_REASON - 2;
    public static final int DISCONNECTING = IDLE + 3;
    public static final int REQUESTING = IDLE + 4;
    public static final int RESPONDING = IDLE + 5;

    private static final int NOTI_RECONNECT_ID = 100;
    private static final int NOTI_UNBOND_ST = 101;
    private static final int NOTI_LOST_BOND_ST = 102;

    static final String MODE_SETTINGS_KEY = "mode_settings";
    static final String CLEAR_SETTINGS_KEY = "clear_settings";
    static final String TIMEOUT_NOTIFICATION_KEY = "notification_settings";

    private int mState = IDLE;

    private final SharedPreferences mSharedPreferences;
    private final TransportManager mTransportManager;

    private AlertDialog mOtherDeviceReqDialog;
    private String mReConnectAddress = null;

    public int getState() {
        return mState;
    }

    void notifyModeChanged(int mode) {
        Mgr.i("Current MODE change to:" + mode);
        for (Module m : mModules.values()) {
            m.onModeChanged(mode);
        }
    }

    public int getCurrentMode() {
        String mode = mSharedPreferences.getString(MODE_SETTINGS_KEY, String.valueOf(DefaultSyncManager.SAVING_POWER_MODE));

        try {
            return Integer.valueOf(mode);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return DefaultSyncManager.SAVING_POWER_MODE;
        }
    }

    // just be listened by glass
    public static final String RECEIVER_ACTION_STATE_CHANGE =
            "receiver.action.STATE_CHANGE";
    public static final String RECEIVER_OTHER_DEVICE_REQUEST =
            "receiver.action.other.device.request";

    // can be listened by mobile
    public static final String RECEIVER_ACTION_DISCONNECTED =
            "receiver.action.DISCONNECTED";
    public static final String RECEIVER_REQUST_UNBIND =
            "receiver.request.unbind";

    public static final String EXTRA_STATE = "extra_state";

    private static final int MSG_BASE = 0;
    public static final int MSG_STATE_CHANGE = MSG_BASE + 1;
    public static final int MSG_TIME_OUT = MSG_BASE + 2;
    public static final int MSG_SEND_FILE = MSG_BASE + 3;

    public static final int MSG_CLIENT_SOCKET_DISCONNECT = MSG_BASE + 4;
    public static final int MSG_CLIENT_LINK_ERROR = MSG_BASE + 5;
    public static final int MSG_CLIENT_LINK_TIMEOUT = MSG_BASE + 6;
    //SyncManagerExt
    public static final int MSG_RUNNABLE_WITH_ARGS = MSG_BASE + 7;
    public static final int MSG_CLEAR_ADDRESS = MSG_BASE + 8;
    public static final int MSG_SERVER_MISMATCH_MAC = MSG_BASE + 9;

    private final String UNBIND_MODULE_NAME = "system_module";
    private final String UNBIND_CMD = "unbind";

    interface DelayedTask {
        void execute(boolean connected);
    }

    private static class FileArg {
        final String mmModule;
        final String mmName;
        final int mmLength;
        final InputStream mmIn;

        FileArg(String module, String name, int length, InputStream in) {
            mmModule = module;
            mmName = name;
            mmLength = length;
            mmIn = in;
        }
    }

    private LinkedList<DelayedTask> mWaitingList = new LinkedList<DelayedTask>();
    private boolean mConnected = false;

    private String convertMsg(int state) {
        switch (state) {
            case IDLE:
                return "IDLE";
            case CONNECTING:
                return "CONNECTING";
            case CONNECTED:
                return "CONNECTED";
            case DISCONNECTING:
                return "DISCONNECTING";
            default:
                return "unknow msg";
        }
    }

    final protected void push(DelayedTask task) {
        synchronized (mWaitingList) {
            mWaitingList.add(task);
        }
    }

    private void sendInitConfig() {
        Mgr.d("sendInitConfig...");
        Config config = new Config(SystemModule.SYSTEM);
        ArrayList<Projo> datas = new ArrayList<Projo>();
        Projo modeCmd = new ModeSendCmd();
        modeCmd.put(ModeSendCmd.ModeSendColumn.mode,
                getCurrentMode());
        datas.add(modeCmd);

        // Projo addressCmd = new AddressSendCmd();
        // addressCmd.put(AddressSendCmd.AddressSendColumn.address,
        // 		BluetoothAdapter.getDefaultAdapter().getAddress());
        // datas.add(addressCmd);

        Projo featureCmd = new FeatureConfigCmd();
        featureCmd.put(FeatureConfigCmd.FeatureConfigColumn.feature_map,
                getSyncMap());
        datas.add(featureCmd);

//		request(config, datas);
    }

    private void notifyStateChange(int state) {
        Intent intent = new Intent(RECEIVER_ACTION_STATE_CHANGE);
        intent.putExtra(EXTRA_STATE, state);
        mContext.sendBroadcast(intent);
    }

    private void notifyDisconnected(int reason) {
        Intent intent = new Intent(RECEIVER_ACTION_DISCONNECTED);
        intent.putExtra(EXTRA_STATE, reason);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_STATE_CHANGE:
                Mgr.d("state change to " + convertMsg(msg.arg1));
                if (msg.arg1 != mState) {
                    int oldSt = mState;
                    mState = msg.arg1;
                    notifyStateChange(mState);

                    switch (msg.arg1) {
                        case IDLE:
                            mConnectingAddress = null;
                            if (mConnected) {
                                Mgr.i("notify disconnected to all Modules");
                                for (Module m : mModules.values()) {
                                    m.onConnectivityStateChange(false);
                                }

                                mConnected = false;
                            } else {
                                synchronized (mWaitingList) {
                                    Mgr.d("connect failed, notify all delayed request callback msg.");
                                    while (!mWaitingList.isEmpty()) {
                                        DelayedTask task = mWaitingList.poll();
                                        task.execute(false);
                                    }
                                }
                            }

                            if (mReConnectAddress != null) {
                                connect(mReConnectAddress);
                            }

                            break;
                        case CONNECTED:
                            if (msg.arg2 == 1) {
                                //rebond
                                Mgr.d("rebond");
                                notifyCleared(getLockedAddress());
                                callModulesOnInit();
                            } else {
                                Enviroment env = Enviroment.getDefault();
                                if (Enviroment.getDefault().isWatch()) {
                                    setLockedAddress((String) msg.obj);
                                }
                            }
                            if (!mConnected) {
                                String lockedAddr = getLockedAddress();
                                Mgr.i("getLockedAddress" + lockedAddr);
                                if (!BluetoothAdapter.checkBluetoothAddress(lockedAddr)) {
                                    if (!TextUtils.isEmpty(mConnectingAddress)) {
                                        Mgr.d("setLockedAddress with connecting address:" + mConnectingAddress);
                                        setLockedAddress(mConnectingAddress);
                                        mConnectingAddress = null;
                                    }
                                } else {
                                    if (CONNECTED_WITH_INIT == msg.arg2) {
                                        notifyCleared(lockedAddr);
                                        callModulesOnInit();
                                    }
                                }

                                Mgr.i("notify connected to all Modules");
                                Enviroment env = Enviroment.getDefault();
                                if (!env.isWatch()) {
                                    sendInitConfig();
                                }

                                synchronized (mWaitingList) {
                                    while (!mWaitingList.isEmpty()) {
                                        DelayedTask task = mWaitingList.poll();
                                        task.execute(true);
                                    }
                                }

                                for (Module m : mModules.values()) {
                                    if (m.getSyncEnable()) {
                                        m.onConnectivityStateChange(true);
                                    }
                                }

                                mConnected = true;
                            } else {
                                Mgr.w("duplicate connected happened!");
                            }

                            break;
                    }
                }
                break;

            case MSG_CLEAR_ADDRESS:
                setLockedAddress("");
                break;
            case MSG_SERVER_MISMATCH_MAC:
                Mgr.i("--RECEIVER_OTHER_DEVICE_REQUEST");
                Intent intent = new Intent(RECEIVER_OTHER_DEVICE_REQUEST);
                mContext.sendBroadcast(intent);
                break;
            case MSG_CLIENT_SOCKET_DISCONNECT:
                if (getState() == CONNECTING) {
                    disconnect();
                    setLockedAddress("");
                    notifyDisconnected(msg.arg1);
                }
                break;
            case MSG_CLIENT_LINK_ERROR:
            case MSG_CLIENT_LINK_TIMEOUT:
                disconnect();
                setLockedAddress("");
                notifyDisconnected(msg.arg1);
                break;

            case MSG_SEND_FILE:
                FileArg arg = (FileArg) msg.obj;
                if (msg.arg1 == SUCCESS) {
                    mTransportManager.sendFile(arg.mmModule, arg.mmName, arg.mmLength,
                            arg.mmIn);
                } else {
                    Mgr.w("connect create failed in SEND_FILE request. module:"
                            + arg.mmModule + " fileName:" + arg.mmName);
                    Module m = getModule(arg.mmModule);
                    if (m == null) {
                        Mgr.e("Module:" + arg.mmModule + " not found");
                        return;
                    }

                    OnFileChannelCallBack callback = m.getFileChannelCallBack();
                    if (callback == null) {
                        Mgr.e("can not found OnFileChannelCallback from Module:"
                                + m.getName());
                        return;
                    }
                    callback.onSendComplete(arg.mmName, false);
                }
                break;

            case MSG_RUNNABLE_WITH_ARGS:
                RunnableWithArgs run = (RunnableWithArgs) msg.obj;
                run.arg1 = msg.arg1;
                run.run();
                break;
        }
    }

    protected static abstract class RunnableWithArgs implements Runnable {
        int arg1;
    }

    private static DefaultSyncManager sManager;

    public static DefaultSyncManager init(Context context) {
        if (sManager == null) {
            if (LogTag.V) {
                Mgr.d("create Manager.");
            }

            sManager = new SyncManagerExt(context);
        } else {
            Mgr.w("Manager alread created.");
        }

        return sManager;
    }

    public static DefaultSyncManager getDefault() {
        if (sManager == null) {
            throw new NullPointerException(
                    "DefaultSyncManager must be inited before getDefault().");
        }

        return sManager;
    }

    private static final String PREF_FILE_NAME = "cn.ingenic.glasssync_preferences";

    public static final String FILE_NAME = "settings";
    public static final String UNIQUE_ADDRESS = "unique_address";

    public static boolean isWatch() {
        return Enviroment.getDefault().isWatch();
    }

    public void setLockedAddress(String address) {

        setLockedAddress(address, false);
        Mgr.d("setLockedAddress");
    }

    public void setLockedAddress(String address, boolean notify) {
        Mgr.d("setLockedAddress:" + address + "  notify=" + notify);
        SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        String oldAddress = sp.getString(UNIQUE_ADDRESS, "");
        Mgr.d("oldAddress:" + oldAddress);
        if (oldAddress.equals(address)) {
            Mgr.w("setLockedAddress duplicate with address:" + address);
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        boolean validOldAddr = BluetoothAdapter.checkBluetoothAddress(oldAddress);
        boolean validAddr = BluetoothAdapter.checkBluetoothAddress(address);
        if (validOldAddr) {
            notifyCleared(oldAddress);
            if (!validAddr) {
                address = "";
                if (notify && isConnect()) {
                    Mgr.i("sendClearMsg with address:" + oldAddress + " to watch point");
                    if (!Enviroment.getDefault().isWatch()) {
                        SyncData data = new SyncData();
                        data.putBoolean(UNBIND_CMD, true);
                        Log.d(TAG, "send(UNBIND_MODULE_NAME, data);");
                        send(UNBIND_MODULE_NAME, data);
                    }
                    //sendClearMsg();
                }
                // mReConnectScheduler.clearReConnectStatus();
            }
        } else if (!validAddr) {
            Mgr.w("Duplicate bond clear.");
            return;
        }
        editor.putString(UNIQUE_ADDRESS, address);
        editor.commit();
        if (validAddr) {
            callModulesOnInit();
        }

        if (Enviroment.getDefault().isWatch()) {
            Settings.System.putInt(mContext.getContentResolver(),
                    "glasssync_bond", validAddr ? 1 : 0);
            Settings.System.putString(mContext.getContentResolver(),
                    "glasssync_bond_addr", address);

        }
    }

    public static final long TIMEOUT = 60 * 1000;

    public void holdOnConnTemporary(String module) {
        Mgr.d(module + " request holdOnConnTemporary");
        holdOnConnTemporaryInternal(null);
    }

    private void holdOnConnTemporaryInternal(Message callback) {
        Projo projo = new DefaultProjo();
        Config config = new Config(SystemModule.SYSTEM);
        config.mCallback = callback;
        Log.d(TAG, "holdOnConnTemporaryInternal");
        request(config, projo);
    }

    private void callModulesOnInit() {
        Mgr.i("call modules onInit()");
        for (Module m : mModules.values()) {
            m.onInit();
        }
    }

    private void sendClearMsg() {
        Log.d(TAG, "sendClearMsg");
        Module m = getModule(SystemModule.SYSTEM);
        Mgr.i("Default----Module:" + m);
        RemoteChannelManagerService service = RemoteChannelManagerImpl
                .asRemoteInterface(m
                        .getRemoteService(RemoteChannelManagerService.DESPRITOR));
        service.sendClearMessage();
    }

    private void notifyCleared(String address) {
        Mgr.i("clear all datas with address:" + address);
        for (Module m : mModules.values()) {
            m.onClear(address);
        }
    }

    void connect() {
        connect(null);
    }

    private String mConnectingAddress;

    public void connect(String address) {
        mReConnectAddress = null; //clear reconnect address
        if (mState != IDLE) {
            Mgr.w("can not connect !current state is " + convertMsg(mState));
            return;
        }
        if (address == null) {
            address = getLockedAddress();
            Mgr.d("getLockedAddress:" + address);
        } else {
            Mgr.d("connecting address-----------connect:" + address);
            mConnectingAddress = address;
        }

        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            throw new IllegalStateException("Invalid address.");
        }
        mState = CONNECTING;
        mTransportManager.prepare(address);
    }

    /*
     *just for mobile now
     * address:reconnect address
     */
    public void reconnect(String address) {
        disconnect();
        mReConnectAddress = address;
    }

    public void disconnect() {
        Log.d("DefaultSyncManager", "disconnect");
        Mgr.d("disconnect in ");
        if (Enviroment.getDefault().isWatch()) {
            /*send unbind to mobile*/
            Mgr.d("send disconnect to mobile ");
            Log.d("DefaultSyncManager", "send disconnect to mobile");
            DefaultSyncManager manager = DefaultSyncManager.getDefault();
            Config config = new Config(SystemModule.SYSTEM);
            Map<String, Boolean> map = new HashMap<String, Boolean>();
            map.put(SystemModule.FEATURE_UNBIND, true);
            Projo projo = new FeatureConfigCmd();
            projo.put(FeatureConfigCmd.FeatureConfigColumn.feature_map, map);
            manager.request(config, projo);
        } else {
            mState = DISCONNECTING;
            mTransportManager.prepare("");

        }
    }

    public static boolean isConnect() {
        Mgr.d("getState()" + DefaultSyncManager.getDefault().getState());
        return DefaultSyncManager.getDefault().getState() == CONNECTED;

    }

    public String getLockedAddress() {

        String bond_addr = "";
        if (Enviroment.getDefault().isWatch()) {
            bond_addr = Settings.System.getString(mContext.getContentResolver(), "glasssync_bond_addr");
            if (bond_addr == null) bond_addr = "";
        } else {
            SharedPreferences sp = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
            bond_addr = sp.getString(UNIQUE_ADDRESS, "");
        }
        return bond_addr;
    }

    protected DefaultSyncManager(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, 0);
        // mReConnectScheduler = new ReConnectScheduler();
        mTransportManager = TransportManager.init(mContext, SystemModule.SYSTEM, this);
        Enviroment env = Enviroment.getDefault();
        String address = getLockedAddress();
        if (!env.isWatch() && BluetoothAdapter.checkBluetoothAddress(address)) {
            /*make sure moblie can reconnect glass when mobile be killed*/
            Mgr.i("reconnect glass address=" + address);
            mTransportManager.prepare(address);
        }
    }

    public static interface OnChannelCallBack {
        void onCreateComplete(boolean success, boolean local);

        void onRetrive(ProjoList projoList);

        @Deprecated
        void onDestory();
    }

    public static interface OnFileChannelCallBack {
        void onSendComplete(String name, boolean success);

        void onRetriveComplete(String name, boolean success);
    }

    public void destoryChannel(String module, UUID uuid) {
        mTransportManager.destoryChannel(uuid);
    }

    public void createChannel(String module, UUID uuid) {
//		if (isConnect()) {
        Module m = getModule(SystemModule.SYSTEM);
        Module requestModule = getModule(module);
        OnChannelCallBack callback = null;
        if (requestModule != null) {
            callback = requestModule.getChannelCallBack(uuid);
        } else {
            Mgr.e("module:" + module + " not found in createChannel");
            return;
        }

        RemoteChannelManagerService service = RemoteChannelManagerImpl
                .asRemoteInterface(m
                        .getRemoteService(RemoteChannelManagerService.DESPRITOR));
        if (service.listenChannel(module, uuid)) {
            mTransportManager.createChannel(uuid, callback);
        } else {
            Mgr.w("RemoteChannelManagerService listenChannle for Module:"
                    + module + " UUID:" + uuid + " failed.");
            callback.onCreateComplete(false, true);
        }
//		} else {
//			Mgr.w("can not createChannel without connecvitity.");
//		}
    }

    public boolean sendCMD(String module, SyncData data) {
        //implement in SyncManagerExt.java
        return false;
    }

    public int getWaitingListSize(int type) {
        //implement in SyncManagerExt.java
        return 0;
    }

    public boolean sendFile(String module, String name, int length, InputStream in) {
        Config config = new Config(SystemModule.SYSTEM);
        ArrayList<Projo> datas = new ArrayList<Projo>();
        Projo projo = new FileSendCmd();
        projo.put(FileSendCmdColumn.module, module);
        projo.put(FileSendCmdColumn.name, name);
        projo.put(FileSendCmdColumn.length, length);
        projo.put(FileSendCmdColumn.address, BluetoothAdapter.getDefaultAdapter().getAddress());
        datas.add(projo);

        boolean connect = isConnect();
        if (!connect) {
            config.mCallback = obtainMessage(MSG_SEND_FILE, new FileArg(module, name, length, in));
        }
        Log.d(TAG, "sendFile");
        request(config, datas);
        if (connect) {
            mTransportManager.sendFile(module, name, length, in);
            return true;
        }

        return false;
    }

    public boolean sendFileByPath(String module, String name, int length, InputStream in, String path) {
        return false;
    }

    void retriveFile(String module, String name, int length, String address) {
        if (isConnect()) {
            mTransportManager.retriveFile(module, name, length, address);
        } else {
            Mgr.w("can not retriveFile without connecvitity.");
            OnFileChannelCallBack cb = getModule(module).getFileChannelCallBack();
            if (cb != null) {
                cb.onRetriveComplete(name, false);
            } else {
                Mgr.w("There is no OnFileChannelCallback for module:" + module
                        + " in retriveFile()");
            }
        }
    }

    public int requestChannel(Config config, Projo projo, UUID uuid) {
        ArrayList<Projo> datas = new ArrayList<Projo>();
        datas.add(projo);
        return requestChannel(config, datas, uuid);
    }

    public int requestChannel(Config config, ArrayList<Projo> datas, UUID uuid) {
        if (isConnect()) {
            if (!isFeatureEnabled(config.mFeature)) {
                Mgr.w("Feature:" + config.mFeature + " in module:"
                        + config.mModule + " is disabled in requestChannel().");
                return FEATURE_DISABLED;
            }
            ProjoList projoList = new ProjoList();
            projoList.put(ProjoListColumn.control, config.getControl());
            projoList.put(ProjoListColumn.datas, datas);
            mTransportManager.requestUUID(uuid, projoList);
            return SUCCESS;
        } else {
            Mgr.w("can not request without connecvitity in requestChannel.");
            return NO_CONNECTIVITY;
        }
    }

    public int request(Config config, Projo projo) {
        ArrayList<Projo> datas = new ArrayList<Projo>();
        datas.add(projo);
        return request(config, datas);
    }

    public int send(String module, SyncData data) {
        Projo p = new SyncProjo(data);
        Config config = parseConfig(module, data);
        return request(config, p);
    }

    public int send(String module, SyncData data, UUID uuid) {
        Projo projo = new SyncProjo(data);
        Config config = parseConfig(module, data);
        return requestChannel(config, projo, uuid);
    }

    private Config parseConfig(String module, SyncData data) {
        Config config = new Config(module);
        SyncData.Config c = data.getConfig();
        if (c != null) {
            config = new Config(module, c.mmIsMid);
            long sort = c.getSort();
            if (sort != SyncData.INVALID_SORT) {
                config.mCallback = c.mmCallback;
            }
        }
        return config;
    }

    public int request(Config config, ArrayList<Projo> datas) {
        return request(config, datas, false);
    }

    protected final void sendCallbackMsg(Message msg, int reason) {
        if (msg != null) {
            msg.arg1 = reason;
            msg.sendToTarget();
        }
    }

//	private String convertReason(int reason) {
//		switch (reason) {
//		case SUCCESS:
//			return "SUCCESS";
//		case NO_CONNECTIVITY:
//			return "NO_CONNECTIVITY";
//		case NO_LOCKED_ADDRESS:
//			return "NO_LOCKED_ADDRESS";
//		case FEATURE_DISABLED:
//			return "FEATURE_DISABLED";
//		default:
//			return "UNKONW";
//		}
//	}

    int request(final Config config, final ArrayList<Projo> datas, final boolean sync) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (BluetoothAdapter.STATE_ON != adapter.getState()) {
            Mgr.w("can not request without Bluetooth");
            sendCallbackMsg(config.mCallback, NO_LOCKED_ADDRESS);
            return NO_CONNECTIVITY;
        }
        Mgr.w("request without locked address;" + BluetoothAdapter.checkBluetoothAddress(getLockedAddress()));
        if (!BluetoothAdapter.checkBluetoothAddress(getLockedAddress())) {
            Mgr.w("can not request without locked address;");
            sendCallbackMsg(config.mCallback, NO_LOCKED_ADDRESS);
            return NO_LOCKED_ADDRESS;
        }

        if (!isFeatureEnabled(config.mFeature)) {
            Mgr.w("Feature:" + config.mFeature + " in module:" + config.mModule + " is disabled in request().");
            sendCallbackMsg(config.mCallback, FEATURE_DISABLED);
            return FEATURE_DISABLED;
        }

        ProjoList projoList = new ProjoList();
        projoList.put(ProjoListColumn.control, config.getControl());
        projoList.put(ProjoListColumn.datas, datas);

        if (isConnect()) {
            Mgr.e("DefaultSyncManager--request-------");
            Log.d(TAG, "DefaultSyncManager--request-------");
            if (sync) {
                mTransportManager.requestSync(projoList);
            } else {
                Log.d(TAG, "request");
                mTransportManager.request(projoList);
            }
            return SUCCESS;
        } else {
            Mgr.w("requesting without connecvitity.");
            connect();
            push(new DelayedTask() {

                @Override
                public void execute(boolean connected) {
                    if (connected) {
                        request(config, datas, sync);
                    } else {
                        sendCallbackMsg(config.mCallback, NO_CONNECTIVITY);
                    }
                }

            });
            return NO_CONNECTIVITY;
        }
    }

    public void response(Config config, ArrayList<Projo> datas) {
//		if (!isConnect()) {
//			Mgr.w("can not response without connecvitity.");
//			return;
//		}

        if (!isFeatureEnabled(config.mFeature)) {
            Mgr.w("Feature:" + config.mFeature + " in module:" + config.mModule
                    + " is disabled in response().");
            return;
        }

        Transaction tran = createTransaction(config);

        if (tran != null) {
            startTransaction(tran, datas);
        } else {
            Mgr.e("can not create Transaction in response!");
        }

    }

    private Transaction createTransaction(Config c) {
        Transaction tran = null;

        Module m = mModules.get(c.mModule);
        if (m == null) {
            Mgr.e("There is not any Module be registed with " + c.mModule);
            return null;
        }

        tran = m.createTransaction();

        if (tran != null) {
            tran.setHandler(this);
            tran.onCreate(c, mContext);
        }
        return tran;
    }

    private void startTransaction(Transaction tran, ArrayList<Projo> datas) {
        tran.onStart(datas);
    }

    //Module
    private Map<String, Module> mModules = new HashMap<String, Module>();

    public boolean registModule(cn.ingenic.glasssync.Module m) {
        String name = m.getName();
        if (mModules.containsKey(name)) {
            Mgr.e("Module:" + name + " has already been registed!");

            mModules.remove(name);
        }

        mModules.put(name, m);
        m.onCreate(mContext);

        String[] nonSyncFeatures = getNonSyncArray();
        for (String feature : nonSyncFeatures) {
            if (m.hasFeature(feature)) {
                featureStateChange(m, feature, false);
            }
        }

        Mgr.i("Module:" + name + " registed.");
        return true;
    }

    public void featureStateChange(String feature, boolean enabled) {
        for (Module m : mModules.values()) {
            if (m.hasFeature(feature)) {
                featureStateChange(m, feature, enabled);
                return;
            }
        }
    }

    public void applyFeatures(Map<String, Boolean> map) {
        Mgr.i("applyFeatures in ");
        // SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (String key : map.keySet()) {
            boolean value = map.get(key);
            Mgr.i("applyFeatures in key=" + key + "value=" + value);
            // if (mSharedPreferences.getBoolean(key, true) != value) {
            // 	editor.putBoolean(key, value);
            featureStateChange(key, value);
            // }
        }
        // editor.commit();
    }

    private static void featureStateChange(Module m, String feature, boolean enabled) {
        Mgr.d("featureStateChange:Module:" + m.getName() + ", feature:" + feature + ", enabled:" + enabled);
        m.onFeatureStateChange(feature, enabled);
    }

    public Module getModule(String name) {
        if (TextUtils.isEmpty(name)) {
            Mgr.w("invalid module name.");
            return null;
        }

        return mModules.get(name);
    }

    public boolean isFeatureEnabled(String feature) {
        if (SystemModule.SYSTEM.equals(feature)) {
            return true;
        }

        return isSync(feature);
    }

    private boolean isSync(String key) {
        return mSharedPreferences.getBoolean(key, true);
    }

    private String[] getNonSyncArray() {
        ArrayList<String> result = new ArrayList<String>();
        for (Module module : mModules.values()) {
            for (String feature : module.getFeatures()) {
                if (!mSharedPreferences.getBoolean(feature, true)) {
                    result.add(feature);
                }
            }
        }
        return result.toArray(new String[0]);
    }

    private Map<String, Boolean> getSyncMap() {
        Map<String, Boolean> m = new HashMap<String, Boolean>();
        for (Module module : mModules.values()) {
            for (String feature : module.getFeatures()) {
                m.put(feature, mSharedPreferences.getBoolean(feature, true));
            }
        }

        return m;
    }

    void applyMode(int mode) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(MODE_SETTINGS_KEY, String.valueOf(mode));
        editor.commit();
    }

}
