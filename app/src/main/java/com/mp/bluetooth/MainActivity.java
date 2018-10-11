package com.mp.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.bluetooth.application.MyApplication;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.linuxbt.LinuxBluetooth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lvDevices;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private DevicesAdapter adapter;
    private boolean isConn = false;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取权限
        getPermission();

        initView();

        registerReceiver();
    }

    /**
     * 关联控件
     */
    private void initView() {
        getSupportActionBar().setTitle("未连接");
        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
        findViewById(R.id.btn_li).setOnClickListener(this);
        findViewById(R.id.bt_base).setOnClickListener(this);
        findViewById(R.id.btn_wifi).setOnClickListener(this);
        findViewById(R.id.btn_custom).setOnClickListener(this);

        lvDevices = findViewById(R.id.lv_devices);
        tvAddress = findViewById(R.id.tv_address);

        adapter = new DevicesAdapter(this, devices);
        lvDevices.setAdapter(adapter);
        lvDevices.setOnItemClickListener(this);

        // 搜索设备
//        BluetoothManager.getInstance().startDiscovery();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:
                BluetoothManager.getInstance().stopDiscovery();
                devices.clear();
                adapter.notifyDataSetChanged();
                BluetoothManager.getInstance().startDiscovery();
                break;
            case R.id.btn_test:
                if (!isConn) {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, TestBluetoothActivity.class));
                break;
            case R.id.btn_li:
                if (!isConn) {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, DataTestActivity.class));
                break;
            case R.id.bt_base:
                if (!isConn) {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, BindSuccessActivity.class));
                break;

            case R.id.btn_wifi:   // WIFI连接测试
                if (!isConn) {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, WifiSetActivity.class));
                break;

            case R.id.btn_custom:   // 参数自定义测试
                if (!isConn) {
                    Toast.makeText(this, "请先连接设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(this, CustomActivity.class));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isConn) {
            Toast.makeText(this, "当前已有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        BluetoothDevice device = devices.get(position);
        if (device.getName().contains(BluetoothManager.linuxName)) {
            //连接Linux笔
            BluetoothManager.getInstance().connect(BluetoothManager.deviceType.LINUX_BT_TYPE, device.getAddress());
        } else {
            //连接Android笔
            BluetoothManager.getInstance().connect(BluetoothManager.deviceType.ANDROID_BT_TYPE, device.getAddress());
        }
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索完成 的广播
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);// 找到设备
        intentFilter.addAction(BTConstants.APP_CONNECT_SUCCESS_ACTION);//蓝牙连接成功广播
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙连接状态改变
        intentFilter.addAction(BTConstants.GET_PEN_INFO_ACTION);//获取笔的信息的广播
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
                        LinuxBluetooth.addr = "";
                    }
                    tvAddress.setText("当前连接：" + LinuxBluetooth.addr);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    break;
                case BTConstants.APP_CONNECT_SUCCESS_ACTION:
                    Toast.makeText(MainActivity.this, "蓝牙连接成功", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle("已连接");
                    isConn = true;
                    SendRequestToPen.sendMsg("{\"data\":{\"active\":\"P\",\"msgid\":\"" + MyApplication.msgid++ + "\",\"text\":\"绑定成功\"},\"type\":1}");
                    tvAddress.setText("当前连接：" + LinuxBluetooth.addr);
                    break;
                case BTConstants.APP_CONNECT_ERROR_ACTION:
                    Toast.makeText(MainActivity.this, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case BTConstants.GET_PEN_INFO_ACTION:

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
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) {
                        return;
                    }
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING://正在配对
                            Log.e("Linux", "正在配对 -- " + device.getName());
                            Toast.makeText(MainActivity.this, "正在配对", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_BONDED://配对结束
                            Log.e("Linux", "配对完成 -- " + device.getName());
                            Toast.makeText(MainActivity.this, "配对成功", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_NONE://取消配对/未配对
                            Log.e("Linux", "配对失败 -- " + device.getName());
                            Toast.makeText(MainActivity.this, "配对失败", Toast.LENGTH_LONG).show();
                        default:
                            break;
                    }
                    break;

            }
        }

    };

    /**
     * 获取友盟需要的权限
     */
    private void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.WAKE_LOCK,
            };

            List<String> notPermissionList = new ArrayList<>();
            for (String permission : mPermissionList) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    notPermissionList.add(permission);
                }
            }
            String[] permissionArray = notPermissionList.toArray(new String[notPermissionList.size()]);
            if (permissionArray != null && permissionArray.length > 0) {
                ActivityCompat.requestPermissions(this, permissionArray, 123);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    long mill;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mill > 2000) {
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mill = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
