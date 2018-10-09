package cn.ingenic.glasssync.transport.ext;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;

import com.ingenic.spp.OnChannelListener;

import java.io.IOException;

import cn.ingenic.glasssync.Enviroment;
import cn.ingenic.glasssync.LogTag;
import cn.ingenic.glasssync.LogTag.Server;
import cn.ingenic.glasssync.transport.TransportManager;
import cn.ingenic.glasssync.transport.transcompat.BluetoothCompat;

class BluetoothServerExt implements BluetoothChannelExt {
	private final BluetoothCompat mServerCompat;
    private Object mClientLock = new Object();
	private Object mClientCloseLock=new Object();
	private volatile boolean mClosed = true;
	//private final TransportStateMachineExt mStateMachine;
	private final TransportManager mTransportManager;
	private final Handler mRetrive;
    private final OnChannelListener mConnListener;
  
    private class ConnListener implements OnChannelListener {
        public void onStateChanged(int state, String addr) {
            if (state == OnChannelListener.STATE_CONNECTED) {
		  //BluetoothClientExt.notifyStateChange(TransportStateMachineExt.S_CONNECTED, mStateMachine);
		mTransportManager.notifyMgrState(true,addr);
                doStartRead();
            } else if (state == OnChannelListener.STATE_NONE) {
		  //sendClientCloseMsg();
		if (!mClosed) {
		    mTransportManager.notifyMgrState(false,addr);
		}
            }
        }
        public void onWrite(byte[] buf, int len, int err) {
        }
        public void onRead(byte[] buf, int err) {
        }

    }

	public BluetoothServerExt(/*TransportStateMachineExt stateMachine,*/TransportManager transportManager,
			final Handler retrive) {
	      //mStateMachine = stateMachine;
	    mTransportManager = transportManager;
		mRetrive = retrive;
        mConnListener = new ConnListener();
        mServerCompat = BluetoothCompat.getServerCompat(mConnListener);
	}

      private final boolean is_ping(byte[] b){
	  if(b.length != 8) return false;
	    if (b[0] == 'p' && b[1] == 'i' && b[2] == 'n' && b[3] == 'g'
		&& b[4] == 'g' && b[5] == 'n' && b[6] == 'i' && b[7] == 'p'){
		Server.i("receive ping");
		return true;
	    }

	    return false;
	}

	void start() {
		if (!mClosed) {
			Server.d("Server already running.");
//			close();
			return;
		}
		mClosed = false;
        mServerCompat.start();
    }	
	private void doStartRead() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				Server.i("server started.");
			/*	mClosed = false;
				mLastRetriveSec = 0;*/
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				Enviroment env = Enviroment.getDefault();
				try {
                    synchronized (mClientLock) {
			while (!mClosed) {
			    Pkg pkg = BluetoothChannelExtTools.retrivePkg(mServerCompat, mRetrive);
				if(pkg == null)
				    continue;
				    if (pkg.getType() == Pkg.PKG){
					byte[] data = pkg.getData();
					if (is_ping(data)){
					    Server.i("retrive ping ");
					    continue;
					}
					    }
					if (pkg instanceof Neg) {
					    Neg neg = (Neg) pkg;
						neg.setAddr(mServerCompat.getAddr());
						    }
					    
					    Message msg = mRetrive.obtainMessage();
						msg.obj = pkg;
						    msg.sendToTarget();
							}
			    }
			Server.d("current client quit.");
			    } catch (IOException e) {
				    Server.e("Exception occurs:" + e.getMessage());
					LogTag.printExp(LogTag.SERVER, e);
					      //sendClientCloseMsg();
					    } catch (ProtocolException ep) {
				    Server.e("protocol exception:" + ep.getMessage());
					}
				    Server.i("server end.");
					}
		};
		thread.start();
/*
		Thread thread1 = new Thread() {
			@Override
			public void run() {
			    try {
				Thread.sleep(2000);
			    } catch (InterruptedException e) {
			    }

			    while (!mClosed){
				if (System.currentTimeMillis() / 1000l - 10 < mLastRetriveSec){
				    byte[] p = new byte[8];
				    p[0]='p';p[1]='i';p[2]='n';p[3]='g';
				    p[4]='g';p[5]='n';p[6]='i';p[7]='p';
				    Pkg pkg = new Pkg(p);
				    try{
					Server.d("send ping");
					send(pkg);
				    }catch (ProtocolException e){
				    }
				}
				
				try {
				    Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			    }
			    Server.d("ping thread1 quit.");
			}
		};
		thread1.start();
*/
	}

	public void send(Pkg pkg) throws ProtocolException {
		try {
			BluetoothChannelExtTools.send(pkg, mServerCompat);
		} catch (Exception e) {
			Server.e("send error:" + e.getMessage());
			mRetrive.removeMessages(BluetoothChannelExtTools.MsgSendAnyData);
		}
	}

	// protected void sendClientCloseMsg() {
	// 	if (!mClosed) {
	// 		//close();
	// 		BluetoothClientExt.notifyStateChange(
	// 				TransportStateMachineExt.S_IDLE, mStateMachine);
	// 	}
	// }

	public void close() {
		if (mClosed) {
			return;
		}

		Server.d("shutdown the server");
		    mClosed = true;
		    mServerCompat.stop();
	}
}