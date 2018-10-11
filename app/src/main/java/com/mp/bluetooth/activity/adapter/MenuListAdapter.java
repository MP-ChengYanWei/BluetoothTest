package com.mp.bluetooth.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mp.bluetooth.R;
import com.mp.bluetooth.activity.NewMainActivity;

import java.util.List;

/**
 * 菜单列表适配器
 * Created by cyw on 2018/10/9.
 */

public class MenuListAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;

    public MenuListAdapter(NewMainActivity newMainActivity, List<String> menuList) {
        this.context = newMainActivity;
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
            convertView = View.inflate(context, R.layout.item_menu, null);
        }
        TextView tvName = convertView.findViewById(R.id.tv_name);
        tvName.setText(data.get(position));
        return convertView;
    }
}
