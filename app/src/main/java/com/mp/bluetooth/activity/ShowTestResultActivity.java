package com.mp.bluetooth.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.bean.TestData;
import com.mp.bluetooth.activity.bean.TestResult;
import com.mpen.bluetooth.constant.BTConstants;
import com.mpen.bluetooth.controller.SendRequestToPen;
import com.mpen.bluetooth.utils.DataController;

/**
 * 测试结果展示界面
 * Created by cyw on 2018/10/10.
 */

public class ShowTestResultActivity extends BaseActivity implements View.OnClickListener {

    private ScrollView scrollView;
    private TextView tvData;
    private int type;
    private TextView tvPhoneCount, tvPhoneCycle, tvPhoneSize;
    private TextView tvPenCount, tvPenCycle, tvPenSize;
    private TextView tvValidData;
    private TextView tvSendData;
    private Handler handler = new Handler();

    private int position = 0;
    private int batchCount;
    private int batchCycle;
    private TextView tvResult;
    private int count;
    private TextView tvStop;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_show);

        type = getIntent().getIntExtra("type", 3);

        initView();
        registerReceiver();
        selectAction(type);

    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);
        tvData = findViewById(R.id.tv_data);
        tvResult = findViewById(R.id.tv_result);

        tvPhoneCount = findViewById(R.id.tv_phone_count);
        tvPhoneCycle = findViewById(R.id.tv_phone_cycle);
        tvPhoneSize = findViewById(R.id.tv_phone_size);

        tvSendData = findViewById(R.id.tv_send_data);

        tvPenCount = findViewById(R.id.tv_pen_count);
        tvPenCycle = findViewById(R.id.tv_pen_cycle);
        tvPenSize = findViewById(R.id.tv_pen_size);

        tvValidData = findViewById(R.id.tv_valid_data);

        tvStop = findViewById(R.id.tv_stop);
    }

    private void selectAction(int type) {
        switch (type) {
            case 3:
                SendRequestToPen.sendSRT(1, 50);
                break;
            case 4:
                SendRequestToPen.sendSRT(1, 5120);
                break;
            case 5:
                SendRequestToPen.sendMsg(3, "P", "123456");
                break;
            case 6:
                count = getIntent().getIntExtra("count", 1);
                SendRequestToPen.sendSRT(50, count, getIntent().getIntExtra("cycle", 0));
                break;
            case 7:
                tvStop.setVisibility(View.VISIBLE);
                tvStop.setOnClickListener(this);
                batchCount = getIntent().getIntExtra("count", 1);
                batchCycle = getIntent().getIntExtra("cycle", 0);
                count = batchCount;
                batchSend();
                break;
        }
    }

    private void batchSend() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                batchCount--;
                SendRequestToPen.sendSRT(3, 50);
                if (batchCount > 0) {
                    batchSend();
                }
            }
        }, batchCycle);
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
        handler.removeCallbacksAndMessages(null);
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
                    //
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        TestResult result = gson.fromJson(data, TestResult.class);

                        tvPhoneCount.setText("条数：" + result.getData().getCfg().getCount());
                        tvPhoneCycle.setText("间隔：" + result.getData().getCfg().getCycle());
                        tvPhoneSize.setText("长度：" + result.getData().getCfg().getSize());
                    } catch (Exception e) {

                    }
                    break;
                case BTConstants.RECEIVE_DATA:
                    tvData.setText(DataController.getInstance().getData());
                    scrollDown(scrollView);
                    // 数据展示
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        TestData testData = gson.fromJson(data, TestData.class);

                        if (!TextUtils.isEmpty(testData.getData().getText())) {
                            switch (type) {
                                case 6:
                                case 7:
                                    tvValidData.setText((++position) + ". " + testData.getData().getText() + "\n\n" + tvValidData.getText().toString());
                                    break;
                                default:
                                    tvValidData.setText(testData.getData().getText());
                            }
                        }
                    } catch (Exception e) {

                    }
                    //
                    try {
                        String data = intent.getStringExtra("data");
                        Gson gson = new Gson();
                        TestResult result = gson.fromJson(data, TestResult.class);

                        tvPenCount.setText("条数：" + result.getData().getCfg().getCount());
                        tvPenCycle.setText("间隔：" + result.getData().getCfg().getCycle());
                        tvPenSize.setText("长度：" + result.getData().getCfg().getSize());
                    } catch (Exception e) {

                    }

                    //测试结果
                    switch (type) {
                        case 3:
                        case 4:
                            if (tvPhoneSize.getText().toString().equals(tvPenSize.getText().toString())) {
                                testSuccess();
                            }
                            break;
                        case 5:
                            if (tvSendData.getText().toString().equals(tvValidData.getText().toString())) {
                                testSuccess();
                            }
                            break;
                        case 6:
                            if (position == count) {
                                testSuccess();
                            }
                            break;
                        case 7:
                            tvResult.setText("测试结果：\n\n预期发送" + count + "次\n发送间隔" + batchCycle + "毫秒\n实际发送" + (count - batchCount) + "次\n接收消息" + position + "次");
                            break;
                    }
                    break;
            }
        }

    };

    private void testSuccess() {
        tvResult.setText("测试通过");
        tvResult.setTextColor(Color.GREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stop:
                handler.removeCallbacksAndMessages(null);
                batchCount = 0;
                tvStop.setText("已停止发送");
                break;
        }
    }
}
