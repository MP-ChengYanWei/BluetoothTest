package cn.ingenic.glasssync.transport.ext;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.InputStream;
import java.util.UUID;

import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.DefaultSyncManager.OnChannelCallBack;
import cn.ingenic.glasssync.LogTag;
import cn.ingenic.glasssync.SyncSerializable;
import cn.ingenic.glasssync.data.ProjoList;
import cn.ingenic.glasssync.services.SyncSerializableTools;
import cn.ingenic.glasssync.transport.TransportManager;

public class TransportManagerClient extends TransportManager {
    static final int PRO_VER = 1;
    public static final int WAIT_TIMEOUT = 0x1;

    private Handler mRetriveHandler;
    private PkgEncodingWorkspace mPkgEncode;
    private PkgDecodingWorkspace mPkgDecode;
    private BluetoothClientExt mClient;
    private BluetoothServerExt mServer;
    private Thread mWorkThread;
    private Handler mLinkHandler = new Handler(){
	    @Override
		public void handleMessage(Message msg) {
		switch(msg.what){
		case WAIT_TIMEOUT:
		    e("link timeout!");
		    mMgrHandler.sendEmptyMessage(DefaultSyncManager.MSG_CLIENT_LINK_TIMEOUT);
		    break;
		}
	    }
	};

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

