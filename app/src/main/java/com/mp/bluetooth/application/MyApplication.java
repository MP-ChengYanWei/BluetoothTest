package com.mp.bluetooth.application;

import android.app.Application;

import com.mpen.bluetooth.init.MpenBluetooth;
import com.mpen.bluetooth.utils.FileUtil;
import com.mpen.bluetooth.utils.SPUtils;

public class MyApplication extends Application {

    public static int msgid = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        MpenBluetooth.getInstance().init(getApplicationContext());
        SPUtils.initContext(getApplicationContext());
        FileUtil.bluetoothDataWrite("****************************************APP启动****************************************");
    }

}
