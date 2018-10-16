package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.adapter.WifiResultListAdapter;
import com.mp.bluetooth.activity.bean.NetworkResult;
import com.mp.bluetooth.activity.bean.PenId;
import com.mp.bluetooth.activity.bean.WifiTestResult;
import com.mp.sharedandroid.network.MPNetwork;
import com.mp.sharedandroid.network.MPNetworkListener;
import com.mp.sharedandroid.network.MPRequest;
import com.mp.sharedandroid.network.ResultParse;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔上的WIFI测试   笔与后台的交互
 * Created by cyw on 2018/10/11.
 */

public class PenWifiTestActivity extends BaseActivity {

    private List<WifiTestResult> wifiTestResultList = new ArrayList<>();
    private ScrollView scrollView;
    private TextView tvData;
    private String url = "https://test.mpen.com.cn/v1/pens/wifiTest?action=getDdbPenWifiTest&fkPenId=";
    private ListView lvWifiResult;
    private WifiResultListAdapter adapter;

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
        lvWifiResult = findViewById(R.id.lv_wifi_test_result);

        adapter = new WifiResultListAdapter(this, wifiTestResultList);
        lvWifiResult.setAdapter(adapter);
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
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        PenId model = gson.fromJson(data, PenId.class);
                        // 获取后台数据
                        String penid = model.getData().getCfg().getPenid();
                        getNetworkData(penid);
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    };

    private void getNetworkData(String penId) {
        Type type = new TypeToken<NetworkResult<List<WifiTestResult>>>() {
        }.getType();
        ResultParse resultParse = new ResultParse(type);
        MPRequest request = MPRequest.newRequest(MPRequest.Method.GET, MPRequest.DataFormat.JSON_FORMAT,
                url + penId, null, resultParse, null, new MPNetworkListener() {

                    @Override
                    public void onPreRequest(MPRequest request) {

                    }

                    @Override
                    public void onResponse(MPRequest request, Object obj, String strResponse) {
                        NetworkResult<List<WifiTestResult>> result = (NetworkResult<List<WifiTestResult>>) obj;
                        Log.e("ToT", strResponse);
                        if (result.isGood()) {
                            wifiTestResultList.clear();
                            wifiTestResultList.addAll(result.getData());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed(MPRequest request, String strResponse, FailedReason reason, String failureMessage) {
                        Log.e("ToT", strResponse);
                    }
                });

        MPNetwork.addRequest(request);
    }
}
