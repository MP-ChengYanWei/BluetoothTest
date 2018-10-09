package com.mp.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mp.bluetooth.application.MyApplication;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mpen on 2018/9/4.
 */

public class WifiSetActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private PopupWindow popupWindow;
    private WifiAdapter adapter;
    //    private List<DataBean> wifiList = new ArrayList<>();
    private List<String> wifiList = new ArrayList<>();
    private RadioButton rbWD;
    private RadioButton rbWC;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private EditText etDetectNum;
    private EditText etWifiName;
    private EditText etWifiPwd;

    private List<WifiData> wifis = new ArrayList<>();
    private LinearLayout llWifiConn;
    private TextView tvWifi;

    private boolean isGetWifi = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);

        initView();
        regReceiver();
    }

    private void initView() {
        getSupportActionBar().setTitle("已连接");

        rbWD = findViewById(R.id.rb_wd);
        rbWC = findViewById(R.id.rb_wc);
        RadioGroup rgActive = findViewById(R.id.rg_active);
        rgActive.setOnCheckedChangeListener(this);
        rb1 = findViewById(R.id.rb_1);
        rb2 = findViewById(R.id.rb_2);
        rb3 = findViewById(R.id.rb_3);
        rb4 = findViewById(R.id.rb_4);
        tvWifi = findViewById(R.id.tv_wifi);
        llWifiConn = findViewById(R.id.ll_wifi_conn);
        etDetectNum = findViewById(R.id.et_detect_num);
        etWifiName = findViewById(R.id.et_wifi_name);
        etWifiPwd = findViewById(R.id.et_wifi_pwd);
        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_get_wifi).setOnClickListener(this);


        adapter = new WifiAdapter(this, wifiList);
    }

//    private void initView() {
//        etWifiName = findViewById(R.id.et_wifi_name);
//        etWifiName.setOnClickListener(this);
//        findViewById(R.id.btn_send).setOnClickListener(this);
//        adapter = new WifiAdapter(this, wifiList);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendData();
                break;
            case R.id.btn_add:
                addWifi();
                hideSoftKeyboard();
                break;
            case R.id.btn_get_wifi:
                hideSoftKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (wifiList.isEmpty()) {
                            isGetWifi = true;
                            SendRequestToPen.sendMsg("{\"data\":{\"active\":\"WD\",\"cfg\":{\"detect_num\":1},\"msgid\":\"" + MyApplication.msgid++ + "\"},\"type\":1}");
                        } else {
                            showWifiList();
                        }
                    }
                }, 50);
                break;
