package com.mp.bluetooth.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.bean.WifiTestResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * WIFI测试结果列表
 * Created by cyw on 2018/10/9.
 */

public class WifiResultListAdapter extends BaseAdapter {

    private Context context;
    private List<WifiTestResult> data;

    public WifiResultListAdapter(Context context, List<WifiTestResult> menuList) {
        this.context = context;
        this.data = menuList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_wifi_result, null);
        }
        TextView tvPenId = convertView.findViewById(R.id.tv_pen_id);
        TextView tvWifiName = convertView.findViewById(R.id.tv_wifi_name);
        TextView tvWifiPwd = convertView.findViewById(R.id.tv_wifi_pwd);
        TextView tvData = convertView.findViewById(R.id.tv_data);
        TextView tvDate = convertView.findViewById(R.id.tv_date);

        WifiTestResult testResult = data.get(position);
        tvPenId.setText("笔ID：" + testResult.getFkPenId());
        tvWifiName.setText("WIFI名称：" + testResult.getName());
        tvWifiPwd.setText("WIFI密码：" + testResult.getPassword());
        tvData.setText("测试数据：" + testResult.getVerifyCode());
        try {
            Date createTime = testResult.getCreateTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tvDate.setText("创建时间：" + sdf.format(createTime));
        } catch (Exception e) {

        }
        return convertView;
    }
}
