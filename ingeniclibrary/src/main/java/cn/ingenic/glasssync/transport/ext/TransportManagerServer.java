package cn.ingenic.glasssync.transport.ext;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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

public class TransportManagerServer extends TransportManager {
	static final int PRO_VER = 1;

	private Handler mRetriveHandler;
	private PkgEncodingWorkspace mPkgEncode;
	private PkgDecodingWorkspace mPkgDecode;
        private BluetoothClientExt mClient;
        private BluetoothServerExt mServer;
	private Thread mWorkThread;

	private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

		@Override
		    public void onReceive(Context context, Intent intent) {
		    String action = intent.getAction();
		    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			if (BluetoothAdapter.STATE_ON == state) {
			    mServer.start();
			} else if (BluetoothAdapter.STATE_OFF == state) {
			    mServer.close();
			}
		    }
		}

	};

	public TransportManagerServer(Context context, String sysetmModuleName,
                                  Handler mgrHandler) {
	    super(context, sysetmModuleName, mgrHandler);
	    init();
	    mServer = new BluetoothServerExt(this, mRetriveHandler);
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

		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (BluetoothAdapter.STATE_ON == adapter.getState()) {
		    mServer.start();
		}
	}

    @Override
	public void send(SyncSerializable serializable) {
		mPkgEncode.push(serializable);
//		if (!mIsRunning) {
//			if (mWorkThread == null) {
//				d("restart the work thread.");
//				mWorkThread = new Thread(mSendWork);
//				mWorkThread.start();
//			} else {
//				v("waiting the work thread working.");
//			}
//		}
	}
    @Override
	public int getPkgEncodeSize(int type) {
	    return mPkgEncode.size(type);
	}

	private Runnable mSendWork = new Runnable() {

		@Override
		public void run() {
			d("SendWork thread start working...");
			try {
				while (true) {
					Pkg pkg = mPkgEncode.poll();
					if (pkg == null) {
						d("No pkg waiting to ben sent, quit sending thread");
						return;
					}
					mServer.send(pkg);
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
			  Neg neg = (Neg) pkg;
			  i("--receive Neg isack2="+neg.isACK2()+"--addr="+neg.getAddr());
			  if (neg.isACK2()) {
			      Message msg1 = mMgrHandler.obtainMessage(DefaultSyncManager.MSG_STATE_CHANGE);
			      msg1.arg1 = DefaultSyncManager.CONNECTED;
				//msg.arg2 = reBond;
			      msg1.obj = neg.getAddr();
			      msg1.sendToTarget();
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
	      //server no used
	}

      /*
       ** callback from SppChannelServer or SppChannelChannel
       ** boolean sucess:indicate socket connect is ok or failed
       ** int arg2:no used now
       ** addr:is RemoteDevice MAC when socket is connected.
      */
	@Override
	    public void notifyMgrState(boolean success, int arg2,String addr) {
		if (success) {
			mPkgEncode.start();
			if (mWorkThread == null) {
				mWorkThread = new Thread(mSendWork, "PollingThread");
				mWorkThread.start();
			} else {
				e("WorkThread should be null while notifying ConnectSuccessState.");
			}

			String oldAddr = DefaultSyncManager.getDefault().getLockedAddress();
			int reBond = oldAddr.equalsIgnoreCase(addr) ? 1:0;

			if(reBond == 1){
			    // mMgrHandler.sendEmptyMessage(DefaultSyncManager.MSG_CLEAR_ADDRESS);
			}

			if(!oldAddr.equals("") && reBond == 0){
			      /*has aleady bonded a addr*/
			    mMgrHandler.sendEmptyMessage(DefaultSyncManager.MSG_SERVER_MISMATCH_MAC);
			    try {
				i("--send false neg to client");
				mServer.send(Neg.fromResponse(false,Neg.FAIL_ADDRESS_MISMATCH));
			    } catch (ProtocolException e) {
				e("Protocol Error:", e);
			    }
			    deInit();
			}else{
			    try {
				i("--send true neg to client");
				mServer.send(Neg.fromResponse(true,0));
			    } catch (ProtocolException e) {
				e("Protocol Error:", e);
			    }
			}
		} else {
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
		send(SyncSerializableTools.projoList2Serial(projoList));
	}

	@Override
	public void requestSync(ProjoList projoList) {
		Log.d(TAG, "requestSync");
		request(projoList);
	}

	@Override
	public void requestUUID(UUID uuid, ProjoList projoList) {
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

	private static final String TAG = "<TRAN-S>";

	private static void i(String msg) {
		Log.i(LogTag.APP, TAG + msg);
	}

	private static void d(String msg) {
		Log.d(LogTag.APP, TAG + msg);
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
