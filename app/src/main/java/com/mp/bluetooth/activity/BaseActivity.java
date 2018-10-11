package com.mp.bluetooth.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;

/**
 * 基类
 * Created by cyw on 2018/10/11.
 */

public class BaseActivity extends AppCompatActivity {

    protected void scrollDown(final ScrollView scrollView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 50);
    }
}
