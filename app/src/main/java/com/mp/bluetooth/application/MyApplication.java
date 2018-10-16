package com.mp.bluetooth.application;

import android.app.Application;
import android.content.Context;

import com.mp.sharedandroid.network.MPNetwork;
import com.mpen.bluetooth.init.MpenBluetooth;
import com.mpen.bluetooth.utils.FileUtil;
import com.mpen.bluetooth.utils.SPUtils;

public class MyApplication extends Application {

    public static Context application;

    public static int msgid = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        SPUtils.initContext(this);
        MPNetwork.initContext(this);

        MpenBluetooth.getInstance().init(getApplicationContext());
        FileUtil.bluetoothDataWrite("****************************************APP启动****************************************");
    }

}
