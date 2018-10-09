// ISyncService.aidl
package cn.ingenic.glasssync.services;

// Declare any non-default types here with import statements
import cn.ingenic.glasssync.services.SyncData;
import cn.ingenic.glasssync.services.IModuleCallback;

interface ISyncService {

	boolean isConnected();

	String getLockedAddress();

	boolean registModule(String name, IModuleCallback callback);

	boolean send(String module, in SyncData bundle);

	boolean sendCMD(String module, in SyncData bundle);

	void sendOnChannel(String module, in SyncData data, in ParcelUuid uuid);

	boolean sendFile(String module, in ParcelFileDescriptor des, String name, int length);

	boolean sendFileByPath(String module, in ParcelFileDescriptor des, String name, int length, String path);

	int getWaitingListSize(int type);

	void createChannel(String module, in ParcelUuid uuid);

	void destroyChannel(String module, in ParcelUuid uuid);
}
