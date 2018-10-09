package cn.ingenic.glasssync.transport.ext;

import android.os.Handler;
import android.os.Message;

import com.ingenic.spp.OnChannelListener;

import cn.ingenic.glasssync.LogTag.Client;
import cn.ingenic.glasssync.transport.TransportManager;
import cn.ingenic.glasssync.transport.transcompat.BluetoothCompat;

class BluetoothClientExt implements BluetoothChannelExt {
	//private final TransportStateMachineExt mStateMachine;
	private final TransportManager mTransportManager;
	private volatile boolean mClosed = true;

	private final Handler mRetrive;
    private final BluetoothCompat mClientCompat;
    private final OnChannelListener mConnListener;

    private class ConnListener implements OnChannelListener {
        public void onStateChanged(int state, String addr) {
            if (state == OnChannelListener.STATE_CONNECTED) {
		Client.w("OnChannelListener.STATE_CONNECTED");
		mTransportManager.notifyMgrState(true);
                doStartRead();
                mClosed = false;
            } else if (state == OnChannelListener.STATE_NONE) {
		Client.w("OnChannelListener.STATE_NONE");
		if (!mClosed) {
		    mTransportManager.notifyMgrState(false);
		}
            }
        }

        public void onWrite(byte[] buf, int len, int err) {
            //Do nothing.
        }

        public void onRead(byte[] buf, int err) {
	    if(err == OnChannelListener.TIMEOUT_READ){
		byte[] p = {'p','i','n','g','g','n','i','p'};
		Pkg pkg = new Pkg(p);
		try{
		    Client.w("send ping length="+p.length);
		    send(pkg);
		}catch (ProtocolException e){
		}
	    }
        }
	
    }

	BluetoothClientExt(/*TransportStateMachineExt stateMachine,*/TransportManager transportManager,
			final Handler retrive) {
	      //mStateMachine = stateMachine;
	    mTransportManager = transportManager;
	    mRetrive = retrive;
	    mConnListener = new ConnListener();
	    mClientCompat = BluetoothCompat.getClientCompat(mConnListener);
	}
	
	// static void notifyStateChange(int state,
	// 		TransportStateMachineExt stateMachine) {
	// 	Message msg = stateMachine
	// 			.obtainMessage(TransportStateMachineExt.MSG_STATE_CHANGE);
	// 	msg.arg1 = state;
	// 	msg.sendToTarget();
	// }

	// private void sendClientCloseMsg() {
	// 	if (!mClosed) {
	// 		notifyStateChange(
	// 				TransportStateMachineExt.C_IDLE, mStateMachine);
	// 	}
	// }

        private final boolean is_ping(byte[] b){
	    if(b.length != 8) return false;
	    if (b[0] == 'p' && b[1] == 'i' && b[2] == 'n' && b[3] == 'g'
		&& b[4] == 'g' && b[5] == 'n' && b[6] == 'i' && b[7] == 'p'){
		Client.w("retrive ping");
		return true;
	    }

	    return false;
	}

	void start(final String address) {
		if (!mClosed) {
			Client.e("Client already running.");
			return;
		}
        mClientCompat.connect(address);
    }
    private void doStartRead() {
		
		Thread thread = new Thread() {

			@Override
			public void run() {
				try {
					while (!mClosed) {
						Pkg pkg = BluetoothChannelExtTools.retrivePkg(mClientCompat, mRetrive);

						if(pkg == null)
							continue;
						  /*  }else if (pkg.getData().length > Pkg.BIG_LEN){
							Client.e("set mLastRetriveSec length:%d" + pkg.getData().length);
							mLastRetriveSec = System.currentTimeMillis() / 1000l;
						    }
						}*/
						Message msg = mRetrive.obtainMessage();
						msg.obj = pkg;
						msg.sendToTarget();
					}
				} catch (Exception e) {
					Client.e("client exception:" + e.getMessage());
					//sendClientCloseMsg();
				}
				Client.d("read thread quit.");
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
					Client.d("send ping");
					send(pkg);
				    }catch (ProtocolException e){
				    }
				}
				
				try {
				    Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			    }
			    Client.d("ping thread1 quit.");
			}
		};
		thread1.start();
*/
	}

	@Override
	public void send(Pkg pkg) throws ProtocolException {
		try {
			BluetoothChannelExtTools.send(pkg, mClientCompat);
		} catch (Exception e) {
			Client.e("send error:" + e.getMessage());
			mRetrive.removeMessages(BluetoothChannelExtTools.MsgSendAnyData);
			
		}

	}

	public void close() {
	    mClientCompat.disconnect();
	    mClosed = true;
	}
}