//            case R.id.et_wifi_name:
////                if (wifiList.isEmpty()) {
//////                    SendRequestToPen.getWIFIList();
////                } else {
//////                    showWifiList();
////                }
////                break;
////            case R.id.btn_send:
////                HashMap<String, Object> hashMap = new HashMap<>();
////                hashMap.put("name", etWifiName.getText().toString());
////                hashMap.put("password", etPwd.getText().toString());
////                SendRequestToPen.setWIFI(hashMap);
////                break;
        }
    }

    private void addWifi() {
        String wifiName = etWifiName.getText().toString();
        String wifiPwd = etWifiPwd.getText().toString();

        if (TextUtils.isEmpty(wifiName)) {
            Toast.makeText(this, "请输入要连接的WIFI名称", Toast.LENGTH_SHORT).show();
            return;
        }
        WifiData wifiData = new WifiData();
        wifiData.setName(wifiName);
        wifiData.setPassword(wifiPwd);
        wifis.add(wifiData);
        etWifiName.setText("");
        etWifiPwd.setText("");
        tvWifi.setText(wifis.toString());
    }

    /**
     * 发送数据
     */
    private void sendData() {
        // 构建要发送的消息
        SendDataModel sendDataModel = new SendDataModel();
        SendOwnData sendOwnData = new SendOwnData();
        SendData sendData = new SendData();
        int msgType;
        if (rb1.isChecked()) {
            msgType = 1;
        } else if (rb2.isChecked()) {
            msgType = 2;
        } else if (rb3.isChecked()) {
            msgType = 3;
        } else {
            msgType = 4;
        }
        sendDataModel.setType(msgType);

        if (rbWD.isChecked()) {
            sendOwnData.setActive("WD");
            sendOwnData.setMsgid(MyApplication.msgid++ + "");
            String strNum = etDetectNum.getText().toString().trim();
            try {
                sendData.setDetect_num(Integer.parseInt(strNum));
            } catch (Exception e) {
                sendData.setDetect_num(1);
            }
        } else {
            sendOwnData.setActive("WC");
            if (wifis.isEmpty()) {
                Toast.makeText(this, "WIFI列表为空", Toast.LENGTH_SHORT).show();
                return;
            }
            List<WifiData> temp = new ArrayList<>();
            for (WifiData wifi : wifis) {
                temp.add(wifi);
                sendData.setWifi_data(temp);
                sendOwnData.setMsgid(MyApplication.msgid++ + "");
                sendOwnData.setConfig(sendData);
                sendDataModel.setData(sendOwnData);
                Gson gson = new Gson();
                String msg = gson.toJson(sendDataModel);
                sendMsg(msg);
                temp.clear();
            }
            return;
        }

        sendOwnData.setConfig(sendData);
        sendDataModel.setData(sendOwnData);
        Gson gson = new Gson();
        String msg = gson.toJson(sendDataModel);
        sendMsg(msg);
    }

    /**
     * 发送数据
     */
    private void sendMsg(String msg) {
        msg = msg.replace("\"count\":0,", "");
        msg = msg.replace("\"cycle\":0,", "");
        msg = msg.replace("\"detect_num\":0,", "");
        msg = msg.replace("\"parallels\":0,", "");
        msg = msg.replace("\"size\":0,", "");
        msg = msg.replace("\"type\":0", "");
        msg = msg.replace(",}", "}");
        msg = msg.replace("{,", "{");
        SendRequestToPen.sendMsg(msg);
    }

    //接收笔里消息的广播
    private void regReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);
        intentFilter.addAction(BTConstants.GET_WIFI_LIST_ACTION);
        intentFilter.addAction(BTConstants.RECEIVE_DATA);// 接收到的数据
        registerReceiver(receiver, intentFilter);
    }

    //接收笔里消息的广播
    private BroadcastReceiver receiver = new BroadcastReceiver() {
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
                        Toast.makeText(WifiSetActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("连接断开了");
                    } else {
                        getSupportActionBar().setTitle("已连接");
                        Toast.makeText(WifiSetActivity.this, "重新连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BTConstants.RECEIVE_DATA:
                    String receiveData = intent.getStringExtra("data");

                    if (isGetWifi) {
                        try {
                            Gson gson = new Gson();
                            ReceiveData model = gson.fromJson(receiveData, ReceiveData.class);
                            List<String> wifi_list = model.getData().getData().getCfg().getWifi_list();
                            wifiList.addAll(wifi_list);
                            adapter.notifyDataSetChanged();
                            showWifiList();
                            isGetWifi = false;
                        } catch (Exception e) {

                        }
                    }
                    break;
                case BTConstants.GET_WIFI_LIST_ACTION:
                    String penWifiInfo = intent.getStringExtra("wifiInfo");
                    Gson gson = new Gson();
                    WifiBean wifiBean = gson.fromJson(penWifiInfo, WifiBean.class);
                    List<DataBean> data = wifiBean.getData();
//                    wifiList.addAll(data);
                    adapter.notifyDataSetChanged();
//                showWifiList();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_wc:
                etDetectNum.setVisibility(View.GONE);
                llWifiConn.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_wd:
                etDetectNum.setVisibility(View.VISIBLE);
                llWifiConn.setVisibility(View.GONE);
                break;
        }
    }

    private void showWifiList() {
        if (popupWindow == null) {
            View view = View.inflate(this, R.layout.module_pop_list, null);
            ListView listView = view.findViewById(R.id.lv_pop_list);
            listView.setAdapter(adapter);
            popupWindow = new PopupWindow(view, etWifiName.getWidth(), etWifiName.getHeight() * 6, true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    etWifiName.setText(wifiList.get(i));
                    popupWindow.dismiss();
                }
            });
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            popupWindow.update();
        }
        popupWindow.showAsDropDown(etWifiName);
    }

    class WifiBean {

        /**
         * data : [{"connected":"1","level":"-43","name":"48.whaty.com","save":"1","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-49","name":"diandubi","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-61","name":"du的iMac","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-60","name":"chanpin_WLAN_2_4","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-62","name":"3_@费@#￥*8_8@","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-65","name":"DIRECT-ypM2070 Series","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-63","name":"47.whaty.com","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-63","name":"46.whaty.com","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-78","name":"Xiaomi_A745","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-68","name":"Powercore_North","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-71","name":"HW-3D","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-75","name":"111111","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-75","name":"360免费WiFi-A5","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-81","name":"4g-huangheyin","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-76","name":"43.whaty.com","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-73","name":"cj_whaty","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-76","name":"TP-LINK_B510","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-80","name":"Rebooting","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-77","name":"2d_test","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-83","name":"chaofengling.whaty.com","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-77","name":"TP-LINK_E500D2","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-82","name":"45.whaty.com","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-81","name":"4g-chaofengling","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-83","name":"TP-LINK-XY","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-74","name":"Xiaomi_62E5","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-80","name":"hw-3E","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-85","name":"HP-Print-1F-LaserJet Pro MFP","save":"0","type":"NONE"},{"connected":"0","level":"-81","name":"island-300F30","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-82","name":"whaty_huiyishi","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-83","name":"4g-bangongshi2","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-80","name":"Xiaomi_1E0C","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-86","name":"HUAWEI-7333","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-87","name":"allcorehatress-bgq+","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-79","name":"4g-wumaci","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-77","name":"wei","save":"0","type":"WPA2_PSK"},{"connected":"0","level":"-87","name":"numbersales","save":"0","type":"WPA_PSK/WPA2_PSK"},{"connected":"0","level":"-86","name":"4g-bangongshi1","save":"0","type":"WPA_PSK/WPA2_PSK"}]
         * ret : 0
         * type : 0
         */

        private int ret;
        private int type;
        private List<DataBean> data;

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

    }

    public class DataBean {
        /**
         * connected : 1
         * level : -43
         * name : 48.whaty.com
         * save : 1
         * type : WPA_PSK/WPA2_PSK
         */

        private String connected;
        private String level;
        private String name;
        private String save;
        private String type;

        public String getConnected() {
            return connected;
        }

        public void setConnected(String connected) {
            this.connected = connected;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSave() {
            return save;
        }

        public void setSave(String save) {
            this.save = save;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
