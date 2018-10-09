package com.mp.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mp.bluetooth.application.MyApplication;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.DDBConnectNormal;

/**
 * 手机端蓝牙压测
 * Created by Mpen on 2018/8/31.
 */

public class DataTestActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static DDBConnectNormal ddbConnectNormal = new DDBConnectNormal();
    private int position = 1;
    private int index = 1;
    private TextView tvReceive;
    private TextView tvSend;

    private EditText etDataText, etDataType, etDataSize, etDataCount, etDataCycle, etDataParallels;
    private EditText etSendCount, etSendSpace;
    private RadioButton rbN;
    private RadioButton rbP;
    private RadioButton rbS;
    private RadioButton rbWS;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rbParams;
    private RadioButton rbText;
    private LinearLayout llParams;
    private Thread thread;
    private int msgCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_test);

        initView();
        registerReceiver();
    }

    private void initView() {
        getSupportActionBar().setTitle("已连接");
        etDataText = findViewById(R.id.et_text);

        rbN = findViewById(R.id.rb_n);
        rbP = findViewById(R.id.rb_p);
        rbS = findViewById(R.id.rb_s);
        rbWS = findViewById(R.id.rb_ws);
        rb1 = findViewById(R.id.rb_1);
        rb2 = findViewById(R.id.rb_2);
        rb3 = findViewById(R.id.rb_3);
        rb4 = findViewById(R.id.rb_4);
        rbParams = findViewById(R.id.rb_param);
        rbText = findViewById(R.id.rb_text);

        etDataType = findViewById(R.id.et_data_type);
        etDataSize = findViewById(R.id.et_size);
        etDataCount = findViewById(R.id.et_count);
        etDataCycle = findViewById(R.id.et_cycle);
        etDataParallels = findViewById(R.id.et_parallels);
        etSendCount = findViewById(R.id.et_send_count);
        etSendSpace = findViewById(R.id.et_send_space);


        llParams = findViewById(R.id.ll_params);


        RadioGroup rgParams = findViewById(R.id.rg_params);
        RadioGroup rgActive = findViewById(R.id.rg_active);
        rgParams.setOnCheckedChangeListener(this);
        rgActive.setOnCheckedChangeListener(this);


        tvReceive = findViewById(R.id.tv_receive);
        tvSend = findViewById(R.id.tv_send);

        findViewById(R.id.btn_send).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                msgCount = 0;
                break;
            case R.id.btn_send:
                buildMsg();
                break;
        }

    }

    private void buildMsg() {
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
        // data
        if (rbN.isChecked()) {
            sendOwnData.setActive("N");
        } else if (rbS.isChecked()) {
            sendOwnData.setActive("S");
        } else if (rbWS.isChecked()) {
            sendOwnData.setActive("WS");
        } else {
            sendOwnData.setActive("P");
        }
        if (rbParams.isChecked()) {
            String strType = etDataType.getText().toString().trim();
            String strSize = etDataSize.getText().toString().trim();
            String strCount = etDataCount.getText().toString().trim();
            String strCycle = etDataCycle.getText().toString().trim();
            String strParallels = etDataParallels.getText().toString().trim();
            try {
                sendData.setType(Integer.parseInt(strType));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sendData.setSize(Integer.parseInt(strSize));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sendData.setCount(Integer.parseInt(strCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sendData.setCycle(Integer.parseInt(strCycle));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sendData.setParallels(Integer.parseInt(strParallels));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String text = etDataText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                text = "null";
            }
            sendOwnData.setText(text);
        }
        sendOwnData.setConfig(sendData);
        sendDataModel.setData(sendOwnData);
//
        // 设置发送条数
        String count = etSendCount.getText().toString().trim();
        if (TextUtils.isEmpty(count)) {
            count = "1";
        }
        // 设置消息间隔
        String cycle = etSendSpace.getText().toString().trim();
        if (TextUtils.isEmpty(cycle)) {
            cycle = "0";
        }
        int msgCycle = Integer.parseInt(cycle);
        msgCount = Integer.parseInt(count);
        sendMsg(msgCycle, sendDataModel);
//        Gson gson = new Gson();
//        String msg = gson.toJson(sendDataModel);
//        sendMsg(msg);
    }

    /**
     * 发送数据
     */
    private void sendMsg(final long msgCycle, final SendDataModel sendDataModel) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(msgCycle);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (msgCount > 0) {
                        msgCount--;
                        sendDataModel.getData().setMsgid(MyApplication.msgid++ + "");
                        Gson gson = new Gson();
                        String msg = gson.toJson(sendDataModel);
                        msg = fillter(msg);
                        ddbConnectNormal.sendMsg(msg);
                        showSend(msg);
                    } else {
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * 显示发送的数据
     *
     * @param msg
     */
    private void showSend(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index % 10 == 0) {
                    tvSend.setText("");
                }
                tvSend.setText(index++ + ". " + msg + "\n\n" + tvSend.getText().toString());
            }
        });
    }

    /**
     * 发送数据
     */
    private void sendMsg(String msg) {
        msg = fillter(msg);
        ddbConnectNormal.sendMsg(msg);
        tvSend.setText(index++ + ". " + msg + "\n\n" + tvSend.getText().toString());
    }

    private String fillter(String msg) {
        msg = msg.replace("\"count\":0,", "");
        msg = msg.replace("\"cycle\":0,", "");
        msg = msg.replace("\"detect_num\":0,", "");
        msg = msg.replace("\"parallels\":0,", "");
        msg = msg.replace("\"size\":0,", "");
        msg = msg.replace("\"type\":0", "");
        msg = msg.replace("\"cfg\":{},", "");
        return msg;
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTConstants.APP_CONNECT_STATE_CHANGE_ACTION);//蓝牙联机状态改变
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
                    boolean connect = intent.getBooleanExtra("connect", false);
                    if (!connect) {
                        Toast.makeText(DataTestActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
                        getSupportActionBar().setTitle("连接断开了");
                    } else {
                        getSupportActionBar().setTitle("已连接");
                        Toast.makeText(DataTestActivity.this, "重新连接", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case BTConstants.RECEIVE_DATA:
                    String data = intent.getStringExtra("data");
                    if (position % 10 == 0) {
                        tvReceive.setText("");
                    }
                    tvReceive.setText(position++ + ". " + data + "\n\n" + tvReceive.getText().toString());
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_param:
                llParams.setVisibility(View.VISIBLE);
                etDataText.setVisibility(View.GONE);
                break;
            case R.id.rb_text:
                llParams.setVisibility(View.GONE);
                etDataText.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_s:
                rbParams.setChecked(true);
                rbText.setClickable(false);
                break;
            case R.id.rb_n:
            case R.id.rb_p:
            case R.id.rb_ws:
                rbText.setClickable(true);
                break;
        }
    }
}
