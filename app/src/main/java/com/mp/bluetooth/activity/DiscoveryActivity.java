package com.mp.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mp.bluetooth.DevicesAdapter;
import com.mp.bluetooth.R;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.utils.DataController;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索笔界面
 * Created by cyw on 2018/10/9.
 */

public class DiscoveryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Context context;

    private ListView lvDevices;
    private DevicesAdapter adapter;
    private List<BluetoothDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        context = this;

        initView();
        registerReceiver();
        discoveryDevices();
    }

    private void discoveryDevices() {
        BluetoothManager.getInstance().startDiscovery();
    }

    private void initView() {
        lvDevices = findViewById(R.id.lv_devices);
        adapter = new DevicesAdapter(this, devices);
        lvDevices.setAdapter(adapter);
        lvDevices.setOnItemClickListener(this);
        findViewById(R.id.tv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devices.clear();
                discoveryDevices();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = devices.get(position);
        if (device.getName().contains(BluetoothManager.linuxName)) {
            //连接Linux笔
            BluetoothManager.getInstance().connect(BluetoothManager.deviceType.LINUX_BT_TYPE, device.getAddress());
        } else {
            //连接Android笔
            BluetoothManager.getInstance().connect(BluetoothManager.deviceType.ANDROID_BT_TYPE, device.getAddress());
        }
        finish();
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索完成 的广播
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);// 找到设备
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙连接状态改变
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
                case BTConstants.APP_CONNECT_STATE_CHANGE_ACTION://连接状态改变

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED://蓝牙搜索完成
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (scanDevice == null || scanDevice.getName() == null) {
                        return;
                    }
                    if (scanDevice.getName().contains("MPEN") && !devices.contains(scanDevice)) {
                        devices.add(scanDevice);
                        adapter.notifyDataSetChanged();
                    }
                    break;

            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothManager.getInstance().stopDiscovery();
        unregisterReceiver(receiver);
    }
}
