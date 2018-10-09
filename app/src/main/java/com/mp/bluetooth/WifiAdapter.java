package com.mp.bluetooth;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Mpen on 2018/9/4.
 */

public class WifiAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;

    public WifiAdapter(Context context, List<String> data) {
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
            convertView = View.inflate(context, R.layout.item_wifi, null);
        }
        TextView tvName = convertView.findViewById(R.id.tv_wifi_name);
        String name = data.get(position);
        tvName.setText(name);
        return convertView;
    }
}
