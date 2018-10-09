package cn.ingenic.glasssync;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemModule extends Module {

	public static final String SYSTEM = "SYSTEM";

	private static final String TAG = "M-SYS";
	private static final boolean V = true;
    private Context mContext;
      //move from DeviceModule
    public static final String FEATURE_UNBIND = "unbind";

	public SystemModule() {
//		super(SYSTEM);
	    super(SYSTEM, new String[]{FEATURE_UNBIND});
	}

	@Override
	protected void onCreate(Context context) {
	    mContext = context;
		if (V) {
			Log.d(TAG, "SystemModule created.");
		}

		if (DefaultSyncManager.isWatch()) {
			registService(RemoteChannelManagerService.DESPRITOR,
					new RemoteChannelManagerImpl());
		} else {
			registRemoteService(RemoteChannelManagerService.DESPRITOR,
					new RemoteBinderImpl(SYSTEM,
							RemoteChannelManagerService.DESPRITOR));
		}
	}

	@Override
	protected Transaction createTransaction() {
		return new SystemTransaction();
	}
	
	@Override
	protected void onFeatureStateChange(String feature, boolean enabled) {
	    Log.i("SystemModule","--received feature changed to "+enabled+", "+feature);
	    if (FEATURE_UNBIND.equals(feature) && enabled) {
		if(Enviroment.getDefault().isWatch() == false){
		      /*received unbind requst from glass*/
		    Log.i("SystemModule","sendbroadcast in ");
		    Intent intent = new Intent();
		    intent.setAction(DefaultSyncManager.RECEIVER_REQUST_UNBIND);
		    mContext.sendBroadcast(intent);
		}
	    }
	}
}
