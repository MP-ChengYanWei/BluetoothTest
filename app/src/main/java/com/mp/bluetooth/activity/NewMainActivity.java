package com.mp.bluetooth.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.adapter.MenuListAdapter;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.BluetoothManager;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试APP2.0主界面
 * Created by cyw on 2018/10/9.
 */

public class NewMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<String> menuList = new ArrayList<>();
    private TextView tvData;
    private ListView lvMenu;
    private MenuListAdapter adapter;
    private ScrollView scrollView;
    private boolean isResume = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        isResume = true;

        getPermission();
        initMenu();
        initView();
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        tvData.setText(DataController.getInstance().getData());
        scrollDown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    private void initMenu() {
        menuList.add("切换主程序");
        menuList.add("搜索设备");
        menuList.add("解除绑定");
        menuList.add("单条数据收发（短消息）");
        menuList.add("单条数据收发（长消息）");
        menuList.add("单条数据收发（验证）");
        menuList.add("批量数据收发（T/N长消息）");
        menuList.add("批量数据收发（显示成功）");
        menuList.add("WIFI扫描/连接");
        menuList.add("WIFI数据收发");
        menuList.add("蓝牙WIFI混合（蓝牙wifi同时收发）");
        menuList.add("蓝牙WIFI混合（蓝牙反复绑定解绑）");
    }

    private void initView() {
        tvData = findViewById(R.id.tv_data);
        lvMenu = findViewById(R.id.lv_menu);
        scrollView = findViewById(R.id.slv_data);

        adapter = new MenuListAdapter(this, menuList);
        lvMenu.setAdapter(adapter);
        lvMenu.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            // 搜索设备
            startActivity(new Intent(this, DiscoveryActivity.class));
        } else {
            if (BluetoothManager.isConnenct) {
                switch (position) {
                    case 0://切换测试程序
                        if (menuList.get(position).equals("切换主程序")) {
                            SendRequestToPen.switchModel("B");
                            menuList.set(position, "切换测试程序");
                        } else {
                            SendRequestToPen.switchModel("T");
                            menuList.set(position, "切换主程序");
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case 2://解绑
                        disConnect();
                        break;
                    case 3://单条数据收发（短消息）
                        Intent intent = new Intent(this, ShowTestResultActivity.class);
                        intent.putExtra("type", 3);
                        startActivity(intent);
                        break;
                    case 4://单条数据收发（长消息）
                        Intent intent4 = new Intent(this, ShowTestResultActivity.class);
                        intent4.putExtra("type", 4);
                        startActivity(intent4);
                        break;
                    case 5://单条数据收发（验证）
                        Intent intent5 = new Intent(this, ShowTestResultActivity.class);
                        intent5.putExtra("type", 5);
                        startActivity(intent5);
                        break;
                    case 6://批量数据收发（T/N长消息）
                        showConfigDialog();
                        break;
                    case 7://批量数据收发（显示成功）
                        showEachotherConfigDialog();
                        break;
                    case 8://WIFI扫描/连接
                        startActivity(new Intent(this, WifiConnActivity.class));
                        break;
                    case 9://WIFI数据收发
                        startActivity(new Intent(this, PenWifiTestActivity.class));
                        break;
                    case 10://蓝牙WIFI混合（蓝牙wifi同时收发）
                        startActivity(new Intent(this, BTAndWFActivity.class));
                        break;
                    case 11://蓝牙WIFI混合（蓝牙反复绑定解绑）
                        showBindConfigDialog();
                        break;
                }
            } else {
                Toast.makeText(this, "蓝牙未连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 显示配置对话框
     */
    private void showConfigDialog() {
        View view = View.inflate(this, R.layout.dialog_edit, null);
        final EditText et1 = view.findViewById(R.id.et_1);
        final EditText et2 = view.findViewById(R.id.et_2);

        et1.setHint("请输入时间间隔（微秒）");
        et2.setHint("请输入发送条数");

        new AlertDialog.Builder(this)
                .setTitle("请配置时间间隔及发送条数")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent6 = new Intent(NewMainActivity.this, ShowTestResultActivity.class);
                        intent6.putExtra("type", 6);
                        if (TextUtils.isEmpty(et1.getText().toString().trim()) || TextUtils.isEmpty(et2.getText().toString().trim())) {
                            Toast.makeText(NewMainActivity.this, "请输入完整配置", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent6.putExtra("cycle", Integer.parseInt(et1.getText().toString().trim()));
                        intent6.putExtra("count", Integer.parseInt(et2.getText().toString().trim()));
                        startActivity(intent6);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }


    /**
     * 显示配置对话框
     */
    private void showBindConfigDialog() {
        View view = View.inflate(this, R.layout.dialog_edit, null);
        final EditText et1 = view.findViewById(R.id.et_1);
        final EditText et2 = view.findViewById(R.id.et_2);

        et1.setHint("请输入断开重连次数");
        et2.setVisibility(View.GONE);

        new AlertDialog.Builder(this)
                .setTitle("请配置断开重连次数")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent11 = new Intent(NewMainActivity.this, BindTestActivity.class);
                        intent11.putExtra("type", 11);
                        if (TextUtils.isEmpty(et1.getText().toString().trim())) {
                            Toast.makeText(NewMainActivity.this, "请输入完整配置", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent11.putExtra("count", Integer.parseInt(et1.getText().toString().trim()));
                        startActivity(intent11);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    /**
     * 显示配置对话框
     */
    private void showEachotherConfigDialog() {
        View view = View.inflate(this, R.layout.dialog_edit, null);
        final EditText et1 = view.findViewById(R.id.et_1);
        final EditText et2 = view.findViewById(R.id.et_2);

        et1.setHint("请输入手机发送条数时间间隔（毫秒）");
        et2.setHint("请输入手机发送条数");

        new AlertDialog.Builder(this)
                .setTitle("请配置时间间隔及发送条数")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent7 = new Intent(NewMainActivity.this, ShowTestResultActivity.class);
                        intent7.putExtra("type", 7);
                        if (TextUtils.isEmpty(et1.getText().toString().trim()) || TextUtils.isEmpty(et2.getText().toString().trim())) {
                            Toast.makeText(NewMainActivity.this, "请输入完整配置", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent7.putExtra("cycle", Integer.parseInt(et1.getText().toString().trim()));
                        intent7.putExtra("count", Integer.parseInt(et2.getText().toString().trim()));
                        startActivity(intent7);
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    /**
     * 获取权限
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

    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//蓝牙搜索完成 的广播
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BTConstants.APP_CONNECT_SUCCESS_ACTION);//蓝牙连接成功广播
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙连接状态改变
        intentFilter.addAction(BTConstants.GET_PEN_INFO_ACTION);//获取笔的信息的广播
        intentFilter.addAction(BTConstants.SEND_DATA);//发送消息的广播
        intentFilter.addAction(BTConstants.RECEIVE_DATA);//收到消息的广播
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
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED://蓝牙搜索完成
                    break;
                case BTConstants.APP_CONNECT_SUCCESS_ACTION://连接成功
                    if (isResume) {
                        Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
                        SendRequestToPen.sendCfgMsg("T");
                        SendRequestToPen.sendMsg(1, "P", "绑定成功");
                    }
                    break;
                case BTConstants.APP_CONNECT_ERROR_ACTION://连接失败

                    break;
                case BTConstants.GET_PEN_INFO_ACTION://获取笔信息

                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) {
                        return;
                    }
                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDING://正在配对
                            Log.e("Linux", "正在配对 -- " + device.getName());
                            Toast.makeText(context, "正在配对", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_BONDED://配对结束
                            Log.e("Linux", "配对完成 -- " + device.getName());
                            Toast.makeText(context, "配对成功", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothDevice.BOND_NONE://取消配对/未配对
                            Log.e("Linux", "配对失败 -- " + device.getName());
                            Toast.makeText(context, "配对失败", Toast.LENGTH_LONG).show();
                        default:
                            break;
                    }
                    break;

                case BTConstants.SEND_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown();
                    break;
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        DataController.getInstance().clean();
    }

    /**
     * 断开蓝牙连接
     */
    private void disConnect() {
        SendRequestToPen.sendMsg(1, "P", "解绑成功");
        BluetoothManager.getInstance().onDeviceDisconnected();
    }

    private void scrollDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 50);
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
