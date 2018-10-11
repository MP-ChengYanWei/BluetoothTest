package com.mp.bluetooth.activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mp.bluetooth.R;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

/**
 * 蓝牙绑定 解绑测试界面
 * Created by cyw on 2018/10/11.
 */

public class BindTestActivity extends BaseActivity {

    private ScrollView scrollView;
    private TextView tvData;
    private int count;
    private int totalCount;//总次数
    private int disConnCount = 0;//断开次数
    private int connCount = 0;//连接次数
    private int failCount = 0;//失败次数
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_test);

        count = getIntent().getIntExtra("count", 1);
        totalCount = count;
        initView();
        registerReceiver();

        SendRequestToPen.getTestWifiList(count * 10);
        repeatConnPen();
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);
        tvData = findViewById(R.id.tv_data);
        tvResult = findViewById(R.id.tv_result);
    }

    private void repeatConnPen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BluetoothManager.getInstance().onDeviceDisconnected();
                count--;
                refreshResult();
            }
        }, 500);
    }

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.SEND_DATA);//发送消息的广播
        intentFilter.addAction(BTConstants.RECEIVE_DATA);//收到消息的广播

        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//蓝牙配对状态改变
        intentFilter.addAction(BTConstants.APP_CONNECT_SUCCESS_ACTION);//蓝牙连接成功广播
        intentFilter.addAction(BTConstants.APP_CONNECT_ERROR_ACTION);//蓝牙连接失败广播
        intentFilter.addAction(BTConstants.CONNECT_ING_ERROR_ACTION);//连接过程中的连接失败
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
                case BTConstants.SEND_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;

                case BTConstants.APP_CONNECT_SUCCESS_ACTION://连接成功
                    break;
                case BTConstants.APP_CONNECT_ERROR_ACTION://连接失败
                    break;
                case BTConstants.CONNECT_ING_ERROR_ACTION://连接失败
                    failCount++;
                    count--;
                    connPen();
                    refreshResult();
                    break;
                case BTConstants.APP_CONNECT_STATE_CHANGE_ACTION://蓝牙连接状态改变
                    boolean connect = intent.getBooleanExtra("connect", false);
                    if (connect) {
                        connCount++;
                        if (count > 0) {
                            BluetoothManager.getInstance().onDeviceDisconnected();
                            count--;
                        }
                    } else {
                        disConnCount++;
                        connPen();
                    }
                    refreshResult();
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) {
                        return;
                    }
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING://正在配对
                            Log.e("Linux", "正在配对 -- " + device.getName());
                            break;
                        case BluetoothDevice.BOND_BONDED://配对结束
                            Log.e("Linux", "配对完成 -- " + device.getName());
                            break;
                        case BluetoothDevice.BOND_NONE://取消配对/未配对
                            Log.e("Linux", "配对失败 -- " + device.getName());
                        default:
                            break;
                    }
                    break;
            }
        }

    };

    private void connPen() {
        BluetoothManager.getInstance().connect(BluetoothManager.getInstance().connBtType, BluetoothManager.DEVICE_ADDRESS);
    }

    private void refreshResult() {
        tvResult.setText("总次数：" + totalCount + "\n\n断开次数：" + disConnCount + "\n\n连接次数：" + connCount + "\n\n失败次数：" + failCount);
    }
}
