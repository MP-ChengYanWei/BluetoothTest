package com.mp.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;

/**
 * Created by cyw on 2018/8/30.
 */

public class BindSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDevice;
    private int position = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_success);

        initView();

        initData();

        registerReceiver();
    }

    private void initView() {
        getSupportActionBar().setTitle("已连接");
        tvDevice = findViewById(R.id.tv_device);
        findViewById(R.id.type0).setOnClickListener(this);
        findViewById(R.id.type1).setOnClickListener(this);
        findViewById(R.id.type2).setOnClickListener(this);
        findViewById(R.id.type3).setOnClickListener(this);
        findViewById(R.id.type4).setOnClickListener(this);
        findViewById(R.id.type5).setOnClickListener(this);
        findViewById(R.id.type7).setOnClickListener(this);
        findViewById(R.id.type8).setOnClickListener(this);
        findViewById(R.id.type29).setOnClickListener(this);
    }

    private void initData() {

    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙联机状态改变
        intentFilter.addAction(BTConstants.RECEIVE_DATA);//获取笔的信息的广播
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
                    boolean connect = intent.getBooleanExtra("connect", false);
                    if (!connect) {
                        Toast.makeText(BindSuccessActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("连接断开了");
                    } else {
                        getSupportActionBar().setTitle("已连接");
                        Toast.makeText(BindSuccessActivity.this, "重新连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BTConstants.RECEIVE_DATA:
                    String data = intent.getStringExtra("data");
                    tvDevice.setText(position++ + ". " + data + "\n\n" + tvDevice.getText().toString());
                    break;
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type0:
                SendRequestToPen.getWIFIList();
                break;
            case R.id.type1:

                break;
            case R.id.type2:
                SendRequestToPen.getAllDownLoads();
                break;
            case R.id.type3:
                break;
            case R.id.type4:
                break;
            case R.id.type5:
                break;
            case R.id.type7:
                break;
            case R.id.type8:
                SendRequestToPen.getPenInfo("屏幕昵称", "12346567865");
                break;
            case R.id.type29:
                SendRequestToPen.playBindVoice();
                break;
        }

    }
}
