package com.mp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mpen on 2018/8/30.
 */

public class DevicesAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothDevice> data;

    public DevicesAdapter(Context context, List<BluetoothDevice> data) {
        this.context = context;
        this.data = data;
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
            convertView = View.inflate(context, R.layout.item_device, null);
        }
        BluetoothDevice device = data.get(position);
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvMac = convertView.findViewById(R.id.tv_mac);
        tvName.setText(device.getName());
        tvMac.setText("蓝牙地址： " + device.getAddress()
                + "\n是否配对： " + (device.getBondState() == 12 ? "已配对" : "未配对")
        );
        return convertView;
    }
}
