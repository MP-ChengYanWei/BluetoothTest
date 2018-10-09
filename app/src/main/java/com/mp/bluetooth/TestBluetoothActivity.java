package com.mp.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mp.bluetooth.application.MyApplication;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.PenUtils;

/**
 * Created by Mpen on 2018/8/30.
 */

public class TestBluetoothActivity extends AppCompatActivity implements View.OnClickListener {

    private int unBindId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        registerReceiver();
        initView();
    }

    private void initView() {
        getSupportActionBar().setTitle("已连接");
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_0:
                unBindId = MyApplication.msgid++;
                SendRequestToPen.sendTextMsg(unBindId, "解除绑定");
                break;
            case R.id.btn_1: // 单条数据收发
                startActivity(new Intent(this, CustomActivity.class));
                break;
            case R.id.btn_2: // 批量数据收发
                startActivity(new Intent(this, CustomActivity.class));
                break;
            case R.id.btn_3: // WIFI扫描连接
                startActivity(new Intent(this, WifiSetActivity.class));
                break;
            case R.id.btn_4: // 蓝牙WIFI混合
                startActivity(new Intent(this, CustomActivity.class));
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private boolean isConn = false;

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.APP_CONNECT_SUCCESS_ACTION);//蓝牙连接成功广播
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙连接状态改变
        intentFilter.addAction(BTConstants.RECEIVE_DATA);// 接收到的数据
        registerReceiver(receiver, intentFilter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case BTConstants.APP_CONNECT_STATE_CHANGE_ACTION:
                    isConn = intent.getBooleanExtra("connect", false);
                    if (isConn) {
                        getSupportActionBar().setTitle("已连接");
                    } else {
                        getSupportActionBar().setTitle("连接已断开");
                        finish();
                    }
                    break;
                case BTConstants.APP_CONNECT_SUCCESS_ACTION:
                    getSupportActionBar().setTitle("已连接");
                    isConn = true;
                    break;
                case BTConstants.RECEIVE_DATA:
                    String data = intent.getStringExtra("data");
                    if (data.contains("\"msgid\":\"" + unBindId + "\"")) {
                        disConnect();
                    }
                    break;

            }
        }

    };

    /**
     * 断开蓝牙连接
     */
    private void disConnect() {
        SendRequestToPen.UnbindPen();
        PenUtils.removePenInfo();
        BluetoothManager.isConnenct = false;
        BluetoothManager.getInstance().onDeviceDisconnected();
        Toast.makeText(this, "解绑成功", Toast.LENGTH_SHORT).show();
    }
}
