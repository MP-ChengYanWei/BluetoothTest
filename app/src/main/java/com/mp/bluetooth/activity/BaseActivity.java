package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;

import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;

/**
 * 基类
 * Created by cyw on 2018/10/11.
 */

public class BaseActivity extends AppCompatActivity {

    protected void scrollDown(final ScrollView scrollView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 50);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
        if (BluetoothManager.isConnenct) {
            getSupportActionBar().setTitle("已连接：" + BluetoothManager.DEVICE_ADDRESS);
        } else {
            getSupportActionBar().setTitle("未连接");
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙连接状态改变
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case BTConstants.APP_CONNECT_STATE_CHANGE_ACTION://蓝牙连接状态改变
                    boolean connect = intent.getBooleanExtra("connect", false);
                    if (connect) {
                        getSupportActionBar().setTitle("已连接：" + BluetoothManager.DEVICE_ADDRESS);
                    } else {
                        getSupportActionBar().setTitle("连接断开了");
                    }
                    break;
            }
        }

    };

}
