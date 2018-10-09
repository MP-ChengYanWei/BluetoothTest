package com.mpen.bluetooth.linuxbt;

import android.app.Notification;
import android.content.Context;
import android.widget.Toast;

import java.util.UUID;

import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.Enviroment;

/**
 * Created by LYH on 2017/10/26.
 * 继承 （君正）Enviroment
 */

public class AppEnviroment extends Enviroment {

    AppEnviroment(Context context) {
        super(context);
        mResMgr = new ResourceManager(context) {

            @SuppressWarnings("deprecation")
            @Override
            public Notification getRetryFailedNotification() {
                Notification.Builder builder = new Notification.Builder(mContext);
                builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
                builder.setContentTitle("同步超时");
                builder.setContentInfo("已长时间失去蓝牙连接能力");
                builder.setDefaults(Notification.DEFAULT_VIBRATE);
                Notification noti = builder.build();
                noti.flags |= Notification.FLAG_ONGOING_EVENT;
                return noti;
            }

            @Override
            public Toast getRetryToast(int reason) {
                String str = "UNKNOW";
                switch (reason) {
                    case DefaultSyncManager.CONNECTING:
                        str = "连接中...";
                        break;
                    case DefaultSyncManager.SUCCESS:
                        str = "连接成功";
                        break;
                    case DefaultSyncManager.NO_CONNECTIVITY:
                        str = "连接失败";
                        break;
                    case DefaultSyncManager.FEATURE_DISABLED:
                        throw new IllegalArgumentException(
                                "System module should ever not be disable.");
                    case DefaultSyncManager.NO_LOCKED_ADDRESS:
                        str = "失去远程设备";
                        break;
                }
                return Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
            }

        };
    }

    @Override
    public boolean isWatch() {
        return false;
    }

    @Override
    public UUID getUUID(int i, boolean b) {
        return null;
    }
}
