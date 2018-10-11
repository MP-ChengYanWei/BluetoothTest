package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mp.bluetooth.R;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

/**
 * 笔上的WIFI测试   笔与后台的交互
 * Created by cyw on 2018/10/11.
 */

public class PenWifiTestActivity extends BaseActivity {

    private ScrollView scrollView;
    private TextView tvData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pen_wifi_test);

        initView();
        registerReceiver();
        sendAction();
    }

    private void sendAction() {
        SendRequestToPen.sendActive("IP");
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);
        tvData = findViewById(R.id.tv_data);
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.SEND_DATA);//发送消息的广播
        intentFilter.addAction(BTConstants.RECEIVE_DATA);//收到消息的广播
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
                case BTConstants.APP_CONNECT_STATE_CHANGE_ACTION://连接状态改变
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;
                case BTConstants.SEND_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;
            }
        }
    };
}
