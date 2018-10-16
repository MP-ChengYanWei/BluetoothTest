package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mp.bluetooth.R;
import com.mp.bluetooth.WifiAdapter;
import com.mp.bluetooth.activity.bean.WifiBean;
import com.mp.bluetooth.activity.bean.WifiConnResult;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

import java.util.ArrayList;
import java.util.List;

/**
 * WIFI连接界面
 * Created by cyw on 2018/10/10.
 */

public class WifiConnActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private List<String> wifiList = new ArrayList<>();
    private List<String> connList = new ArrayList<>();// wifi连接列表
    private TextView tvData;
    private ScrollView scrollView;
    private ListView lvWifiList;
    private WifiAdapter wifiListAdapter;
    private EditText etWifiName;
    private EditText etWifiPwd;
    private TextView tvAddWifi;
    private TextView tvConnWifi;
    private ListView lvConnList;
    private ArrayAdapter<String> connAdapter;
    private boolean isWifiConnIng = false;
    private TextView tvRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_conn);

        initView();
        registerReceiver();
        getWifiList();
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);
        tvData = findViewById(R.id.tv_data);
        lvWifiList = findViewById(R.id.lv_wifi_list);
        etWifiName = findViewById(R.id.et_wifi_name);
        etWifiPwd = findViewById(R.id.et_wifi_pwd);
        tvAddWifi = findViewById(R.id.tv_add_wifi);
        tvConnWifi = findViewById(R.id.tv_conn_wifi);

        wifiListAdapter = new WifiAdapter(this, wifiList);
        lvWifiList.setAdapter(wifiListAdapter);
        lvWifiList.setOnItemClickListener(this);

        tvAddWifi.setOnClickListener(this);
        tvConnWifi.setOnClickListener(this);

        lvConnList = findViewById(R.id.lv_conn_list);
        connAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connList);
        lvConnList.setAdapter(connAdapter);

        tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(this);
    }

    private void getWifiList() {
        wifiList.clear();
        wifiListAdapter.notifyDataSetChanged();
        SendRequestToPen.getTestWifiList(2);
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
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        WifiBean model = gson.fromJson(data, WifiBean.class);
                        List<String> wifi_list = model.getData().getCfg().getWifi_list();
                        wifiList.addAll(wifi_list);
                        wifiListAdapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        WifiConnResult model = gson.fromJson(data, WifiConnResult.class);
                        if (model != null && !connList.isEmpty()) {
                            connWifi();
                        } else {
                            if (isWifiConnIng) {
                                isWifiConnIng = false;
                                Toast.makeText(context, "WIFI测试完成", Toast.LENGTH_LONG).show();
                            }
                        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etWifiName.setText(wifiList.get(position));
        etWifiPwd.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_wifi:
                addWifi();
                break;
            case R.id.tv_conn_wifi:
//                connWifi();
                connAllWifi();
                break;
            case R.id.tv_refresh:
                getWifiList();
                break;
        }
    }

    private void connWifi() {
        if (connList.isEmpty()) {
            Toast.makeText(this, "请添加WIFI信息", Toast.LENGTH_SHORT).show();
            return;
        }
        isWifiConnIng = true;
        SendRequestToPen.connWifi(connList.remove(0));
        connAdapter.notifyDataSetChanged();
    }

    private void connAllWifi() {
        if (connList.isEmpty()) {
            Toast.makeText(this, "请添加WIFI信息", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < connList.size(); i++) {
            sb.append(connList.get(i));
            if (i != connList.size() - 1) {
                sb.append(",");
            }
        }
        SendRequestToPen.connWifi(sb.toString());
        connList.clear();
        connAdapter.notifyDataSetChanged();
    }

    private void addWifi() {
        if (TextUtils.isEmpty(etWifiName.getText())) {
            Toast.makeText(this, "请输入WIFI名", Toast.LENGTH_SHORT).show();
            return;
        }
        connList.add("{\"name\":\"" + etWifiName.getText().toString() + "\"" + ",\"password\":\"" + etWifiPwd.getText().toString() + "\"" + "}");
        connAdapter.notifyDataSetChanged();

        etWifiName.setText("");
        etWifiPwd.setText("");
    }
}