	    @Override
		public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
		    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
		    if (BluetoothAdapter.STATE_ON == state) {
			SharedPreferences sp = mContext.getSharedPreferences(DefaultSyncManager.FILE_NAME, Context.MODE_PRIVATE);
			String address = sp.getString(DefaultSyncManager.UNIQUE_ADDRESS, "");
			if (BluetoothAdapter.checkBluetoothAddress(address)) {
			    mClient.start(address);
			}
		    } else if (BluetoothAdapter.STATE_OFF == state) {
			mClient.close();	
		    }
		}
	    }

	};

    public TransportManagerClient(Context context, String sysetmModuleName,
                                  Handler mgrHandler) {
	super(context, sysetmModuleName, mgrHandler);
	i("--TransportManagerExt in");
	init();
	mClient = new BluetoothClientExt(this, mRetriveHandler);

	Handler handler = new Handler() {

		@Override
		    public void handleMessage(Message msg) {
		    mCallback.onRetrive((SyncSerializable) msg.obj);
		}

	    };
	mPkgEncode = new PkgEncodingWorkspace(DefaultSyncManager.SUCCESS,
					      DefaultSyncManager.NO_CONNECTIVITY, DefaultSyncManager.UNKNOW);
	mPkgDecode = new PkgDecodingWorkspace(context, handler);
	ConnectionTimeoutManager.init(
				      context,
				      mPkgEncode,
				      mPkgDecode,
				      mgrHandler.obtainMessage(
							       DefaultSyncManager.MSG_TIME_OUT),
				      DefaultSyncManager.TIMEOUT);

	IntentFilter filter = new IntentFilter();
	filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	context.registerReceiver(mBluetoothReceiver, filter);
    }

    @Override
    public void send(SyncSerializable serializable) {
    	Log.d(TAG, "send serializable:" + serializable.getLength());
		mPkgEncode.push(serializable);
    }
    @Override
    public int getPkgEncodeSize(int type) {
	return mPkgEncode.size(type);
    }

    private Runnable mSendWork = new Runnable() {

	    @Override
		public void run() {
		i("SendWork thread start working...");
		try {
		    while (true) {
			Pkg pkg = mPkgEncode.poll();
			if (pkg == null) {
			    i("No pkg waiting to ben sent, quit sending thread");
			    return;
			}
			mClient.send(pkg);
		    }
		} catch (Exception e) {
		    e("Exception occurs, quit send thread.", e);
		    mPkgEncode.clear();
		}
	    }

	};

    @Override
	protected void init() {
	HandlerThread ht = new HandlerThread("retrive");
	ht.start();
	mRetriveHandler = new Handler(ht.getLooper()) {
		@Override
		    public void handleMessage(Message msg) {
		    Pkg pkg = (Pkg) msg.obj;
		    int type = pkg.getType();
		    if (type == Pkg.PKG || type == Pkg.CFG) {
			try {
			    mPkgDecode.push(pkg);
			} catch (Exception e) {
			    e("", e);
			}
		    } else if (type == Pkg.NEG) {
			i("--receive Neg ");
			Neg neg = (Neg) pkg;
			if (neg.isACK2()) {
			      //client receive neg
			    mLinkHandler.removeMessages(WAIT_TIMEOUT);
			    boolean isPass = neg.isPass();
			    i("--isPass:"+isPass);
			    if(isPass){
				try {
				    mClient.send(Neg.fromResponse(true,0));
				} catch (ProtocolException e) {
				    e("Protocol Error:", e);
				}
				Message msg1 = mMgrHandler.obtainMessage(DefaultSyncManager.MSG_STATE_CHANGE);
				msg1.arg1 = DefaultSyncManager.CONNECTED;
				msg1.sendToTarget();
			    }else{
				mMgrHandler.sendEmptyMessage(DefaultSyncManager.MSG_CLIENT_LINK_ERROR);
			    }
			}
		    } else {
			e("unknow Pkg TYPE:" + type);
			return;
		    }
		}  
	    };
    }

    @Override
	public void prepare(String address) {
	if (BluetoothAdapter.checkBluetoothAddress(address)) {
	    mClient.start(address);
	    SharedPreferences sp = mContext.getSharedPreferences(DefaultSyncManager.FILE_NAME,
								 Context.MODE_PRIVATE);
	    String store_addr = sp.getString(DefaultSyncManager.UNIQUE_ADDRESS, "");
	    
	      /*
	       *first connect store_addr is ""
	       * store_addr will be a valid value when app be killed after connect
	       */
	    if(store_addr.equals(""))
		mLinkHandler.sendEmptyMessageDelayed(WAIT_TIMEOUT, 20000); //delay 20s
	} else {
	    deInit();
	    mClient.close();

	    Message msg = mMgrHandler.obtainMessage(DefaultSyncManager.MSG_STATE_CHANGE);
	    msg.arg1 = DefaultSyncManager.IDLE;
	    msg.arg2 = 0;
	    msg.sendToTarget();
	}
    }

      /*
      ** callback from SppChannelServer or SppChannelChannel
      ** boolean sucess:indicate socket connect is ok or failed
      ** int arg2:no used now
      ** addr:is RemoteDevice MAC when socket is connected.
      */
    @Override
	public void notifyMgrState(boolean success, int arg2,String addr) {
	i("notifyMgrState success="+success);
	if (success) {
	    mPkgEncode.start();
	    if (mWorkThread == null) {
		mWorkThread = new Thread(mSendWork, "PollingThread");
		mWorkThread.start();
	    }
	} else {
	    mLinkHandler.removeMessages(WAIT_TIMEOUT);
	    mMgrHandler.sendEmptyMessage(DefaultSyncManager.MSG_CLIENT_SOCKET_DISCONNECT);
	    deInit();
	}
    }
    private void deInit(){
	i("-------deinit");
	mWorkThread = null;
	mPkgEncode.clear();
	mPkgDecode.clear();
	releaseWakeLock();

	Message msg = mMgrHandler.obtainMessage(DefaultSyncManager.MSG_STATE_CHANGE);
	msg.arg1 = DefaultSyncManager.IDLE;
	msg.arg2 = 0;
	msg.sendToTarget();
    }

    @Override
	public void sendBondResponse(boolean pass) {
	w("unimplemention sendBondResponse");
    }

    @Override
	public void request(ProjoList projoList) {
    	Log.d(TAG, "request");
		SystemClock.sleep(10);
		send(SyncSerializableTools.projoList2Serial(projoList));
    }

    @Override
	public void requestSync(ProjoList projoList) {
		Log.d(TAG, "requestSync");
		SystemClock.sleep(10);
		request(projoList);
    }

    @Override
	public void requestUUID(UUID uuid, ProjoList projoList) {
        Log.d(TAG, "requestUUID");
	SyncSerializable serial = SyncSerializableTools
	    .projoList2Serial(projoList);
	serial.getDescriptor().mUUID = uuid;
	serial.getDescriptor().mPri = SPEICAL;
	send(serial);
    }

    @Override
	public void sendFile(String module, String name, int length, InputStream in) {
	e("sendFile unimplement.");
    }

    @Override
	public void retriveFile(String module, String name, int length,
                            String address) {
	e("retriveFile unimplement.");
    }

    @Override
	public void createChannel(UUID uuid, OnChannelCallBack callback) {
	e("createChannel unimplement.");
    }

    @Override
	public boolean listenChannel(UUID uuid, OnChannelCallBack callback) {
	e("listenChannel unimplement.");
	return true;
    }

    @Override
	public void destoryChannel(UUID uuid) {
	e("destoryChannel unimplement.");
    }

    @Override
	public void closeFileChannel() {
	e("closeFileChannel unimplement.");
    }

    private static final String TAG = "<TRAN-C>";

    private static void i(String msg) {
	Log.i(LogTag.APP, TAG + msg);
    }

    private static void w(String msg) {
	Log.w(LogTag.APP, TAG + msg);
    }

    private static void e(String msg) {
	Log.e(LogTag.APP, TAG + msg);
    }
    private static void e(String msg, Throwable t) {
	Log.e(LogTag.APP, msg, t);
    }
}
