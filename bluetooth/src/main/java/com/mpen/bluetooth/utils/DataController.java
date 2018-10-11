package com.mpen.bluetooth.utils;

/**
 * 数据控制 蓝牙测试使用
 * Created by cyw on 2018/10/9.
 */

public class DataController {

    private volatile static DataController INSTANCE;
    private String data = "";
    private final StringBuilder sb;

    private DataController() {
        sb = new StringBuilder();
    }

    public static DataController getInstance() {
        if (INSTANCE == null) {
            synchronized (DataController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataController();
                }
            }
        }
        return INSTANCE;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    /**
     * 追加数据
     */
    public String appendData(String str) {
//        sb.delete(0,sb.length());
        return data = sb.append(str).append("\n\n").toString();
    }

    public void clean() {
        data = null;
        INSTANCE = null;
    }
}
