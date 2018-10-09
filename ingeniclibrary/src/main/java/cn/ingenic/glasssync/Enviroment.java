package cn.ingenic.glasssync;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.UUID;

import cn.ingenic.glasssync.transport.BluetoothChannel;

public abstract class Enviroment {
	
	private static final String TAG = "Enviroment";
	
	public static interface EnviromentCallback {
		Enviroment createEnviroment();
	}
	
	public static abstract class ResourceManager {
		
		protected Context mContext;
		
		public ResourceManager(Context context) {
			mContext = context;
		}
		
		public abstract Notification getRetryFailedNotification();
		
		public abstract Toast getRetryToast(int reason);
	}
	
	private static Enviroment sInstance = null;
	
	protected final Context mContext;
	public ResourceManager mResMgr;
	
	public static Enviroment init(EnviromentCallback listener) {
		if (sInstance == null) {
			Log.i(TAG, "create Enviroment");
			sInstance = listener.createEnviroment();
		} else {
			Log.w(TAG, "enviroment already exists.");
		}
		
		return sInstance;
	}
	
	public static Enviroment getDefault() {
		if (sInstance == null) {
			throw new NullPointerException("Enviroment must be inited before getDefault().");
		}
		
		return sInstance;
	}
	
	
	public Enviroment(Context context) {
		mContext = context;
	}
	
	public ResourceManager getResourceManager() {
		return mResMgr;
	}
	
	public abstract boolean isWatch();
	
	
	public abstract UUID getUUID(int type, boolean remote);
	
	public boolean isMainChannel(UUID uuid, boolean remote) {
		if (uuid != null) {
			return uuid.equals(getUUID(BluetoothChannel.CUSTOM, remote))
					|| uuid.equals(getUUID(BluetoothChannel.SERVICE, remote));
		}
		return false;
	}
	
	public BluetoothChannel getAnotherMainChannel(UUID uuid, boolean remote, Map<UUID, BluetoothChannel> map) {
		if (uuid == null) {
			return null;
		}
		
		UUID custom = getUUID(BluetoothChannel.CUSTOM, remote);
		UUID service = getUUID(BluetoothChannel.SERVICE, remote);
		if (uuid.equals(custom)) {
			return map.get(service);
		} else if (uuid.equals(service)) {
			return map.get(custom);
		}
		
		return null;
	}
}
