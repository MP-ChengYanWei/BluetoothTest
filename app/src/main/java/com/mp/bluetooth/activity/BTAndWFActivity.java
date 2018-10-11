package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.bean.TestData;
import com.mp.bluetooth.activity.bean.WifiBean;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙和WIFI混合
 * Created by cyw on 2018/10/10.
 */

public class BTAndWFActivity extends AppCompatActivity {

    private List<String> wifiList = new ArrayList<>();
    private ScrollView scrollView;
    private TextView tvData;
    private TextView tvValidData;
    private TextView tvSendData;
    private ListView lvWifiList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_and_wifi);

        initView();
        registerReceiver();

        startTest();
    }

    private void startTest() {
        SendRequestToPen.sendMsg(3, "P", "123456");
        SendRequestToPen.getTestWifiList(3);
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);
        tvData = findViewById(R.id.tv_data);
        tvSendData = findViewById(R.id.tv_send_data);
        tvValidData = findViewById(R.id.tv_valid_data);

        lvWifiList = findViewById(R.id.lv_wifi_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiList);
        lvWifiList.setAdapter(adapter);
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
                    scrollDown();
                    break;
                case BTConstants.SEND_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();

                    // 数据展示
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        TestData testData = gson.fromJson(data, TestData.class);

                        if (!TextUtils.isEmpty(testData.getData().getText())) {
                            tvSendData.setText(testData.getData().getText());
                        }
                    } catch (Exception e) {

                    }
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();
                    // 数据展示
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        TestData testData = gson.fromJson(data, TestData.class);

                        if (!TextUtils.isEmpty(testData.getData().getText())) {
                            tvValidData.setText(testData.getData().getText());
                        }
                    } catch (Exception e) {

                    }

                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        WifiBean model = gson.fromJson(data, WifiBean.class);
                        List<String> wifi_list = model.getData().getCfg().getWifi_list();
                        wifiList.addAll(wifi_list);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                    break;
            }
        }

    };

    private void scrollDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 50);
    }
}
