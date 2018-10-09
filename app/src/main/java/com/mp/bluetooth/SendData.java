package com.mp.bluetooth;

import java.util.List;

/**
 * Created by Mpen on 2018/9/5.
 */

public class SendData {

    private int type;
    private int count;
    private int size;
    private int cycle;
    private int parallels;  // 并行数
    private int detect_num;  // WIFI探测次数
    private List<WifiData> wifi_data;
    private List<String> wifi_list; //笔上接收的WIFI名列表

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getParallels() {
        return parallels;
    }

    public void setParallels(int parallels) {
        this.parallels = parallels;
    }

    public int getDetect_num() {
        return detect_num;
    }

    public void setDetect_num(int detect_num) {
        this.detect_num = detect_num;
    }

    public List<WifiData> getWifi_data() {
        return wifi_data;
    }

    public void setWifi_data(List<WifiData> wifi_data) {
        this.wifi_data = wifi_data;
    }

    public List<String> getWifi_list() {
        return wifi_list;
    }

    public void setWifi_list(List<String> wifi_list) {
        this.wifi_list = wifi_list;
    }
}
